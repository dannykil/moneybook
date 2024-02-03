package com.cos.blog.controller;

//import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.cos.blog.model.KakaoProfile;
import com.cos.blog.model.OAuthToken;
import com.cos.blog.model.User;
import com.cos.blog.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

// /auth라는 경로를 붙이는 이유
// 인증이 안된 사용자들이 출입할 수 있는 경로를 /auth/**라고 되어있는 경로만 허용(인증이 필요없는 부분만 접근가능하도록)
// 그냥 주소가 '/'이면 index.jsp 허용
// static 이하에 있는(resource파일 등) /js/**, /css/**, /image/** 

@Controller
public class UserController {
	
	// application.yml에 있는 값을 주입받는다.
	@Value("${cos.key}")
	private String cosKey;
	
	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UserService userService;

	@GetMapping("/auth/joinForm")
	public String joinForm() {

		return "user/joinForm";
	}

	@GetMapping("/auth/loginForm")
	public String loginForm() {

		return "user/loginForm";
	}

	@GetMapping("/user/updateForm")
	public String updateForm() {

		return "user/updateForm";
	}

	@GetMapping("/auth/kakao/callback")
	// @ResponseBody를 지워야 ViewResolver를 호출해서 파일을 찾아간다(return 때)
	//public @ResponseBody String kakaoCallback(String code) { 
	public String kakaoCallback(String code) {
		// @ResponseBody Data를 Return해주는 컨트롤러 함수
		// querystring의 값은 메소드의 파라미터로 쉽게 받을 수 있다(쿼리스트링명과 파라미터명이 동일해야함).

		// POST방식으로 key=value타입의 데이터를 카카오로 요청한다.
		// a태그로, 하이퍼링크로 전달하는 요청은 무조건 GET방식이기 때문에 
		// 이때 필요한 라이브러리가 RestTemplate라이브러리이다 = http요청을 편하게 할 수 있다.
		// RestTemplate외에 Retrofit2, OkHttp 등 여러가지가 있다.
		RestTemplate rt = new RestTemplate();
		// HttpHeader Object 생성
		HttpHeaders headers = new HttpHeaders();
		headers.add("application/x-www-form-urlencoded;charset=utf-8", code); 
		// Content-type을 담는다 = 내가 지금 전송할 http body데이터가 key=value형태의 데이터라는 것을 명시

		// Body Data를 담을 Object 생성
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "authorization_code");
		params.add("client_id", "0b340e6ec9a55fcd5aceceae883023b3");
		params.add("redirect_uri", "http://localhost:8000/auth/kakao/callback");
		params.add("code", code);

		// 얘가 바디데이터와 헤더값을 가지고 있는 엔티티가 된다.
		// 여기에 데이터를 담는 이유는 아래의 exchange라는 함수가 HttpEntity라는 Object를 받게 되어있다.
		HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = 
				new HttpEntity<>(params, headers);

		// Post방식으로 Http 요청하기 + response
		ResponseEntity<String> response = rt.exchange(
				"https://kauth.kakao.com/oauth/token",	// 첫번째, 토큰발급 요청주소
				HttpMethod.POST,	// 두번째, 요청메소드
				kakaoTokenRequest,	// 세번째, 바디데이터와 헤더값을 한번에 넣는다 
				String.class // 네번째, 응답을 받을 타입 = response가 스트링으로 응답된다
				);

		// JSON 데이터를 오브젝트에 담겠다.
		// Gson, Json Simple, ObjectMapper(ObjectMapper는 기본적으로 내장되어있다)
		ObjectMapper objectMapper = new ObjectMapper();
		OAuthToken oauthToken = null;

		// 파싱하는 과정에 오류가 많이 나기 때문에
		try {
			oauthToken = objectMapper.readValue(response.getBody(), OAuthToken.class);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}		

		System.out.println("카카오 액세스 토큰 : " + oauthToken.getAccess_token());





		RestTemplate rt2 = new RestTemplate();
		HttpHeaders headers2 = new HttpHeaders();
		headers2.add("Authorization", "Bearer " + oauthToken.getAccess_token());
		headers2.add("Context-type", "application/x-www-form-urlencoded;charset=utf-8"); 

		HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest2 = 
				new HttpEntity<>(headers2);

		ResponseEntity<String> response2 = rt2.exchange(
				"https://kapi.kakao.com/v2/user/me",
				HttpMethod.POST,
				kakaoProfileRequest2, 
				String.class 
				);

		ObjectMapper objectMapper2 = new ObjectMapper();
		KakaoProfile kakaoProfile = null;

		// 파싱하는 과정에 오류가 많이 나기 때문에
		try {
			kakaoProfile = objectMapper2.readValue(response2.getBody(), KakaoProfile.class);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		// User 오브젝트 : username, password, email
		System.out.println("카카오 아이디(번호) : " + kakaoProfile.getId());
		System.out.println("카카오 이메일 : " + kakaoProfile.getKakao_account().getEmail());

		System.out.println("블로그서버 유저네임 : " + kakaoProfile.getKakao_account().getEmail() + "_" + kakaoProfile.getId());
		System.out.println("블로그서버 이메일 : " + kakaoProfile.getKakao_account().getEmail());
		
		// UUID란 중복되지 않는 특정한 값을 만들어내는 알고리즘
		// UUID garbagePassword = UUID.randomUUID();
		System.out.println("블로그서버 패스워드 : " + cosKey);		

		User kakaoUser = User.builder()
				.username(kakaoProfile.getKakao_account().getEmail()+"_"+kakaoProfile.getId())
				.password(cosKey)
				.email(kakaoProfile.getKakao_account().getEmail())
				.oauth("kakao")
				.build();				

		// 가입자 혹은 비가입자 체크해서 처리해야한다
		User originUser = userService.회원찾기(kakaoUser.getUsername());

		// 비가입자인 경우, 회원가입
		if(originUser.getUsername() == null) {
			System.out.println("기존 회원이 아니기 때문에 자동 회원가입을 진행합니다.");
			userService.회원가입(kakaoUser);
		}
		
		System.out.println("자동 로그인을 진행합니다.");
		// 가입자인 경우, 로그인처리
		// 세션등록
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(kakaoUser.getUsername(), cosKey));
		SecurityContextHolder.getContext().setAuthentication(authentication);


		// return "카카오 토큰요청완료 : 토큰요청에 대한 응답 > " + response;
		// 바디와 헤더값을 나눠서 볼 수 있다.
		//return response.getBody();
		//return "카카오 토큰요청완료 : 토큰요청에 대한 응답 > " + response.getHeaders();
		//return response2.getBody();
		return "redirect:/";
	}
}
