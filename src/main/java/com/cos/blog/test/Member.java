package com.cos.blog.test;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*@Getter
@Setter*/
@Data // Getter Setter를 모두 포함한 것
//@AllArgsConstructor // 모든 필드를 다 쓰는 생성자
@NoArgsConstructor // 빈생성자
// @RequiredArgsConstructor // final이 붙은 애들에 대한 생성자(constructor)를 만들어준다.
public class Member {	
	// java에서 변수는 모두 private로 만든다.
	// 변수의 상태를 직접 수정하는 것이 아닌 메소드를 톻해 수정하기 위해(객체지향)
	private int id;
	private String username;
	private String password;
	private String email;
	
	
	/*
	 * private final int id; private final String username; private final String
	 * password; private final String email; // 불변성 유지를 위해 final을 쓴다.
	 */	// 변경할 필요가 있으면 final을 쓰면 안된다.
	
	// lombok.Getter로 만듬
//	// 생성자
//	// 마우스 우클릭 > source > Generate Constructor Using Fields
	@Builder 
	// 파리미터 갯수가 다를 때 + 순서를 지키지 않아도 됨
	// lombok이 @Builder패턴을 만들어준다
	public Member(int id, String username, String password, String email) {
//		
//		// 필요없으므로 지운다
//		//super();
		this.id = id;
		this.username = username;
		this.password = password;
		this.email = email;
	}
//	
//	// 마우스 우클릭 > source > Generate Getters and Setters 
//	public int getId() {
//		return id;
//	}
//	public void setId(int id) {
//		this.id = id;
//	}
//	public String getUsername() {
//		return username;
//	}
//	public void setUsername(String username) {
//		this.username = username;
//	}
//	public String getPassword() {
//		return password;
//	}
//	public void setPassword(String password) {
//		this.password = password;
//	}
//	public String getEmail() {
//		return email;
//	}
//	public void setEmail(String email) {
//		this.email = email;
//	}
}
