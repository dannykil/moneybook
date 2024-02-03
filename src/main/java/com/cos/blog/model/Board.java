package com.cos.blog.model;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

//import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// ${board.title}에서 getTitle()이라는 메소드가 호출된다 : @Data(getter, setter 모두 만들어진다)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity // 가장 아래 내려오는게 좋다.
public class Board {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // auto_increment
	private int id;
	
	@Column(nullable = false, length = 100)
	private String title;
	
	@Lob // 대용량 데이터 > 섬머노트 라이브러리를 사용할 예정이기 때문
	private String content; // 섬머노트 라이브러리 <html>태그가 섞여서 디자인됨
	
	//@ColumnDefault("0")
	private int count; // 조회수
	
	// 1개여도 됨
	// Board가 Many, User가 One. 한명의 유저는 여러개의 게시글을 쓸 수 있다.
	@ManyToOne(fetch = FetchType.EAGER) // 기본전략이 EAGER
	// Board테이블을 Select하면 user정보는 한건밖에 없으니 Join해서 바로 가져온다. 
	@JoinColumn(name="userId") // userId라는 필드값이 생성되고 연관관계는 ManyToOne으로 만들어진다.
	private User user; // DB는 Object를 저장할 수 없다. FK, 자바는 오브젝트를 저장할 수 있다.
	// User객체를 참조하기 때문에 자동으로 FK가 만들어진다.	
	// 1개이면 안됨 > List(Collection)이 되어야 함
	
	// @OneToMany(mappedBy = "board") // 하나의 게시물은 여러개의 답변을 가질 수 있다.
	// 기본전략은 LAZY이나 우리는 첫화면에서 댓글을 바로 보여줄 예정이므로 EAGER로 수정
	// cascade 옵션
	// 1) Persist : reply는 연관관계의 주인이 아니기 때문에 DB업데이트가 안되나 Persist를 등록하면 가능해진다.
	// 2) Remove : Board게시글을 지울 때 댓글을 모두 지우겠다.
	// 3) All : Persist + Remove
	@OneToMany(mappedBy = "board", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE) 
	// mappedBy가 적혀있으면 연관관계의 주인이 아니다(난 FK가 아니다) DB에 칼럼을 만들지 마세요.
	// Reply테이블의 boardId가 FK이다.	
	// Board를 Select할 때 Join문을 통해서 값을 얻기 위해 필요한 것이다.
	// (mappedBy = "board")에서 board는 필드이름인데 Reply클래스의 private Board board; 이 부분이다.
	// @JoinColumn(name="replyId") // 이건 필요없다 > DB 1정규화가 깨지는 경우이기 때문
	// 1정규화 : 데이터베이스의 하나의 칼럼은 하나의 값(원제성?)을 가져야 한다.
	// Board가 replyId를 FK로 가지게 되면 하나의 게시물은 여러개의 댓글을 가질 수 있는데 
	// 그럴 경우 한 칼럼에서 다수의 값을 가지게 되기 때문에.
	// @OneToMany의 기본전략은 FetchType.EAGER이 아니다.
	// 필요하면 가져오고 필요없으면 안가져오기 때문에 
	// 기본 Fetch 전략이 FetchType.LAZY(@ManyToOne과 다르다)
	// @ManyToOne은 무조건 가져와라
	// @OneToMany는 필요하면 가져와라
	@JsonIgnoreProperties({"board", "user"}) // 연관관계가 있는 테이블(reply)에서 board를 또 호출할 경우 무시한다.
	@OrderBy("id desc")
	private List<Reply> replys;
	
	@CreationTimestamp // Data가 insert or update될 때 자동으로 들어감
	private Timestamp createDate;
}
