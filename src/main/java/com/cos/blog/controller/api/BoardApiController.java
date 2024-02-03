package com.cos.blog.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.cos.blog.config.auth.PrincipalDetail;
import com.cos.blog.dto.ReplySaveRequestDto;
import com.cos.blog.dto.ResponseDto;
import com.cos.blog.model.Board;
import com.cos.blog.service.BoardService;

@RestController 
public class BoardApiController {

	@Autowired
	private BoardService boardService;

	@PostMapping("/api/board")
	public ResponseDto<Integer> save(@RequestBody Board board, @AuthenticationPrincipal PrincipalDetail principal) { 

		boardService.글쓰기(board, principal.getUser());
		return new ResponseDto<Integer>(HttpStatus.OK.value(), 1); 
	}

	@DeleteMapping("/api/board/{id}")
	public ResponseDto<Integer> deleteById(@PathVariable int id){

		boardService.글삭제하기(id);

		return new ResponseDto<Integer>(HttpStatus.OK.value(), 1); 
	}

	@PutMapping("/api/board/{id}")
	public ResponseDto<Integer> update(@PathVariable int id, @RequestBody Board board) {
		boardService.글수정하기(id, board);

		return new ResponseDto<Integer>(HttpStatus.OK.value(), 1);
	}

	// 댓글등록 > 2번째 방식 적용 : Dto(Data Transfer Object)와 @Builder패턴
	// 원래는 데이터를 받을 때 컨트롤러에서 Dto를 만들어서 받는게 좋다.
	// 이번에 Dto를 사용하지 않은 이유는 이정도는 작은 프로젝트였기 때문에 굳이 사용할 필요가 없었다.
	// 하지만 프로젝트가 커지면 왔다갔다하는 데이터(필드)가 많아지고 join이 되면서 더 복잡해지는데 
	// 그것을 model의 object로만 받아서 처리한다는건 좋은방법이 아니다.

	// 1번째 방법 : 전통적인 방식
	//	 @PostMapping("/api/board/{boardId}/reply")
	//		public ResponseDto<Integer> replySave(@PathVariable int boardId, @RequestBody Reply reply, @AuthenticationPrincipal PrincipalDetail principal) { 
	//	
	//			boardService.댓글쓰기(principal.getUser(), boardId, reply);
	//			return new ResponseDto<Integer>(HttpStatus.OK.value(), 1); 
	//		}

	// 2번째 방법 : Dto(Data Transfer Object)와 @Builder패턴 적용
	@PostMapping("/api/board/{boardId}/reply")
	public ResponseDto<Integer> replySave(@RequestBody ReplySaveRequestDto replySaveRequestDto) {
		boardService.댓글쓰기(replySaveRequestDto);
		return new ResponseDto<Integer>(HttpStatus.OK.value(), 1); 
	}
	
	@DeleteMapping("/api/board/{boardId}/reply/{replyId}")
	public ResponseDto<Integer> replyDelete(@PathVariable int replyId) {
		
		boardService.댓글삭제(replyId);
		return new ResponseDto<Integer>(HttpStatus.OK.value(), 1);
	}
}
