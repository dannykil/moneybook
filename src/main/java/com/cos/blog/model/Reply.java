package com.cos.blog.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class Reply {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(nullable = false, length = 200)
	private String content;

	// 연관관계
	@ManyToOne // 여러개의 답변은 한개의 게시물에 존재할 수 있다.
	@JoinColumn(name="boardId")
	private Board board; 

	@ManyToOne // 여러개의 답변을 한명의 유저가 작성할 수 있다.
	@JoinColumn(name="userId")
	private User user;

	@CreationTimestamp
	private Timestamp createDate;

	// Alt + Shift + s
	@Override
	public String toString() {
		return "Reply [id=" + id + ", content=" + content + ", board=" + board + ", user=" + user + ", createDate="
				+ createDate + "]";
	}	
	
	
	// 3번째 방법(함수사용)
	// 이 프로젝트에서는 @Bulider패턴 사용으로 주석처리
//	public void update(User user, Board board, String Content) {
//
//		setUser(user);
//		setBoard(board);
//		setContent(content);
//	}
}
