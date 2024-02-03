package com.cos.blog.test;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// 파일을 return할 것이다.
// RestController가 return "문자열"에서 문자열만을 리턴한다는 것과 다르다
@Controller
public class TempControllerTest {
	
	// http://localhost:8000/blog/temp/home
	@GetMapping("/temp/home")
	public String tempHome() {
		System.out.println("tempHome()");
		
		// 파일을 리턴할 때 기본경로가 src/main/resources/static인데
		// 리턴명을 /home.html로 해줘야 같이 붙여서 읽을 수 있따.
		// 풀경로 : src/main/resources/static/home.html
		return "/home.html";
		
		// 우리는 jsp를 쓸건데 스프링부트는 기본적으로 jsp를 지원하지 않는다.
		// = 정상적으로 동작하지 않기 때문에 
		// 1) pom.xml에 jsp설정을 해줘야한다(jsp 템플릿 엔진)
		// 2) 기본경로가 static으로 되어있기 때문에 jsp을 제대로 인식할 수 없다 = 정적파일을 놓는 경로
		// = 브라우저가 인식하는 파일만 놓아야한다.
		// 3) 
	}
	
	@GetMapping("/temp/img")
	public String tempImg() {
		
		return "/a.png";
	}
	
	// jsp파일은 정적인 파일이 아닌 동적인 파일(컴파일이 일어남)이기 때문에 static에서 인식을 못한다.
	// /main폴더 안에 /WEB-INF/views폴더를 생성해서 그 안에 파일을 만들어야 한다.
	// 폴더를 생성하고 application.yml 파일 내에 prefix를 주석해제 해야한다.
	@GetMapping("/temp/jsp")
	public String tempJsp() {
		
		// prefix: /WEB-INF/views/ : return의 왼쪽(앞)에 온다.
		// surfix: .jsp            : return의 오른쪽(뒤)에 온다.
		// 풀네임  : /WEB-INF/views/test.jsp
		// return값에서 확장자를 지워야한다.
		// application.yml 파일을 수정한 이후로 static폴더는 못읽고 있다. 이럴경우 정적파일은 어떻게 하는가?
		return "test";
	}
}
