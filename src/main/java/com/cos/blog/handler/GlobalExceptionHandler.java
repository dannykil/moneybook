package com.cos.blog.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import com.cos.blog.dto.ResponseDto;

@ControllerAdvice // 어디에서든 Exception이 발생하면 이쪽으로 오게하는 애노테이션
@RestController
public class GlobalExceptionHandler {
	
	//@ExceptionHandler(value=IllegalArgumentException.class)
	@ExceptionHandler(value=Exception.class) // 모든 Exception을 받고싶은 경우
	//public String handleArgumentException(IllegalArgumentException e) {
	//public String handleArgumentException(Exception e) {	
	public ResponseDto<String> handleArgumentException(Exception e) {
		
		//return "<h1>" + e.getMessage() + "</h1>";
		return new ResponseDto<String>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()); // 500 
	}
}
