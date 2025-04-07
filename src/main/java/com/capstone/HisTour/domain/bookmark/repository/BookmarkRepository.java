package com.capstone.HisTour.domain.bookmark.repository;

import com.capstone.HisTour.domain.bookmark.domain.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
}
