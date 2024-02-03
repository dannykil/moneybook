package com.cos.blog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cos.blog.dto.ReplySaveRequestDto;
import com.cos.blog.model.Board;
import com.cos.blog.model.User;
import com.cos.blog.repository.BoardRepository;
import com.cos.blog.repository.ReplyRepository;
import com.cos.blog.repository.UserRepository;

//@RequiredArgsContructor을 사용하면 
// 생성자 만들 때 초기화가 꼭 필요한 애들을 생성자 파라미터에 넣어서 초기화해준다.
@Service
public class BoardService {

	// @Autowired(DI)의 원리
	// 1) 기본적으로 생성자를 가지고 있고 이걸 통해서 스프링 컨텍스트에 올린다.
	//	public BoardService() {
	//		
	//	}
	// 스프링이 컴포넌트 스캔할 때, @Service를 읽고 BoardService가 
	// SpringContext(Ioc컨테이너)에 저장되어야 하는 Object(Bean)라는 것을 인식하는데 
	// 여기서 new를 하며 이 말은 기본생성자(위)를 호출해서 컨텍스트에 올린다는 의미이다.
	// 2) 하지만 다른 객체를 사용하기 위해선 기본생성자에 파라미터를 주입해야 하는데
	//    파라미터를 주입받으면 더 이상 기본생성자가 아니고 파라미터를 주입받아야 하는
	//    생성자가 된다.
	// 3) 받아야 하는 파라미터들이 여기서 직접 주입할 수 있기 때문에(올라가 있어서)
	//    주입하면서 일반생성자가 new하게 된다.
	

	// *** 기존의 기본생성자호출 방법 = 컨텍스트에 올리다는 의미
	//	public BoardService() {
	//		
	//	}

	// *** 기존의 기본생성자에 파라미터 DI(주입)하는 방법
	//	private BoardRepository boardRepository;
	//	private ReplyRepository replyRepository;
	//	
	//	public BoardService(BoardRepository bRepo, ReplyRepository rRepo) {
	//		this.boardRepository = bRepo;
	//		this.replyRepository = rRepo;
	//	}

	// *** 애노테이션을 통한 DI
	//	@Autowired
	//	private UserRepository userRepository;
	//
	//	@Autowired
	//	private BoardRepository boardRepository;

	// *** final을 통한 DI = 초기화를 위해 null을 넣어줘야 한다.
	//	private final BoardRepository boardRepository = null;
	//	private final ReplyRepository replyRepository = null;
	// *** or
	// @RequiredArgsContructor을 사용하면 
	// 생성자 만들 때 초기화가 꼭 필요한 애들을 생성자 파라미터에 넣어서 초기화해준다.
	//	private final BoardRepository boardRepository;
	//	private final ReplyRepository replyRepository;
	
	// ****** 가장 편한 방법은
	// 클래스 위에 @RequiredArgsContructor을 붙이고 final을 쓰는 것이다.


	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BoardRepository boardRepository;

	@Autowired
	private ReplyRepository replyRepository;


	@Transactional
	public void 글쓰기(Board board, User user) {

		board.setCount(0);
		board.setUser(user);
		boardRepository.save(board);
	}

	@Transactional(readOnly = true)
	public Page<Board> 글목록(Pageable pageable){

		// 페이징이 되면 return값이 List타입이 아니 된다 : List > Page타입으로 변경
		return boardRepository.findAll(pageable);
	}

	@Transactional(readOnly = true)
	public Board 글상세보기(int id) {

		return boardRepository.findById(id)
				.orElseThrow(()->{
					return new IllegalArgumentException("글 상세보기 실패 : 아이디를 찾을 수 없습니다.");
				});
	}

	@Transactional
	public void 글삭제하기(int id) {

		boardRepository.deleteById(id);
	}

	// 서비스가 시작될 때 트랜잭션이 시작되고 서비스가 종료될 때 트랜잭션도 종료되면서 commit된다. 
	// 수정하려면 영속화를 먼저 해야한다.
	@Transactional
	public void 글수정하기(int id, Board requestBoard) {
		Board board = boardRepository.findById(id)
				.orElseThrow(()->{
					return new IllegalArgumentException("글 찾기 실패 : 아이디를 찾을 수 없습니다.");
				}); // 영속화 완료

		board.setTitle(requestBoard.getTitle());
		board.setContent(requestBoard.getContent());
		// 해당 함수의 종료 시(Service가 종료될 때) 트랜잭션이 종료된다. 
		// 이때 더티체킹이 일어나면서 자동으로 업데이트(DB에 Flush)된다 = commit
	}


	// 1번째 방법 : 전통적인 방식
	//		@Transactional
	//		public void 댓글쓰기(User user, int boardId, Reply requestReply) { // 1번 방법 : 전통적public void 댓글쓰기(ReplySaveRequestDto replySaveRequestDto) { // 2번 방법 : @Builder패턴
	//	
	//			Board board = boardRepository.findById(boardId).orElseThrow(()->{
	//				return new IllegalArgumentException("댓글쓰기 실패 : 게시글 아이디를 찾을 수 없습니다.");
	//			}); // 영속화 완료
	//	
	//			requestReply.setUser(user);
	//			requestReply.setBoard(board);
	//	
	//			replyRepository.save(requestReply);
	//		}


	// 2번째 방법 : Dto(Data Transfer Object)와 @Builder패턴 적용
	// 하나하나 적는 이 방식보다 더 좋은 방법이 있다 > 3번째 방법
	// Reply model로 가서 함수를 하나 만들면 된다.
	// 하지만 지금은 굳이 안하고 위에 @Builder패턴을 사용하겠다.
	// board.js 수정해야함
	//	@Transactional
	//	public void 댓글쓰기(ReplySaveRequestDto replySaveRequestDto) { 
	//
	//		User user = userRepository.findById(replySaveRequestDto.getUserId()).orElseThrow(()->{
	//			return new IllegalArgumentException("댓글쓰기 실패 : 사용자 ID를 찾을 수 없습니다.");
	//		}); // 영속화 완료
	//
	//		Board board = boardRepository.findById(replySaveRequestDto.getBoardId()).orElseThrow(()->{
	//			return new IllegalArgumentException("댓글쓰기 실패 : 게시글 ID를 찾을 수 없습니다.");
	//		}); // 영속화 완료
	//
	//		// Reply객체를 만들 때 @Builder 패턴으로 만든다(model.Reply)
	//		Reply reply = Reply.builder() // @Builder패턴
	//				.user(user)
	//				.board(board)
	//				.content(replySaveRequestDto.getContent())
	//				.build();
	//
	//		replyRepository.save(reply);
	//	}


	// 3번째 방법 : 함수, 네이티브 쿼리 사용
	@Transactional
	public void 댓글쓰기(ReplySaveRequestDto replySaveRequestDto) { 

		int result = replyRepository.mSave(replySaveRequestDto.getUserId(), replySaveRequestDto.getBoardId(), replySaveRequestDto.getContent());
		System.out.println(result); // Object를 출력할 경우 자동으로 toString()이 호출됨
	}
	
	@Transactional
	public void 댓글삭제(int replyId) {
		replyRepository.deleteById(replyId);
	}
}
