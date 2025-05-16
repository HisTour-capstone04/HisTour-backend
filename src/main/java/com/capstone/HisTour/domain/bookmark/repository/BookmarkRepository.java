package com.capstone.HisTour.domain.bookmark.repository;

import com.capstone.HisTour.domain.bookmark.domain.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    List<Bookmark> findAllByMemberId(Long memberId);

    boolean existsByMemberIdAndHeritageId(Long memberId, Long heritageId);
}
