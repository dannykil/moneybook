let index = {
	init:function(){
		$("#btn-save").on("click", ()=>{ 
			// function(){}을 쓰지않고 화살표함수를 쓴 이유는 
			// 코드를 줄이기 위해서가 아니라 this를 바인딩하기 위해서이다. 
			// 현재 이 function 밖과 안의 this값이 다르다.
			this.save();
		});
		$("#btn-update").on("click", ()=>{ 
			this.update();
		});
		/*$("#btn-login").on("click", ()=>{ 
			this.login();
		});*/
	},
	
	save:function(){
		//alert('user');
		
		let data = {
			username: $("#username").val(), 
			password: $("#password").val(), 
			email: $("#email").val()
		};
		
		//console.log(data);
		// callback : 하던일을 멈추고 돌아가는 것
		// ex. 다운로드 받는 과정이 오래 걸리면 다음 프로세스로 넘어가 진행하다가
		// 다운로드 받던 작업이 끝나면 그 프로세스로 돌아가 마무리하는 것	 > callback = 비동기
		// 비동기식을 사용하는 이유
		// 1) 웹과 앱의 서버를 한개로 통일하기 위해
		// 2) 비동기식 처리를 위해
		
		// ajax호출 시 default가 비동기 호출
		// ajax통신을 이용해서 3개의 데이터를 json으로 변경하여 insert요청한다.
		$.ajax({ // 회원가입 수행요청			 
			type:"POST", // type을 POST라고 해두면 insert라는걸 보통 알기 때문에 url을 상세히 안적는다.
			url:"/auth/joinProc",
			data:JSON.stringify(data), // http body데이터
			contentType:"application/json;charset=utf-8", // data와 contentType은 세트로 항상 같이 적어야 하며 body데이터타입이 json(mime)
			dataType:"json" // 서버로 요청해서 응답을 받을 때 json type으로 받으며 기본적으로 모두 String type
		}).done(function(resp){		
			if(resp.status == 500){
				alert("회원가입에 실패했습니다.");
			}
			else{
				alert("회원가입이 완료됐습니다.");
				location.href = "/";
			}					
		}).fail(function(error){
			// 실패했을 때는 fail함수롤 호출한다.
			alert(JSON.stringify(error));
		}); 
	},
	
	update:function(){		
		let data = {
			id: $("#id").val(),
			username: $("#username").val(),  
			password: $("#password").val(), 
			email: $("#email").val()
		};
		
		$.ajax({ 			 
			type:"PUT", 
			url:"/user",
			data:JSON.stringify(data), 
			contentType:"application/json;charset=utf-8",
			dataType:"json" 
		}).done(function(resp){
			alert("회원수정이 완료됐습니다.");
			location.href = "/";
						
		}).fail(function(error){
			alert(JSON.stringify(error));
		}); 
	}
	
	/*login:function(){		
		let data = {
			username: $("#username").val(), 
			password: $("#password").val()
		};
		
		$.ajax({ 	 
			type:"POST", 
			url:"/api/user/login",
			data:JSON.stringify(data), 
			contentType:"application/json;charset=utf-8", 
			dataType:"json" 
		}).done(function(resp){
			alert("로그인이 완료됐습니다.");
			location.href = "/";
						
		}).fail(function(error){
			alert(JSON.stringify(error));
		}); 
	}*/
}

index.init();