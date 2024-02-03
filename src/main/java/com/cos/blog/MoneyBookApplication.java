package com.cos.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MoneyBookApplication {

	public static void main(String[] args) {
		SpringApplication.run(MoneyBookApplication.class, args);
	}

}


// Request Process의 전통적인 방법
// 1) JDBC Connection Start
// 2) Transaction Start
// 3) Persistence Context(영속성 컨텍스트) Start
//  > 사용자마다 영속성 (객체)생성 > 응답 > 서비스에서 받고 영속성에 있는 객체값을 변경
//  > 컨트롤러로 돌아와서 응답하기 직전에 
// 4) JDBC Connection Close
// 5) Transaction Close > Commit(트랜잭션 종료 때 commit이 되며 변경을 감지해서 update가 수행됨)
// 6) Persistence Context Close 


// DB의 부하를 줄이는 방법 > 단계가 줄어들면서 DB와 연결된 시간을 줄여 부하가 줄게된다.
// Process : Request > [ Controller > Service > Repository ]여기까지가 스프링 컨테이너 > Persistence Context > DB > Response
// 1) Request단계에서는 Persistence Context(시작시점에는 비어있는 상태)만 시작됨 
// 2) Service가 시작될 때 a)Transaction과 b)JDBC 연결시작 
// 3) Service가 종료될 때 a)영속성, b)JDBC, c) Transaction 모두 종료시킨다.
// 
// 예시. 선수정보 요청
// 1) 영속성 컨텍스트 시작(비어있음)
// 2) 컨트롤러에서 분기해서 서비스한테 요청
// 3) 서비스는 선수정보에 대한 서비스를 호출
// 4) Repository에서 Select하며 Select할 때 영속성 컨텍스트한테 선수정보가 있는지 물어본다.
// 5) 영속성 컨텍스트에는 1차캐시라는 것이 있는데 여기에 선수정보에 대한 객체가 있으면 
//    DB에 가지않고 바로 돌려준다. 하지만 이 시점에는 1차캐시에 없기 때문에 DB한테 선수정보를 달라고 한다.
// 6) 그래서 DB에서 해당 선수정보를 들고온다.
// 7) 이 때, 특정 선수정보를 들고와야 하는데 선수정보는 Foreign Key로 팀ID가 연결되어있다.
// 8) 그럼 그 선수에 대한 정보는 연결된 테이블과 ManyToOne의 관계가 형성된다.
//    왜냐하면 한팀에는 여러명의 선수가 있기 때문에.
// 9) ManytoOne의 기본전략은 EAGER라서 관련된 팀정보까지 모두 Select되어 
//    1차캐시에는 검색한 선수의 정보와 선수가 속한 팀정보까지 같이 받게된다.
//10) 여기서 요청했던 선수정보만 return된다.
//11) Controller에게 돌려주며 이 시점에 영속성, JDBC, 트랜잭션이 종료된다.
//12) 연결이 끊어지면서 1차캐시에 있던 정보들은 비영속상태가 되었다고 한다 = 쓸모없음


// EAGER전략을 LAZY전략으로 바꿀 수도 있다. LAZY로딩
// EAGER에서 가져왔던 선수와 팀정보 중에서 요청하지 않았던 팀정보는 1차캐시에 안가져오게 된다.
// Controller에 들어와서 View를 만들어야 하는데 이 때 화면에 


// ***
// application.yml파일에서 jpa:  open_in_view를 true로 주게되면 LAZY로딩이 가능해진다.
// 그렇게 되면 영속성을 프리젠테이션 계층(Controller)까지 가져갈 수 있게 된다.
// 트랜잭션과 JDBC는 Service가 끝나는 시점에서 그대로 종료하는데
// 영속성 컨텍스트는 컨트롤러가 시작되고 종료되는 시점까지 이어간다. 


//> 컨트롤러로 들어와서 분기를 한다 
//> 서비스가 실행됨 > Select가 일어남 > 영속성 컨텍스트에 1차 캐시라는 것이 있는데 
// JDBC Connection Start
// 3) Transaction Start