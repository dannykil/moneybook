package com.cos.blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.cos.blog.service.BoardService;

@Controller
public class BoardController {
	
	
	@Autowired
	private BoardService boardService;
	
	// 컨트롤러에서 세션을 어떻게 찾는가?
	@GetMapping({"", "/"}) 
	public String index(Model model, @PageableDefault(size=3, sort="id", direction=Sort.Direction.DESC) Pageable pageable) { 
		
		model.addAttribute("boards", boardService.글목록(pageable));
		// System.out.println("로그인 사용자 아이디 : " + principal.getUsername());
		
		// index.jsp로 boards가 날라간다
		// @Controller는 viewResolver가 작동하는데 해당 페이지로 Model의 정보를 들고 이동한다.
		// 앞뒤로 application.yml의 prefix, suffix가 붙는다 : prefix + 페이지명 + suffix
		return "index";
	}
	
	@GetMapping("/board/{id}")
	public String findById(@PathVariable int id, Model model) {
		
		model.addAttribute("board", boardService.글상세보기(id));
		return "board/detail";
	}
	
	@GetMapping("/board/{id}/updateForm")
	public String updateForm(@PathVariable int id, Model model) {
		model.addAttribute("board", boardService.글상세보기(id));
		
		return "board/updateForm";
	}
	
	// USER권한 필요
	@GetMapping("/board/saveForm")
	public String saveForm() { 
		
		return "board/saveForm";
	}
}
