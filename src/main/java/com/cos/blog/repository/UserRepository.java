package com.cos.blog.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Query;

import com.cos.blog.model.User;
//import com.google.common.base.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
	
	// JPA naming Query
	// Select * from user Where username = 1; // 첫번째 파라미터
	//Optional<User> findByUsername(String username);
	Optional<User> findByUsername(String username);
	
	
	// #################################
	
	@Query(value = "SELECT * FROM USER", nativeQuery = true)
	public List<User> findAll();
	
	// #################################
	
	
	
	
	
	
	
	
	
// JpaRepository<User, Integer>는 User테이블이 관리하는 Repository이며, 
// User테이블의 PrimaryKey는 Integer(숫자)이다.
// 기본적인 CRUD는 	JpaRepository 함수만으로도 충분히 가능하다(모든 함수를 다 들고있기 때문에)
// JSP기준에서는 DAO(Data Access Object) 역할
// 스프링 레거시나 부트를 쓰는 사람들은 bean으로 등록되는지 의심할 수 있다.
// = 스프링 IoC에서 객체를 가지고 있냐는 것과 같은 의미
// = 스프링 IoC에서 객체를 가지고 있어야 필요한 곳에서 DI(Data Injection) 할 수 있기 때문에.
// * 결론은 자동으로 bean으로 등록된다.
// = 그렇기 때문에 @Repository 생략가능	
	
	// 로그인을 위한 함수를 만들어야 한다.
	// JPA Naming전략 
	// 이 부분도 스프링 시큐리티 적용으로 사용안하기 때문에 삭제
	// User findByUsernameAndPassword(String username, String password);
	// 실제로 없는 함수인데 이런식으로 네이밍을 하면 함수이름에서 대문자를 인식해서 where절에 넣게된다.
	// = Select * from user where username = ?1 and password = ?2;
	// or 아래와 같은 방법으로도 가능하다(Native Query).
//	@Query(value="Select * from user where username = ?1 and password = ?2", nativeQuery = true)
//	User login(String username, String password);
}
