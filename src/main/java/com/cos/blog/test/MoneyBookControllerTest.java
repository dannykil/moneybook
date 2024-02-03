package com.cos.blog.test;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

//@Controller
// 스프링이 com.cos.blog 패키지 이하를 스캔해서 모든 파일을 메모리에 new하는 것이 아닌,
// 특정 애노테이션이 붙어있는 클래스 파일만을 new해서 (IoC) 스프링 컨테이너에서 관리한다.
// 스프링이 메모리에 띄워진다.
// 현재 위 컨트롤러는 아래 RestController로 바꾼 상태.
@RestController
public class MoneyBookControllerTest {
	
	@GetMapping("/test/hello")
	public String hello() {
		return "<h1>hello spring boot</h1>";
	}
}
