package com.cos.blog.test;

import java.util.List;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.cos.blog.model.RoleType;
import com.cos.blog.model.User;
import com.cos.blog.repository.UserRepository;

@RestController
public class DummyControllerTest {

	// 스프링이 RestController라는 애노테이션을 읽어서 DummyControllerTest를 메모리에 띄워줄 때
	// userRepository는 null이다.
	// 이 때 @Autowired를 붙여주면 DummyControllerTest가 메모리에 뜰 때
	// userRepository도 메모리에 같이 뜬다.
	// 여기서 @Autowired는 UserRepository 타입으로 스프링이 관리하고 있는 객체가 있다면 
	// "userRepository에 넣어줘라"라고 명시하고 있는 것이다.
	// 현재는 UserRepository 타입으로 메모리에 떠있는 상태이다.
	// 왜냐하면 스프링이 컴포넌트 스캔할 때 UserRepository Interface를 띄워주기 떄문에.
	// 그렇기 때문에 우리는 @Autowired를 통해서 사용하기만 하면된다.
	// 이게 의존성 주입이다 = DI
	@Autowired
	private UserRepository userRepository;
	
	
	@DeleteMapping("/dummy/user/{id}")
	public String delete(@PathVariable int id) {
		
		try {
			userRepository.deleteById(id);
		//} catch (EmptyResultDataAccessException e) { // 다른 Exception을 정의할 수있다.
		} catch (Exception e) { // 가장 최상위(귀찮을 경우)
			return "삭제에 실패했습니다. 해당 id는 DB에 없습니다.";
		}
		
		return "삭제되었습니다. id : " + id;
	}
	
	
	
	// save함수는
	// 1) id를 전달하지 않으면 insert
	// 2) id를 전달하고 데이터가 있으면 update
	// 3) id를 전달했는데 데이터가 없으면 insert
	// email, 
	@Transactional // 함수 종료시에 자동으로 commit된다.
	@PutMapping("/dummy/user/{id}")
	// @RequestBody은 form태그로 받은 데이터(json)를 가져오는 애노테이션
	public User updateUser(@PathVariable int id, @RequestBody User requestUser) {
		System.out.println("id         : " + id);
		System.out.println("password   : " + requestUser.getPassword());
		System.out.println("email      : " + requestUser.getEmail());
		
		// 방법1. save함수와 set을 통한 update
		// 그래서 save로 update를 할 때는 DB에서 id값에 맞는 데이터를 가져와서 객체에 담고
		// 변경하고자 하는 값만 set으로 바꿔서 save한다.
		// 이 방법을 쓰지 않으면 password와 email을 제외한 값이 null이라 오류가 발생한다.
		// 이 시점에서 영속화된다.
		User user = userRepository.findById(id).orElseThrow(()->{
			return new IllegalArgumentException("수정에 실패했습니다.");
		});
		
		// 영속화되고나서 데이터가 변경되고나면 감지해서 DB에 수정을 날린다 = 더티체킹
		user.setPassword(requestUser.getPassword());
		user.setEmail(requestUser.getEmail());
		
		// requestUser에 id값이 안들어있기 때문에 추가
		// requestUser.setId(id); 
		// save는 insert할 때 쓰는 메소드인데 id값이 들어오면 update를 해준다.
		// 문제는 다른값들(update되지 않는 값들)이 들어오면 null로 변해버려서 잘 안쓴다.
		// userRepository.save(user); 
		// 위 부분을 주석처리하고 @Transactional로 바꾸면 save를 호출하지 않고도 변경된다.
		// = '더티채킹'이라고 한다.
		return user;
	}
	
	
	// http://localhost:8000/blog/dummy/user
	@GetMapping("/dummy/users")
	public List<User> list(){
		
		return userRepository.findAll();
	}
	
	// return타입을 List가 아니라 Page로 주게되면 Pageable관련 데이터로 리턴받을 수 있다.
	// 여기서는 쓸필요 없지만 Paging기능 구현에서는 씀
	@GetMapping("/dummy/user")
	public List<User> pageList(@PageableDefault(size=2, sort="id", direction = Sort.Direction.DESC) Pageable pageable){		
		Page<User> pagingUser = userRepository.findAll(pageable);
		
		// .getContent()를 붙이면 필요한 데이터만 가져온다.
		List<User> users = pagingUser.getContent();
		return users;
	}
	
	
	// {id} 주소로 파라미터를 전달받을 수 있음
	// http://localhost:8000/blog/dummy/user/3
	@GetMapping("/dummy/user/{id}")
	public User detail(@PathVariable int id) { // 파라미터명과 변수명이 동일해야 한다.

		// /user/4를 찾을 때 데이터베이스에서 못찾아오게 되면 user가 null된다.
		// 그런경우 return값이 null이 되는데 문제가 발생한다.
		// 그래서 optional로 user객체를 감싸서 가져와서 null인지 아닌지 판단 후 return한다.

		// 방법1. 하지만 방법2.를 더 선호한다.
		// User user = userRepository.findById(id).get(); // null일 경우가 절대없을 경우 get() = 위험하다.
		// 검색조건에 대한 결과가 없을 경우 아래 로직을 타게되며 빈객체를 return하는데 그것은 null값이 아니다. 
		//		User user = userRepository.findById(id).orElseGet(new Supplier<User>() { 
		//			// 파라미터가 Supplier 타입이다.
		//			// Supplier 타입의 제너릭 부분이 '?'로 되어있는데 여기에는 익명객체가 들어가야 한다.(위 new ~)
		//			// 왜냐하면 Supplier는 interface라서 interface가 들고있는 함수(get() - 추상메소드)를 Override해줘야 햔다.
		//			// interface는 new로 생성할 수 없는데, new하려면 익명클래스를 만들어야 한다.
		//			// new하면서 바로 함수를 Override해주게 되면 객체생성이 가능하다.
		//			// 그리고 여기서는 빈객체 User()를 return해주면서 검색조건이 없는 경우,
		//			// 빈객체를 User()에 넣어주고 이것은 null이 아니게 된다.
		//			@Override
		//			public User get() { // ctrl + F3으로 자동생성이 안되서 직접 입력함
		//				
		//				return new User(); // 빈 객체를 return
		//			}
		//		});		

		// 방법2. 이 방법을 더 선호함
		User user = userRepository.findById(id).orElseThrow(new Supplier<IllegalArgumentException>() {

			@Override
			public IllegalArgumentException get() { 

				// 실제로는 AOP라는 개념을 이용해서 
				// Exception이 발생할 경우 에러페이지로 이동하게 만든다.
				return new IllegalArgumentException("해당 유저는 없습니다. id : " + id); 
			}
		});		


		//		// 방법3. 람다식, 하지만 이 교육과정에서는 사용하지 않겠다.
		//		// 람다식은 현재 진행하고 있는 교육과정보다는 더 상위방식이기 때문에.
		//		User user = userRepository.findById(id).orElseThrow(()->{
		//			return new IllegalArgumentException("해당 사용자는 없습니다.");
		//		});

		// user 객체는 = 자바 Object인데 요청은 웹브라우저에서 했다.
		// 하지만 DummyControllerTest클래스는 @RestController라서 html이 아니라 Data를 return해준다.
		// 그런데 여기서 @RestController가 웹브라우저에게 user객체를 return한다고 한다. 
		// = 웹브라우저는 user객체를 이해할 수가 없다 = 웹브라우저는 html같은 것만 이해할 수 있기 때문에.
		// 웹브라우저가 이해할 수 있게 변환해줘야 한다 > json (Gson 라이브러리)
		// 스프링부트는 MessageConverter가 응답 시에 자동으로 작동해서 
		// 만약 자바 Object를 return하게되면 Jackson 라이브러리를 호출해서 
		// user Object를 json으로 변환해서 브라우저에게 던져준다.
 		return user;
	}


	// http://localhost:8000/blog/dummy/join (요청)
	// http의 body에 username, password, email 데이터를 가지고 요청
	@PostMapping("/dummy/join")
	public String join(User user) {
		// public String join(String username, String password, String email) { // object 형식으로 받기 위해 주석처리
		// 원래는 @RequestParam("username") String u(변수명이 바껴도됨) 이렇게 했었다. 
		// key=value 형태로 받으며 약속된 규칙이다. x-www-form-urlencoded
		// 더 강력한 일을 해주는데 object로 받게 해준다.

		// id, role, createDate는 default값이 있기 때문에 
		// username, password, email만 파라미터에 추가할 경우 0 or null값으로 받게된다.
		System.out.println("id         : " + user.getId());
		System.out.println("username   : " + user.getUsername());
		System.out.println("password   : " + user.getPassword());
		System.out.println("email      : " + user.getEmail());
		// id는 auto_increment
		// CreateDate는 @CreationTimestamp를 붙였기 때문에 자바에서 현재시간을 넣어 insert 해준다.
		// role은 @ColumnDefault를 써서 insert할 때 작동하는데 
		// 여기서는 쿼리에 role=null값으로 insert되고 있다 = 파라미터를 지워야한다 = @DynamicInsert
		// @DynamicInsert를 붙이면 insert할 때 null인 필드를 JPA에서 지워준다.
		System.out.println("role       : " + user.getRole());
		System.out.println("CreateDate : " + user.getCreateDate());

		user.setRole(RoleType.USER);
		userRepository.save(user);
		return "회원가입이 완료되었습니다.";
	}
}
