package org.jnad.wstail.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import org.jnad.wstail.scoket.LogTailWebSocket;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class TailfLoggerService{
	public static boolean isopen = false;
	public static String runpath = "";

	private static AtomicInteger lognum = new AtomicInteger();
	private static LoadingCache<Integer, String> repository = CacheBuilder.newBuilder().maximumSize(200_000)// 20w一个按2k算400m
			.build(new CacheLoader<Integer, String>(){
				public String load(Integer key){
					String info = "-";
					return info;
				}
			});

	public static void addMessage(String msg){
		repository.put(lognum.incrementAndGet(), msg);
	}

	public static String getMessage(int index) throws InterruptedException, ExecutionException{
		while(true){
			if(index < lognum.get()){
				Thread.sleep(300);
				continue;
			}
			return repository.get(index);
		}
	}

	public static boolean openTail(final String path){
		if(isopen){
			return false;
		}

		new Thread(() -> {
			try{
				// String pathx = path.replace("_","/");
				System.out.println("tail -f " + path);
				runpath = path;
				Process process = Runtime.getRuntime().exec("tail -f " + path);
				BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
				String line;
				isopen = true;
				while((line = reader.readLine()) != null){
					// 将实时日志通过WebSocket发送给客户端，给每一行添加一个HTML换行
					// TailfLoggerService.addMessage(line + "<br>");
					LogTailWebSocket.sendInfo(line + "<br>");
					System.out.println(line);
					if(!isopen){ // 被关闭了
						System.out.println("我被关了,但没变强");
						break;
					}
				}
			} catch(IOException e1){
				e1.printStackTrace();
				System.out.println(e1.getMessage());
				isopen = false;
			}
		}).start();
		return true;
	}

}
