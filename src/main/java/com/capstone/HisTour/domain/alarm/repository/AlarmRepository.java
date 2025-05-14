package com.capstone.HisTour.domain.alarm.repository;

import com.capstone.HisTour.domain.alarm.domain.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
}
