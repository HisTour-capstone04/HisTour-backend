package com.capstone.HisTour.domain.bookmark.service;

import com.capstone.HisTour.domain.apiPayload.exception.handler.BookmarkHandler;
import com.capstone.HisTour.domain.apiPayload.exception.handler.HeritageHandler;
import com.capstone.HisTour.domain.apiPayload.exception.handler.MemberHandler;
import com.capstone.HisTour.domain.apiPayload.status.ErrorStatus;
import com.capstone.HisTour.domain.bookmark.domain.Bookmark;
import com.capstone.HisTour.domain.bookmark.dto.BookmarkDeleteRequest;
import com.capstone.HisTour.domain.bookmark.dto.BookmarkListResponse;
import com.capstone.HisTour.domain.bookmark.dto.BookmarkRequest;
import com.capstone.HisTour.domain.bookmark.dto.BookmarkResponse;
import com.capstone.HisTour.domain.bookmark.repository.BookmarkRepository;
import com.capstone.HisTour.domain.heritage.domain.Heritage;
import com.capstone.HisTour.domain.heritage.dto.HeritageResponse;
import com.capstone.HisTour.domain.heritage.repository.HeritageRepository;
import com.capstone.HisTour.domain.heritage.service.HeritageService;
import com.capstone.HisTour.domain.member.domain.Member;
import com.capstone.HisTour.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final MemberRepository memberRepository;
    private final HeritageRepository heritageRepository;
    private final HeritageService heritageService;

    // 북마크 추가
    public BookmarkResponse addBookmark(Long memberId, BookmarkRequest bookmarkRequest) {

        // 멤버 유효성 검증
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        Heritage heritage = heritageRepository.findById(bookmarkRequest.getHeritageId())
                .orElseThrow(() -> new HeritageHandler(ErrorStatus.HERITAGE_NOT_FOUND));

        // 유저의 bookmark 조회
        boolean isDuplicate = bookmarkRepository.existsByMemberIdAndHeritageId(member.getId(), heritage.getId());

        if (isDuplicate)
            throw new BookmarkHandler(ErrorStatus.BOOKMARK_DUPLICATE);

        Bookmark bookmark = new Bookmark(member, heritage);
        bookmark.setCreatedAt(LocalDateTime.now());

        return BookmarkResponse.from(bookmarkRepository.save(bookmark));
    }

    // 북마크 조회
    public BookmarkListResponse getBookmarkList(Long memberId) {

        // 멤버 유효성 검증
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        // 북마크 member id로 조회
        List<Bookmark> bookmarks = bookmarkRepository.findAllByMemberId(memberId);

        // bookmark를 토대로 heritage 조회 및 DTO 변환
        List<HeritageResponse> heritageResponses = bookmarks.stream()
                .map(bookmark -> heritageService.getHeritageById(bookmark.getHeritage().getId()))
                .toList();

        return BookmarkListResponse.from(heritageResponses);

    }

    // 북마크 삭제
    public void deleteBookmark(Long memberId, BookmarkDeleteRequest deleteRequest) {

        // 멤버 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        // 북마크 조회
        Bookmark bookmark = bookmarkRepository.findByMemberIdAndId(member.getId(), deleteRequest.getBookmarkId())
                .orElseThrow(() -> new BookmarkHandler(ErrorStatus.BOOKMARK_NOT_FOUND));

        // 북마크 삭제
        bookmarkRepository.delete(bookmark);
    }
}
