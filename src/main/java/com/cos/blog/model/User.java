package com.cos.blog.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// JPA는 ORM이다.
// ORM -> Java(다른언어) Object를 테이블로 매핑해주는 기술
// 이 클래스를 테이블화 시키기 위해서는 @Entity라는 애노테이션을 붙여야한다.
// User 클래스가 아래를 읽어서 자동으로 MySQL에 테블이 생성된다. 

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder // 빌더패턴
@Entity // 가장 아래 내려오는게 좋다.
//@DynamicInsert // insert 시에 null인 필드를 지워준다. 하지만 애노테이션이 너무 많아진다.
public class User {
	
	@Id // Primary key
	// @GeneratedValue : 해당 프로젝트에서 연결된 DB의 넘버링 전략을 따라간다.
	// 여기서는 mysql을 사용하기 때문에 auto_increment를 사용하게된다.
	@GeneratedValue(strategy = GenerationType.IDENTITY)  
	private int id; // 시퀀스(오라클), auth_increment(mysql)에 해당
	
	@Column(nullable = false, length = 100, unique = true) // null이 될 수 없고, 중복값이 있으면 안된다(unique).
	private String username; // 아이디
	
	@Column(nullable = false, length = 100) // length를 넉넉히 주는 이유는 나중에 hash를 통해 암호화하기 때문에
	//@Column(length = 100) // 카카오 로그인서비스를 이용하면서 password가 null이 될 수 있기 때문에
	private String password; 
	
	@Column(nullable = false, length = 50)
	private String email;
	
	// DB는 RoleType이라는게 없다.
	@Enumerated(EnumType.STRING)
	//@ColumnDefault("'user'")
	//private String role; // Enum을 쓰는게 좋다 > Enum은 데이터의 도메인(범위)을 만들어주기 때문에. admin, user, manager or 남, 여 등(특정값을 고정할 수 있음)
	private RoleType role;
	
	// kakao, google
	private String oauth;
	
	@CreationTimestamp // 시간이 자동으로 입력된다.
	private Timestamp createDate;
	
	
}
