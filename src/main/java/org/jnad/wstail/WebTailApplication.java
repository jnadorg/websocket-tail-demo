package org.jnad.wstail;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * Hello world!
 *
 */

@SpringBootApplication
public class WebTailApplication{
	public static void main(String[] args){
		new SpringApplicationBuilder(WebTailApplication.class).web(true).run(args);

	}

}
