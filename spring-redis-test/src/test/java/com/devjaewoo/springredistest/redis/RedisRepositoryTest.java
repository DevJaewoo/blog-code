package com.devjaewoo.springredistest.redis;

import com.devjaewoo.springredistest.entity.Member;
import com.devjaewoo.springredistest.repository.MemberRedisRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
public class RedisRepositoryTest {

    @Autowired MemberRedisRepository memberRedisRepository;

    @Test
    public void save() throws Exception {
        //given
        Member member = new Member("member", 23);
        memberRedisRepository.save(member);

        //when
        Member result = memberRedisRepository.findById(member.getId()).orElseThrow();

        //then
        Assertions.assertThat(result.getName()).isEqualTo(member.getName());
    }

    @Test
    public void delete() throws Exception {
        //given
        Member member = new Member("member", 23);
        memberRedisRepository.save(member);

        //when
        memberRedisRepository.delete(member);
        Member result = memberRedisRepository.findById(member.getId()).orElse(null);

        //then
        Assertions.assertThat(result).isNull();
    }
}
