package com.cos.blog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.cos.blog.config.auth.PrincipalDetailService;

// 이 클래스가 bean으로 등록되어야 한다 = 스프링 컨테이너에서 객체를 관리할 수 있게 해야한다 = 빈등록(IoC로 관리)
// 아래 세개의 애노테이션은 시큐리티 적용 시 필수(세트)
@Configuration // @Configuration를 붙이면 이 클래스는 스프링에 빈이 등록된다
@EnableWebSecurity // 시큐리티라는 필터가 등록되며 설정은 아래에서 한다. 
@EnableGlobalMethodSecurity(prePostEnabled=true) // 특정 주소로 접근하면 권한 및 인증을 미리 체크하겠다라는 뜻
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	// DI해준다
	@Autowired
	private PrincipalDetailService principalDetailService; 
	
	// alt + shift + s > Override/Implement Methods에서 추가
	@Bean	
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	// 이 때부터는 IoC가 된다 > 이 함수가 return하는 값을 스프링이 관리한다.
	@Bean 
	public BCryptPasswordEncoder encodePWD() {		
		// String encPassword = new BCryptPasswordEncoder().encode("1234");
		return new BCryptPasswordEncoder();
	}
	
	// 시큐리티가 대신 로그인해주는데 password를 가로채기 하는데
	// 해당 password가 뭐로 해쉬되어 회원가입 되었는지 알아야 
	// 같은 해쉬로 암호화해서 DB에 있는 해쉬랑 비교할 수 있음.
	// conf를 치고 ctrl + spacebar를 누르면 입력가능한 함수목록이 뜬다.
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(principalDetailService).passwordEncoder(encodePWD());
	}
	
	// WebSecurity도 있음 + .ignoring()
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		// 시큐리티가 모든 요청이 컨트롤러로 가서 어떤 함수가 실행되기 전에 가로채서 아래를 동작한다.
		http
			.csrf().disable() // csrf토큰 비활성화(테스트시 걸어두는게 좋음)
			// 시큐리티가 csrf토큰을 가진 사용자만 접근을 허용하면서 외부에서는 접근이 불가하게 됨 
			.authorizeRequests()
			.antMatchers("/", "/auth/**", "/js/**", "/css/**", "/image/**", "/dummy/**") // /auth경로로 들어오면 누구나 접근할 수 있다.
			//.antMatchers("/", "/auth/**", "/js/**", "/css/**", "/image/**", "/dummy/**", "/user/**") // /user경로로 들어오면 사용자 전체데이터 조회가능
			.permitAll()
			.anyRequest() // 그게 아닌 모든 주소는 아래와 같이 인증이 되어야 한다.
			.authenticated()  // 이게아닌 다른 모든 요청은 authenticated(인증) 되어야 한다.
			// 필터링이 되는 것이다.
		.and()
			.formLogin()
			.loginPage("/auth/loginForm")
		// 인증이 필요하면 이곳으로 이동한다
		// 메인페이지('/')는 auth가 붙어있지 않아서 이쪽으로 이동시켜야 한다.
			.loginProcessingUrl("/auth/loginProc") 
		// UserApiController에 놓지 않는 이유 
		// 스프링 시큐리티가 로그인을 가로채서 대신 로그인 해주기 위해
			.defaultSuccessUrl("/"); // 로그인 프로세스가 완료되면 해당경로로 이동한다.
		//	.failureUrl("/fail"); // 실패하면 /fail로 가겠다.
	}
}
