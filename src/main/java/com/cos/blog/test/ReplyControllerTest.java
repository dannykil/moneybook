package com.cos.blog.test;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.cos.blog.model.Board;
import com.cos.blog.model.Reply;
import com.cos.blog.repository.BoardRepository;
import com.cos.blog.repository.ReplyRepository;

@RestController
public class ReplyControllerTest {

	@Autowired
	private BoardRepository boardRepository;
	
	@Autowired
	private ReplyRepository replyRepository;
	
	@GetMapping("/test/board/{id}")
	public Board getBoard(@PathVariable int id) {
		
		// 무한참조 관련 테스트
		// 리털할 때 jackson 라이브러리(오브젝트를 json으로 리턴)가 실행된다.
		// = 모델의 getter를 호출해서 json으로 바꿔줌
		return boardRepository.findById(id).get();
	}
	
	// reply로 바로들어오면 board와 user를 return하지만
	// board를 통해서 들어오면 무시한다.
	@GetMapping("/test/reply")
	public List<Reply> getReply() {
		
		return replyRepository.findAll();
	}
}
