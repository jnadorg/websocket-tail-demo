package org.jnad.wstail.controller;

import org.jnad.wstail.service.TailfLoggerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

@RequestMapping("/log")
@RestController
public class LogController{
	Logger logger = LoggerFactory.getLogger(this.getClass());

	@RequestMapping(value = "/open", method = RequestMethod.GET)
	public String list(@RequestParam("path") String path){
		String result = "" + TailfLoggerService.openTail(path);
		return result;
	}

	@RequestMapping(value = "/close", method = RequestMethod.GET)
	public String list(){
		TailfLoggerService.isopen = false;
		String result = "" + true;
		return result;
	}

	@RequestMapping(value = "/status", method = RequestMethod.GET)
	public String status(){
		JSONObject jo = new JSONObject();
		jo.put("status", TailfLoggerService.isopen);
		jo.put("path", TailfLoggerService.runpath);
		return jo.toJSONString();
	}
}
