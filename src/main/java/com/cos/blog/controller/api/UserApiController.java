package com.cos.blog.controller.api;

import java.util.List;

// import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.cos.blog.dto.ResponseDto;
import com.cos.blog.model.User;
import com.cos.blog.service.UserService;

// 이 url은 앱에서도 쓸 수 있으며
// data만 return하기 때문에 RestController
@RestController 
public class UserApiController {

	// DI할 수 있다.
	// 왜냐하면 스프링이 컴포넌트 스캔할 때 @Service 클래스를 보는 순간 Bean에 등록해서 메모리에 올린다.
	// 우리는 DI받아서(@Autowired) 사용하면 된다.
	@Autowired
	private UserService userService;	

	@Autowired
	private AuthenticationManager authenticationManager;
	
	
	// #################################
	@GetMapping("/user")
	public List<User> findAll() {				
		
		return userService.findAll();
	}
	// #################################
	
	
	// 세션객체는 스프링 컨테이너가 bean으로 등록해서 가지고 있기 때문에 필요하면 DI해서 사용할 수 있다.
	//	@Autowired
	//	private HttpSession session;

	@PostMapping("/auth/joinProc")
	public ResponseDto<Integer> save(@RequestBody User user) { 
		// username, password
		// 세션은 스프링에서 컨트롤러의 매개변수로 받을 수 있다.

		System.out.println("UserApiController : save 호출됨");
		//return 1;
		// 여기서 실제로 DB에 insert하고 아래에서 return이 되어야 한다.

		//user.setRole(RoleType.USER);
		//int result = userService.회원가입(user);
		userService.회원가입(user);
		// Jackson이라는 라이브러리가 자바 Object를 JSON으로 변환해서 return해준다.
		return new ResponseDto<Integer>(HttpStatus.OK.value(), 1); 
	}

	// 로그인은 스피링 시큐리티로 진행할 것이기 때문에 이부분(전통적인 로그인 방식)은 주석처리
	//	@PostMapping("/api/user/login")
	//	public ResponseDto<Integer> login(@RequestBody User user, HttpSession session){
	//	// public ResponseDto<Integer> login(@RequestBody User user){ //@Autowired해서 사용할 수 있다.
	//		System.out.println("UserApiController : login 호출됨");
	//		User principal = userService.로그인(user); // principal 접근주체
	//		
	//		if(principal != null) {
	//			session.setAttribute("principal", principal);
	//		}
	//		
	//		return new ResponseDto<Integer>(HttpStatus.OK.value(), 1);
	//	}

	@PutMapping("/user")
	public ResponseDto<Integer> update(@RequestBody User user) { 
		// key=value, x-www-form-urlencoded
		// JSON데이터를 받고싶으면 @RequestBody를 반드시 적어야 한다.

		userService.회원수정(user);

		// 현재 이 시점은 트랜잭션이 종료되면서 DB값은 변경됐지만 세션값은 변경되지 않은 상태.
		// 우리가 직접 세션값을 변경해줘야함.
		// pricipal만 날리면 된다. 토큰이 pricipal을 통해서 Authentication객체를 만들어준다.
		//		Authentication authentication = 
		//				new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
		//		SecurityContext securityContext = SecurityContextHolder.getContext();
		//		securityContext.setAuthentication(authentication);		
		//		session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);
		// 위에 강제로 세션 내 Authentication를 내가(소스에서) 강제로 만드는 것은 현재 불가능한 상태(과거에는 됐음)


		// AuthenticationManager에 접근해서 로그인을 강제로 해서 Authentication을 만들면
		// 자동으로 컨텍스트에 넣어주는 방법

		// 세션등록
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);

		return new ResponseDto<Integer>(HttpStatus.OK.value(), 1);
	}
}

// Process
// 1) id와 pw를 필터가 낚아채서 토큰을 만든다.
// 2) 그리고 Authentication객체를 만들기 위해 토큰을 AuthenticationManager에게 던진다.
// 3) AuthenticationManager는 DB에 해당정보로 데이터가 있는지 확인하는데 이때 pw는 Encripted된 상태로 비교한다.
// 4) 이 때 데이터가 있으면 Authentication객체를 만들어서 세션에 저장한다.
// 5) 이 세션에 시큐리티 컨텍스트에는 User오브젝트를 저장하지 못하기 때문에 Authentication객체만 저장할 수 있다.
// * 세션안에 Authentication이 저장될 수 있는 시큐리티 컨테스트 공간이 있다.
// 이 과정은 간단히 말하면 Authentication객체를 만들어서 세션에 저장하기 위한 프로세스이다.
// 이것이 왜 DB값을 가져와서 세션업데이틑하는 것과 연관이 있는가?
// > 강제로 세션을 만들려면 이 로직을 강제로 타야한다.
// 내가 Authentication객체를 만들어서 세션에 넣으면된다!
