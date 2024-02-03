package com.cos.blog.test;

//import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

//import springfox.documentation.swagger2.annotations.EnableSwagger2;

// 사용자가 요청 -> 응답(HTML파일)
// @Controller

// 사용자가 요청 -> 응답(Data)
//@SpringBootApplication
//@EnableSwagger2
@RestController
public class httpControllerTest {
	
	private static final String TAG = "httpControllerTest : ";
	
	//@GetMapping("/http/lombok")
	@GetMapping("/http/lombok")
	public String lombokTest() {
		//Member m = new Member(1, "ssar", "1234", "email");		
		Member m = Member.builder().username("ssar").password("1234").email("ssar@nate.com").build();
		System.out.println(TAG + "getter : " + m.getUsername());
		m.setUsername("cos");
		System.out.println(TAG + "setter : " + m.getUsername());
		
		return "lombok test 완료2";
	}
	
	// 인터넷 브라우저 요청은 무조건 get 요청밖에 할 수 없다(post, put, delete는 405에러)
	//http://localhost:8080/http/get (select)
	@GetMapping("/http/get")
	// 하나하나 받을 때
	// public String getTest(@RequestParam int id, @RequestParam String username) {
	// 한번에 받을 때
	public String getTest(Member m) { //id=1&username=ssar&password=1234&email=ssar@nate.com
		
		return "get 요청 : " + m.getId() + ", " + m.getUsername() + ", " + m.getPassword() + ", " + m.getEmail();
	}
	
	//http://localhost:8080/http/post (insert)
	@PostMapping("/http/post") //text/plain, application/json
	//public String postTest(Member m) {
	public String postTest(@RequestBody Member m) { // MessageConverter (스프링부트) 클래스가 매핑해준다
		//return "post 요청";
		return "post 요청 : " + m.getId() + ", " + m.getUsername() + ", " + m.getPassword() + ", " + m.getEmail();
	}
	
	//http://localhost:8080/http/put (update)
	@PutMapping("/http/put")
	public String putTest(@RequestBody Member m) {
		return "put 요청 : " + m.getId() + ", " + m.getUsername() + ", " + m.getPassword() + ", " + m.getEmail();
	}
	
	//http://localhost:8080/http/delete (delete)
	@DeleteMapping("/http/delete")
	public String deleteTest() {
		return "delete 요청";
	}
}
