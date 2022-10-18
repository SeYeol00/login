package hello.login;

import hello.login.web.argumentresolver.LoginMemberArgumentResolver;
import hello.login.web.filter.LogFilter;
import hello.login.web.filter.LoginCheckFilter;
import hello.login.web.interceptor.LogInterceptor;
import hello.login.web.interceptor.LoginCheckInterceptor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.Filter;
import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {


    @Override // 아규먼트 리졸버 등록은 이걸 오버라이드 해야한다.
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LoginMemberArgumentResolver());
    }

    @Override // 인터셉터 등록은 이걸 오버라이드 해야한다.
    public void addInterceptors(InterceptorRegistry registry) {

        //인터셉터 등록 방식
        registry.addInterceptor(new LogInterceptor())
                .order(1) // 인터셉터 우선순위
                .addPathPatterns("/**") // 인터셉터 먹일 URL
                .excludePathPatterns("/css/**","/*.ico","/error"); // 화이트리스트 느낌, 제외할 URL

        // 경로에 대해서 세밀하게 조정이 가능하다.
        registry.addInterceptor(new LoginCheckInterceptor())
                .order(2)
                .addPathPatterns("/**")
                .excludePathPatterns("/","/members/add","/login","/logout",
                        "/css/**","/*.ico","/error");

    }

    //@Bean
    public FilterRegistrationBean logFilter(){
        //  내가 만든 필터 등록
        FilterRegistrationBean<Filter> filterFilterRegistrationBean = new FilterRegistrationBean<>();
        filterFilterRegistrationBean.setFilter(new LogFilter());
        filterFilterRegistrationBean.setOrder(1); // 첫 순위로
        filterFilterRegistrationBean.addUrlPatterns("/*"); // 모든 url들을 대상 으로 필터 적용
        return filterFilterRegistrationBean;
    }

    //@Bean
    public FilterRegistrationBean loginCheckFilter(){
        //  내가 만든 필터 등록
        FilterRegistrationBean<Filter> filterFilterRegistrationBean = new FilterRegistrationBean<>();
        filterFilterRegistrationBean.setFilter(new LoginCheckFilter());
        filterFilterRegistrationBean.setOrder(2); // 두번째 순위로
        // 미래에 새로운 URL이 나오더라도 적용시키기 위해
        filterFilterRegistrationBean.addUrlPatterns("/*"); // 모든 url들을 대상 으로 필터 적용
        return filterFilterRegistrationBean;
    }
}
