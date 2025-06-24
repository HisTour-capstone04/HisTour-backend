package com.capstone.HisTour.domain.heritage.service;

import com.capstone.HisTour.domain.api.service.ChatGPTService;
import com.capstone.HisTour.domain.api.service.OpenWeatherService;
import com.capstone.HisTour.domain.bookmark.domain.Bookmark;
import com.capstone.HisTour.domain.bookmark.repository.BookmarkRepository;
import com.capstone.HisTour.domain.heritage.domain.Heritage;
import com.capstone.HisTour.domain.heritage.domain.HeritageRecommend;
import com.capstone.HisTour.domain.heritage.dto.HeritageListResponse;
import com.capstone.HisTour.domain.heritage.dto.HeritageRecommendListResponse;
import com.capstone.HisTour.domain.heritage.dto.HeritageRecommendResponse;
import com.capstone.HisTour.domain.heritage.dto.HeritageResponse;
import com.capstone.HisTour.domain.heritage.repository.HeritageRecommendRepository;
import com.capstone.HisTour.domain.heritage.repository.HeritageRepository;
import com.capstone.HisTour.domain.member.domain.Member;
import com.capstone.HisTour.domain.member.repository.MemberRepository;
import com.capstone.HisTour.domain.visited.domain.Visited;
import com.capstone.HisTour.domain.visited.repostiory.VisitedRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HeritageService {

    private final HeritageRepository heritageRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final VisitedRepository visitedRepository;
    private final BookmarkRepository bookmarkRepository;
    private final MemberRepository memberRepository;
    private final OpenWeatherService openWeatherService;
    private final ChatGPTService chatGPTService;
    private final HeritageRecommendRepository recommendRepository;
    private final ImageApiService imageApiService;

    // 특정 유적지 조회
    public HeritageResponse getHeritageById(Long id) {

        // id를 통해서 heritage 조회
        Heritage heritage = heritageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 id를 가진 Heritage가 존재하지 않습니다."));

        // ccbakdcd, ccbaasno, ccbactcd를 이용하여 이미지 url 추출하기
        String ccbakdcd = heritage.getCategoryCode();
        String ccbaasno = heritage.getManageNum();
        String ccbactcd = heritage.getLocationCode();


        // 소수점 제거 로직
        if (ccbactcd != null && ccbactcd.endsWith(".0")) {
            ccbactcd = ccbactcd.substring(0, ccbactcd.length() - 2); // ".0" 제거
        }

        if (ccbakdcd != null && ccbakdcd.endsWith(".0")) {
            ccbakdcd = ccbakdcd.substring(0, ccbakdcd.length() - 2); // ".0" 제거
        }

        List<String> imageUrls = imageApiService.getCachedImageUrls(ccbakdcd, ccbaasno, ccbactcd);

        // HeritageResponse 반환
        return HeritageResponse.from(heritage, imageUrls);
    }

    // 근처 유적지 리스트 조회
    public HeritageListResponse getHeritageNearby(Long memberId, double latitude, double longitude, double radius) {

        // 위도, 경도, radius를 사용하여 근처 유적지 조회
        List<Heritage> heritagesNearby = heritageRepository.findNearbyHeritages(latitude, longitude, radius);

        // 조회된 것 중 15개 무작위 추출
        heritagesNearby = new ArrayList<>(heritagesNearby);
        if (!heritagesNearby.isEmpty()) {
            Collections.shuffle(heritagesNearby);
            int limit = Math.min(15, heritagesNearby.size());
            heritagesNearby = heritagesNearby.subList(0, limit);
        }

        List<HeritageResponse> heritageResponses = heritagesNearby.stream()
                .map(this::convertToHeritageResponse)
                .toList();

        // HeritageNearbyResponse 반환
        return HeritageListResponse.builder()
                .count(heritageResponses.size())
                .heritages(heritageResponses)
                .build();
    }

    // 근처 유적지 리스트 조회 및 알람 메시지 생성
    public HeritageListResponse getHeritageNearbyForAlarm(Long memberId, double latitude, double longitude, double radius) {

        // 위도, 경도, radius를 사용하여 근처 유적지 조회
        List<Heritage> heritagesNearby = heritageRepository.findNearbyHeritages(latitude, longitude, radius);

        // Redis에서 최근 본 유적지 ID 조회
        String keyPrefix = "member:" + memberId + ":recent-heritage:";

        List<Heritage> freshHeritages = new ArrayList<>();

        // 최근에 조회됐던 유적지를 제외하고 freshHeritages 에 추가
        for (Heritage heritage : heritagesNearby) {
            String key = keyPrefix + heritage.getId();
            Boolean alreadySent = redisTemplate.hasKey(key);

            if (!Boolean.TRUE.equals(alreadySent)) {
                freshHeritages.add(heritage);

                // 1시간 TTL
                redisTemplate.opsForValue().set(key, "1", Duration.ofHours(1));
            }
        }

        String message = null;
        int others = 0;

        if (!freshHeritages.isEmpty()) {
            Heritage mostClose = freshHeritages.get(0);
            others = freshHeritages.size() - 1;

            message = others == 0
                    ? "근처에 " + mostClose.getName() + " 유적지가 있습니다."
                    : "근처에 " + mostClose.getName() + " 외 " + others + "개의 유적지가 근처에 있습니다.";
        } else {
            message = "유적지가 존재하지 않습니다.";
        }

        // Heritage -> HeritageResponse List
        List<HeritageResponse> heritageResponses = freshHeritages.stream()
                .map(this::convertToHeritageResponse)
                .toList();

        // HeritageNearbyResponse 반환
        return HeritageListResponse.builder()
                .count(heritageResponses.size())
                .message(message)
                .heritages(heritageResponses)
                .build();
    }

    // 유적지 이름으로 조회
    public HeritageListResponse searchHeritageByName(String name) {

        // heritage 이름으로 검색
        List<Heritage> heritages = heritageRepository.findByNameContainingIgnoreCase(name);

        // List<HeritageResponse> 반환
        List<HeritageResponse> heritageResponseList = heritages.stream()
                .map(this::convertToHeritageResponse)
                .toList();

        return HeritageListResponse.builder()
                .count(heritageResponseList.size())
                .heritages(heritageResponseList)
                .build();
    }

    // 경로상에 있는 유적지 조회
    public HeritageListResponse searchHeritageInRoute(double srcLatitude, double srcLongitude, double destLatitude, double destLongitude) {

        // 중간 좌표의 (위도, 경도) 추출
        double[] midpoint = getMidpoint(srcLatitude, srcLongitude, destLatitude, destLongitude);

        double midLatitude = midpoint[0];
        double midLongitude = midpoint[1];

        log.info("latitude: {}, longitude: {}", midLatitude, midLongitude);

        // 두 지점 사이의 거리 계산
        double distance = calculateDistance(srcLatitude, srcLongitude, destLatitude, destLongitude);
        log.info("distance: {}", distance);

        List<Heritage> heritages = heritageRepository.findNearbyHeritages(midLatitude, midLongitude, distance/2);

        List<HeritageResponse> heritageResponseList = heritages.stream()
                .map(this::convertToHeritageResponse)
                .toList();

        return HeritageListResponse.builder()
                .count(heritageResponseList.size())
                .heritages(heritageResponseList)
                .build();
    }

    // 유적지 추천
    public HeritageRecommendListResponse recommendHeritages(Long memberId, double latitude, double longitude) {

        // member 조회
        Member foundMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        // repository에서 조회
        List<HeritageRecommend> recommends = recommendRepository.findAllByMemberAndCreatedAt(foundMember, LocalDate.now());

        if (!recommends.isEmpty()) {
            List<HeritageRecommendResponse> recommendResponses = recommends.stream()
                    .map(h -> {
                        Heritage heritage = h.getHeritage();
                        String reason = h.getReason();

                        return covertToHeritageRecommendResponse(heritage, reason);
                    })
                    .toList();

            return HeritageRecommendListResponse.builder()
                    .count(recommendResponses.size())
                    .recommendations(recommendResponses)
                    .build();
        }

        // 방문했던 유적지 조회
        List<Heritage> visitedHeritages = visitedRepository.findAllByMemberId(foundMember.getId())
                .stream()
                .map(Visited::getHeritage)
                .toList();

        // 북마크헀던 유적지 조회
        List<Heritage> bookmarkedHeritages = bookmarkRepository.findAllByMemberId(foundMember.getId())
                .stream()
                .map(Bookmark::getHeritage)
                .toList();

        // 실내, 실외 판단
        boolean isIndoorOnly = openWeatherService.isIndoorOnly(latitude, longitude);

        // 반경 10km 내 유적지 조회
        List<Heritage> nearby = heritageRepository.findNearbyHeritages(latitude, longitude, 10000);

        // 방문했던 유적지 제외
        List<Heritage> filtered = nearby.stream()
                .filter(h -> visitedHeritages.stream().noneMatch(v -> v.getId().equals(h.getId())))
                .toList();

        // 실내 필터링
        if (isIndoorOnly) {
            filtered = filtered.stream()
                    .filter(h -> h.getSide().equalsIgnoreCase("실내"))
                    .toList();
        }

        filtered = new ArrayList<>(filtered);

        if (filtered.size() > 100) {
            Collections.shuffle(filtered);
            filtered = filtered.subList(0, 100);
        }

        // chatGPT API를 통한 추천
        Map<Long, String> recommended = chatGPTService.recommendFromContext(visitedHeritages, bookmarkedHeritages, filtered);

        Map<Long, Heritage> filteredMap = filtered.stream()
                .collect(Collectors.toMap(Heritage::getId, h -> h));

        List<HeritageRecommendResponse> recommendResponses = recommended.entrySet().stream()
                .map(entry -> {
                    Long id = entry.getKey();
                    String reason = entry.getValue();
                    Heritage heritage = filteredMap.get(id);
                    if (heritage == null) return null;
                    recommendRepository.save(HeritageRecommend.builder()
                            .member(foundMember)
                            .heritage(heritage)
                            .reason(reason)
                            .build());
                    return covertToHeritageRecommendResponse(heritage, reason);
                })
                .filter(Objects::nonNull)
                .toList();

        return HeritageRecommendListResponse.builder()
                .count(recommendResponses.size())
                .recommendations(recommendResponses)
                .build();
    }

//    // heritage image url 가져오기
//    @Cacheable(value = "heritageImageUrls", key = "#ccbaKdcd + '_' + #ccbaAsno + '_' + #ccbaCtcd")
//    public List<String> getImageUrls(String ccbaKdcd, String ccbaAsno, String ccbaCtcd) {
//        System.out.println("DEBUG: Calling external image API for " + ccbaKdcd + "/" + ccbaAsno + "/" + ccbaCtcd); // 캐시 히트/미스 확인용
//        String url = "https://www.khs.go.kr/cha/SearchImageOpenapi.do" +
//                "?ccbaKdcd=" + ccbaKdcd +
//                "&ccbaAsno=" + ccbaAsno +
//                "&ccbaCtcd=" + ccbaCtcd;
//
//        String xmlString = restClient.get()
//                .uri(url)
//                .accept(MediaType.APPLICATION_XML)
//                .retrieve()
//                .body(String.class);
//
//        return parseImageUrl(xmlString);
//    }

//    // xml 파싱해서 image url 추출
//    private List<String> parseImageUrl(String xmlString) {
//        List<String> imageUrls = new ArrayList<>();
//
//        try {
//            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//            DocumentBuilder builder = factory.newDocumentBuilder();
//
//            // 문자열 -> InputStream -> Document
//            ByteArrayInputStream input = new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8));
//            Document doc = builder.parse(input);
//
//            // 모든 <imageUrl> 태그 찾기
//            NodeList imageUrlNodes = doc.getElementsByTagName("imageUrl");
//
//            for (int i = 0; i < imageUrlNodes.getLength(); i++) {
//                Node imageUrlNode = imageUrlNodes.item(i);
//                String url = imageUrlNode.getTextContent().trim();
//                imageUrls.add(url);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//
//        return imageUrls;
//    }

    // 좌표 2개의 mid point 추출
    private double[] getMidpoint(double lat1, double lon1, double lat2, double lon2) {

        // convert to radians
        lat1 = Math.toRadians(lat1);
        lon1 = Math.toRadians(lon1);
        lat2 = Math.toRadians(lat2);
        lon2 = Math.toRadians(lon2);

        double dLon = lon2 - lon1;

        double bx = Math.cos(lat2) * Math.cos(dLon);
        double by = Math.cos(lat2) * Math.sin(dLon);

        double lat3 = Math.atan2(
                Math.sin(lat1) + Math.sin(lat2),
                Math.sqrt((Math.cos(lat1) + bx) * (Math.cos(lat1) + bx) + by * by)
        );
        double lon3 = lon1 + Math.atan2(by, Math.cos(lat1) + bx);

        // 라디안 -> 도
        return new double[]{Math.toDegrees(lat3), Math.toDegrees(lon3)};
    }

    // 두 좌표 사이의 거리 계산
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // 도 -> 라디안 변환
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        // Haversine 공식
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return 6371.0 * c * 1000;
    }

    private HeritageResponse convertToHeritageResponse(Heritage heritage) {
        String ccbakdcd = heritage.getCategoryCode();
        String ccbaasno = heritage.getManageNum();
        String ccbactcd = heritage.getLocationCode();

        // 소수점 제거 로직
        if (ccbactcd != null && ccbactcd.endsWith(".0")) {
            ccbactcd = ccbactcd.substring(0, ccbactcd.length() - 2);
        }

        if (ccbakdcd != null && ccbakdcd.endsWith(".0")) {
            ccbakdcd = ccbakdcd.substring(0, ccbakdcd.length() - 2);
        }

        List<String> imageUrls = imageApiService.getCachedImageUrls(ccbakdcd, ccbaasno, ccbactcd);

        return HeritageResponse.from(heritage, imageUrls);
    }

    public HeritageRecommendResponse covertToHeritageRecommendResponse(Heritage heritage, String reason) {
        String ccbakdcd = heritage.getCategoryCode();
        String ccbaasno = heritage.getManageNum();
        String ccbactcd = heritage.getLocationCode();

        // 소수점 제거 로직
        if (ccbactcd != null && ccbactcd.endsWith(".0")) {
            ccbactcd = ccbactcd.substring(0, ccbactcd.length() - 2);
        }

        if (ccbakdcd != null && ccbakdcd.endsWith(".0")) {
            ccbakdcd = ccbakdcd.substring(0, ccbakdcd.length() - 2);
        }

        List<String> imageUrls = imageApiService.getCachedImageUrls(ccbakdcd, ccbaasno, ccbactcd);

        return HeritageRecommendResponse.from(heritage, imageUrls, reason);
    }
}
