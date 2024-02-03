package com.cos.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseDto<T> { // <T> : 제네릭을 걸어준다. : 무슨말이지?

	//HttpStatus status;
	int status;
	T data;
}
