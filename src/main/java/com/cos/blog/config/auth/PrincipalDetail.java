package com.cos.blog.config.auth;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.cos.blog.model.User;

import lombok.Data;

// 스프링 시큐리티가 로그인 요청을 가로채서 로그인을 진행하고 완료되면 UserDetails 타입의 Object를
// 스프링 시큐리티의 고유한 세선저장소에 저장한다.
// UserDetails 타입의 Principal이 저장된다.
// 저장될 때 user객체도 함께 저장되어야 한다.
@Data
public class PrincipalDetail implements UserDetails {
	
	// 생성자
	public PrincipalDetail(User user) {
		this.user = user;
	}
	
	private User user; 
	// 컴포지션 : 객체를 품고있는 것
	// extends를 해서 가져오는건 상속

	// Alt + Shift + s를 눌러서 Override해준다.
	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getUsername();
	}
	
	// 계정이 만료되지 않았는지 리턴한다. (true : 만료안됨)
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}
	
	// 계정이 잠겼는지 안잠겼는지를 return
	@Override
	public boolean isAccountNonLocked() {
		return true; // true : 안잠겨있는것
	}

	// 비밀번호가 만료되지 않았는지를 리턴한다.
	@Override
	public boolean isCredentialsNonExpired() {
		return true; // true : 만료가 안된것
	}

	// 계정활성화가 되었는지 않되어있는지
	@Override
	public boolean isEnabled() {
		return true; // 활성화된 것
	}
	
	// 계정이 가지고 있는 권한목록을 리턴한다.
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		
		// ArrayList는 collection타입
		Collection<GrantedAuthority> collectors = new ArrayList<>();
		// 람다식 표현으로 교체
		
//		collectors.add(new GrantedAuthority() { 
//			// GrantedAuthority가 interface이기 때문에 익명클래스가 만들어지고 
//			// 거기에 추상메소디(아래)가 Overriding된다.			
//			@Override
//			public String getAuthority() {
//				
//				// Role을 받을 때 아래와 같이 쓰는건 규칙이다. 
//				// 꼭 넣어야함 : "ROLE_"
//				return "ROLE_" + user.getRole();
//			}
//		});
		
		// 람다식 표현
		collectors.add(()->{return "ROLE_"+user.getRole();});
		
		return collectors;
	}
}
