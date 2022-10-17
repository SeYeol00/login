package hello.login.domain.member;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Repository
public class MemberRepository {


    private static Map<Long, Member> store = new ConcurrentHashMap<>(); // static 사용

    private static long sequence = 0L; // static 사용, 만들어질 때만다 글로벌 변수로 증가

    public Member save(Member member){
        member.setId(sequence++);
        log.info("save: member={}",member);

        store.put(member.getId(), member);
        return member;
    }

    public Member findById(Long id){
        return store.get(id);
    }

    public List<Member> findAll(){
        return new ArrayList<>(store.values());
    }

    public Optional<Member> findByLoginId(String loginId){
//        List<Member> all = findAll();
//        for (Member m : all) {
//            if(m.getLoginId().equals(loginId)){
//                return Optional.of(m);
//            }
//        }
//        return Optional.empty();

        // 람다식 표현 요즘은 기본으로 써야한다.
        return findAll().stream()//리스트를 스트림으로 바꾼다.
                // filter는 sql의 where절 느낌으로 보면 된다. 조건식
                // 즉 filter에 if 조건절을 넣어 반환하는 것이다.
                .filter(m->m.getLoginId().equals(loginId))
                .findFirst();
    }

    public void clearStore(){
        store.clear();
    }
}
