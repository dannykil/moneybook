package com.cos.blog.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cos.blog.model.RoleType;
import com.cos.blog.model.User;
import com.cos.blog.repository.UserRepository;

// 스프링이 컴포넌트 스캔을 통해서 Bean에 등록해줌 = IoC를 해준다 = 메모리에 대신 띄워준다
// service가 왜 필요한가
// 1) 트랜잭션 관리(layer를 알아야한다 : Controller, Service, Dao) 
// 2) 서비스 의미때문
// Repository는 단순히 crud 하나씩 들고있으면
// Service는 예를들어 update가 1개일수도 있고 2개일수도 있다.
// 즉, Service는 update 2개(ex)가 모두 성공해야 이상이 없는 것이며, 하나라도 이상이 있을 경우 롤백을 해야한다.
// 이 때 각각의 update를 트랜잭션이라고 하는데 이 트랜잭션들을 하나의 트랜잭션으로 묶어서 서비스화 할 수 있다.
// Dummy에서도 insert하는데 왜 또 굳이?
@Service
public class UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder encoder;	
	
	
	// #################################
	@Transactional(readOnly = true)
	public List<User> findAll() {
		
		List<User> user = userRepository.findAll();
		
		return user;
	}
	// ################################
	
	
	@Transactional(readOnly = true)
	public User 회원찾기(String username) {
		
		// 없는경우 null을 준다
		// null일 경우와 아닐경우 어떻게 처리할지 알기때문에(어디서?)
		User user = userRepository.findByUsername(username).orElseGet(()->{
			
			// 회원이 없는 경우, 빈객체를 리턴한다.
			// 원하는 값을 입력해도 된다.
			return new User();
		});
		
		return user;
	}
	
	// service 함수를 만들어준다.
	// import 할 수 있는게 2가지 있는데 2가지가 서로 다른것인가?
	@Transactional
	//public int 회원가입(User user) {
	public void 회원가입(User user) {
		String rawPassword = user.getPassword();
		String encPassword = encoder.encode(rawPassword);
		user.setPassword(encPassword);
		user.setRole(RoleType.USER);
		userRepository.save(user);
		
//		try {
//			userRepository.save(user);
//			return 1;
//		}catch(Exception e){
//			e.printStackTrace();
//			System.out.println("UserService : 회원가입() : " + e.getMessage());
//		}
//		return -1;
	}
	
	// 이 부분도 스프링 시큐리티 적용으로 사용안하기 때문에 삭제
//	@Transactional(readOnly = true) 
//	// Select할 때 트랜잭션 시작, 서비스 종료시에 트랜잭션 종료(정합성 유지를 위해)
//	public User 로그인(User user) {
//		
//		return userRepository.findByUsernameAndPassword(user.getUsername(), user.getPassword());
//	}
	
	@Transactional
	public void 회원수정(User user) {
		
		// 수정시에는 영속성 컨텍스트에 User 오브젝트를 영속화시키고, 영속화된 User 오브젝트를 수정.
		// Select를 해서 User오브젝트를 DB로 부터 가져오는 이유는 영속화를 하기 위해서.
		// 영속화된 오브젝트를 변경하면 자동으로 DB에 update문을 날려준다.
		User persistance = userRepository.findById(user.getId())
				.orElseThrow(()->{
					return new IllegalArgumentException("회원찾기 실패");
				});
		
		// validation 체크
		// oauth가 null이 아닌 회원은 패스워드 수정을 할 수 없도록 막아야한다
		// 화면을 막아도 postman같은 api테스트 툴로 서버에 값을 던질 수 있기 때문에
		if(persistance.getOauth() == null || persistance.getOauth().equals("")) {
			String rawPassword = user.getPassword();
			String encPassword = encoder.encode(rawPassword);
			persistance.setPassword(encPassword);
			persistance.setEmail(user.getEmail());
		}
		
		
		
		// 회원수정함수 종료 시 = 서비스 종료 = 트랜잭션 종료 = Commit이 자동으로 됨
		// Commit이 자동으로 된다는 것은 영속화된 persistance객체의 변화가 감지되어 더티체킹되어
		// 변화된 것들을 update문을 날려준다.				
	}
}
