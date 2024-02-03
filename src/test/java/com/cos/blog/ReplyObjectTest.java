package com.cos.blog;

import org.junit.jupiter.api.Test;

import com.cos.blog.model.Reply;

public class ReplyObjectTest {

	// 테스트는 마우스 우클릭해서 Run As > junit Test
	@Test
	public void 투스트링테스트() {
		Reply reply = Reply.builder()
				.id(1)
				.user(null)
				.board(null)
				.content("안녕")
				.build();
		
		System.out.println(reply);
	}
}
