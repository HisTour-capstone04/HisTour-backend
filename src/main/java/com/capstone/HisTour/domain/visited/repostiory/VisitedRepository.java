package com.capstone.HisTour.domain.visited.repostiory;

import com.capstone.HisTour.domain.visited.domain.Visited;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VisitedRepository extends JpaRepository<Visited, Long> {

}
