package com.project.onlinechat.repository;

import com.project.onlinechat.entity.Member;
import com.project.onlinechat.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {
}