package com.devjaewoo.springredistest.repository;

import com.devjaewoo.springredistest.entity.Member;
import org.springframework.data.repository.CrudRepository;

public interface MemberRedisRepository extends CrudRepository<Member, String> {
}
