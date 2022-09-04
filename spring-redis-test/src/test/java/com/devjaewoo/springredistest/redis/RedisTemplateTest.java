package com.devjaewoo.springredistest.redis;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SpringBootTest
public class RedisTemplateTest {

    @Autowired RedisTemplate<String, Object> redisTemplate;

    @Test
    public void opsForValue() throws Exception {
        //given
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        String key = "stringKey";
        String value = "stringValue";

        valueOperations.set(key, value);

        //when
        String result = (String) valueOperations.get(key);

        //then
        Assertions.assertThat(result).isEqualTo(value);
    }

    @Test
    public void opsForList() throws Exception {
        //given
        ListOperations<String, Object> listOperations = redisTemplate.opsForList();
        String listKey = "listKey";

        List<Long> list = List.of(1L, 2L, 3L, 4L, 5L);

        //when
        for(Long l : list) {
            listOperations.leftPush(listKey, l);
        }

        //then
        for (Long l : list) {
            Long result = (Long) listOperations.rightPop(listKey);
            Assertions.assertThat(result).isEqualTo(l);
        }
    }

    @Test
    public void opsForSet() throws Exception {
        //given
        SetOperations<String, Object> setOperations = redisTemplate.opsForSet();
        String setKey = "setKey";

        //when
        setOperations.add(setKey, "h", "e", "l", "l", "o");
        Set<String> result = setOperations.members(setKey).stream().map(it -> (String) it).collect(Collectors.toSet());

        //then
        Assertions.assertThat(result).containsOnly("h", "e", "l", "o");
    }
}
