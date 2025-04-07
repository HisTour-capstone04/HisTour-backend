package com.capstone.HisTour.domain.bookmark.service;

import com.capstone.HisTour.domain.bookmark.domain.Bookmark;
import com.capstone.HisTour.domain.bookmark.dto.BookmarkRequest;
import com.capstone.HisTour.domain.bookmark.dto.BookmarkResponse;
import com.capstone.HisTour.domain.bookmark.repository.BookmarkRepository;
import com.capstone.HisTour.domain.heritage.domain.Heritage;
import com.capstone.HisTour.domain.heritage.repository.HeritageRepository;
import com.capstone.HisTour.domain.member.domain.Member;
import com.capstone.HisTour.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final MemberRepository memberRepository;
    private final HeritageRepository heritageRepository;

    // 북마크 추가
    public BookmarkResponse addBookmark(BookmarkRequest bookmarkRequest) {

        Member member = memberRepository.findById(bookmarkRequest.getMemberId())
                .orElseThrow(() -> new RuntimeException("해당 멤버가 존재하지 않습니다."));

        Heritage heritage = heritageRepository.findById(bookmarkRequest.getHeritageId())
                .orElseThrow(() -> new RuntimeException("해당 유적지가 존재하지 않습니다."));

        Bookmark bookmark = new Bookmark(member, heritage);
        bookmark.setCreatedAt(LocalDateTime.now());

        return BookmarkResponse.from(bookmarkRepository.save(bookmark));
    }
}
