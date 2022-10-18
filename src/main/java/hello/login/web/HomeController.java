package hello.login.web;

import hello.login.domain.member.Member;
import hello.login.domain.member.MemberRepository;
import hello.login.web.argumentresolver.Login;
import hello.login.web.session.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final MemberRepository memberRepository;
    private final SessionManager sessionManager;

    //@GetMapping("/")
    public String home() {
        return "home";
    }

    //@GetMapping("/") // 쿠키를 꺼내는 파라미터, required=false는 로그인 안 한 사람도 보게 만드는 거
    public String homeLogin(@CookieValue(name = "memberId",required = false) Long memberId, Model model){
        if(memberId==null){
            return "home";
        }

        // 로그인
        Member loginMember = memberRepository.findById(memberId);
        if(loginMember==null){
            return "home";
        }

        model.addAttribute("member",loginMember);
        return "loginHome";
    }

    //@GetMapping("/")
    public String homeLoginV2(HttpServletRequest request, Model model){


        // 세션 관리자에 저장된 회원 정보 조회
        Member loginMember = (Member)sessionManager.getSession(request);

        // 로그인
        if(loginMember==null){
            return "home";
        }

        model.addAttribute("member",loginMember);
        return "loginHome";
    }
    //@GetMapping("/")
    public String homeLoginV3(HttpServletRequest request, Model model){

        // 홈 화면에 처음 들어오자마자 세션이 생길 수 있기 때문에 일단 false
        // 세션을 만들 의도는 맨 처음에는 없다.
        HttpSession session = request.getSession(false);
        if(session==null){
            return "home";
        }

        // 세션 벨류 꺼내기
        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MEMBER);
        // 로그인
        if(loginMember==null){
            return "home";
        }

        model.addAttribute("member",loginMember);
        return "loginHome";
    }
    //@GetMapping("/")// 이 에노테이션으로 위 로직을 그냥 다 해준다.
    public String homeLoginV3Spring(// 캬 이 기능은 세션을 생성하지 않는다.
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false)
            Member loginMember, Model model){

        // 로그인
        if(loginMember==null){
            return "home";
        }

        model.addAttribute("member",loginMember);
        return "loginHome";
    }

    @GetMapping("/")// 이 에노테이션으로 위 로직을 그냥 다 해준다.
    public String homeLoginV3ArgumentResolver(// 캬 이 기능은 세션을 생성하지 않는다.
                                    @Login Member loginMember,
                                              Model model){

        // 로그인
        if(loginMember==null){
            return "home";
        }

        model.addAttribute("member",loginMember);
        return "loginHome";
    }


}