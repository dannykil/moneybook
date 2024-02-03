package com.cos.blog.config.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.cos.blog.model.User;
import com.cos.blog.repository.UserRepository;

@Service // 메모리에 올리려면 @Service써야 Bean으로 등록
// PrincipalDetailService의 타입은 UserDetailsService이 되어야 한다.
public class PrincipalDetailService implements UserDetailsService {
	
	@Autowired
	private UserRepository userRepository;
	// ctrl + spacebar로 한번에 등록
	// 스프링이 로그인 요청을 가로챌 때, username, password라는 변수 2개를 가로채는데
	// password 부분의 처리는 알아서 함.
	// username이 DB에 있는지만 확인해주면 됨 > loadUserByUsername 함수에서 확인함
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		User principal = userRepository.findByUsername(username)
				.orElseThrow(()->{
					return new UsernameNotFoundException("해당 사용자를 찾을 수 없습니다. : " + username);
				});
		
		// PrincipalDetail()은 null이기 때문에 생성자를 만들어줘야한다.
		return new PrincipalDetail(principal); // 시큐리티 세션에 유저정보가 저장됨
	}
}
