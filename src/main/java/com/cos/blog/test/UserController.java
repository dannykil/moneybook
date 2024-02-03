package com.cos.blog.test;

import org.springframework.web.bind.annotation.GetMapping;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
public class UserController {
	
	@GetMapping("/api")
	public String lombokTest() {	
		
		return "Swagger";
	}
}