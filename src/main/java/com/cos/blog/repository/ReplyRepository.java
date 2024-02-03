package com.cos.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.cos.blog.model.Reply;

public interface ReplyRepository extends JpaRepository<Reply, Integer> {

	@Modifying
	// interface에서는 public을 안써도 된다.
	// ReplySaveRequestDto타입을 받는다.
	@Query(value="INSERT INTO reply(userId, boardId, content, createDate) VALUES(?1, ?2, ?3, now())", nativeQuery = true) // nativeQuery = true : 내가 작성한 쿼리가 작동한다.
	int mSave(int userId, int boardId, String content); // 업데이트된 행의 갯수를 리턴

	// mSave 호출하면 영속화할 필요가 없다.
	// BoardService에서 호출
}
