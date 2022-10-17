package hello.login.web.login;


import hello.login.domain.login.LoginService;
import hello.login.domain.member.Member;
import hello.login.web.SessionConst;
import hello.login.web.session.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
@Controller
@RequiredArgsConstructor

public class LoginController {

    private final LoginService loginService;
    private final SessionManager sessionManager;


    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginForm") LoginForm loginForm){
        return "login/loginForm";
    }

    //@PostMapping("/login")
    public String login(@Validated  @ModelAttribute("loginForm") LoginForm loginForm,
                        BindingResult bindingResult,
                        // 리스폰스에 쿠키를 넣을 것이다.
                        HttpServletResponse response){
        // 아이디나 비밀번호가 빈 경우
        if(bindingResult.hasErrors()){
            return "login/loginForm";
        }
        // 로그인 비즈니스 로직
        Member loginMember = loginService.login(loginForm.getLoginId(), loginForm.getPassword());
        // 로그인 실패시
        if(loginMember == null){
            // 글로벌 에러
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";
        }

        // 로그인 성공 처리 - 쿠키 방식
        // 쿠키에 시간 정보를 주지 않으면 세션 쿠키(브라우저 종료시 모두 종료)
        Cookie idCookie = new Cookie("memberId", String.valueOf(loginMember.getId()));
        response.addCookie(idCookie);
        return "redirect:/";
    }


   // @PostMapping("/login")
    public String loginV2(@Validated  @ModelAttribute("loginForm") LoginForm loginForm,
                        BindingResult bindingResult,
                        // 리스폰스에 쿠키를 넣을 것이다.
                        HttpServletResponse response){
        // 아이디나 비밀번호가 빈 경우
        if(bindingResult.hasErrors()){
            return "login/loginForm";
        }
        // 로그인 비즈니스 로직
        Member loginMember = loginService.login(loginForm.getLoginId(), loginForm.getPassword());
        // 로그인 실패시
        if(loginMember == null){
            // 글로벌 에러
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";
        }

        // 로그인 성공 처리
        // 세션 관리자를 통해 세션을 생성하고, 회원 데이터를 보관
        sessionManager.createSession(loginMember,response);
        return "redirect:/";
    }

    @PostMapping("/login")
    public String loginV3(@Validated  @ModelAttribute("loginForm") LoginForm loginForm,
                          BindingResult bindingResult,
                          // 리스폰스에 쿠키를 넣을 것이다.
                          HttpServletResponse response,
                          // 자바에 있는 세션 사용
                          HttpServletRequest request){
        // 아이디나 비밀번호가 빈 경우
        if(bindingResult.hasErrors()){
            return "login/loginForm";
        }
        // 로그인 비즈니스 로직
        Member loginMember = loginService.login(loginForm.getLoginId(), loginForm.getPassword());
        // 로그인 실패시
        if(loginMember == null){
            // 글로벌 에러
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";
        }

        // 로그인 성공 처리
        // 세션이 있으면 세션 반환, 없으면 신규 세션을 생성
        HttpSession session = request.getSession();

        // 세션에 로그인 회원 정보 보관
        // 세션의 키값, 벨류값
        session.setAttribute(SessionConst.LOGIN_MEMBER,loginMember);


        // 세션 관리자를 통해 세션을 생성하고, 회원 데이터를 보관
//        sessionManager.createSession(loginMember,response);
        return "redirect:/";
    }


    //@PostMapping("/logout")
    public String logout(HttpServletResponse response){
        expireCookie(response,"memberId");
        return "redirect:/";

    }

    //@PostMapping("/logout")
    public String logoutV2(HttpServletResponse response, HttpServletRequest request){
        sessionManager.expire(request);
        return "redirect:/";

    }

    @PostMapping("/logout")
    public String logoutV3(HttpServletResponse response, HttpServletRequest request){
        // 세션을 없애는게 목적, 세션이 없을 때 false면 null 반환
        HttpSession session = request.getSession(false);
        if(session!=null){
            // 세션 다 날라감
            session.invalidate();
        }
        return "redirect:/";

    }

    private void expireCookie(HttpServletResponse response, String cookieName) {
        // 쿠키는 하나만 되나보다...? 그래서 기존 껄 덮어버린 걸지도
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }



}
