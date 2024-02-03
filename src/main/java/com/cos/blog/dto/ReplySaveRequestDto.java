package com.cos.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
// Dto(Data Transfer Object)를 쓰는 것의 장점은
// 필요한 데이터를 한번에 받아 사용할 수 있다.
public class ReplySaveRequestDto { 

	private int userId;
	private int boardId;
	private String content;
}
