package com.capstone.HisTour.domain.alarm.repository;

import com.capstone.HisTour.domain.alarm.domain.Alarm;
import com.capstone.HisTour.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {

    List<Alarm> findByMember(Member member);
}
