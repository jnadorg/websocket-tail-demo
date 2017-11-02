package org.jnad.wstail.scoket;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.springframework.stereotype.Component;

@ServerEndpoint(value = "/log")
@Component
public class LogTailWebSocket{
	// 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
	private static int onlineCount = 0;

	// concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
	private static CopyOnWriteArraySet<LogTailWebSocket> webSocketSet = new CopyOnWriteArraySet<LogTailWebSocket>();

	// 与某个客户端的连接会话，需要通过它来给客户端发送数据
	private Session session;

	/**
	 * 连接建立成功调用的方法
	 */
	@OnOpen
	public void onOpen(Session session){
		this.session = session;
		webSocketSet.add(this); // 加入set中
		System.out.println("有新连接加入！id:" +session.getId());
	}

	/**
	 * 连接关闭调用的方法
	 */
	@OnClose
	public void onClose(){
		webSocketSet.remove(this); // 从set中删除
		System.out.println("有一连接关闭！id:" +session.getId());
	}

	/**
	 * 收到客户端消息后调用的方法
	 *
	 * @param message
	 *            客户端发送过来的消息
	 */
	@OnMessage
	public void onMessage(String message, Session session){

		// 群发消息
		for(LogTailWebSocket item: webSocketSet){
			try{
				item.sendMessage(message);
			} catch(IOException e){
				e.printStackTrace();
			}
		}
	}

	/**
	 * 发生错误时调用
	 */
	@OnError
	public void onError(Session session, Throwable error){
		System.out.println("发生错误");
		error.printStackTrace();
	}

	/*
	 * /** 群发自定义消息
	 */

	public void sendMessage(String message) throws IOException{
		this.session.getBasicRemote().sendText(message);
	}

	public static void sendInfo(String message) throws IOException{
		for(LogTailWebSocket item: webSocketSet){
			try{
				item.sendMessage(message);
			} catch(IOException e){
				continue;
			}
		}
	}

	public static synchronized int getOnlineCount(){
		return onlineCount;
	}

	public static synchronized void addOnlineCount(){
		LogTailWebSocket.onlineCount++;
	}

	public static synchronized void subOnlineCount(){
		LogTailWebSocket.onlineCount--;
	}
}
