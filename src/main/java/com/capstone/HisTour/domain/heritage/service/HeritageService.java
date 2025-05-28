package com.capstone.HisTour.domain.heritage.service;

import com.capstone.HisTour.domain.heritage.domain.Heritage;
import com.capstone.HisTour.domain.heritage.dto.HeritageListResponse;
import com.capstone.HisTour.domain.heritage.dto.HeritageResponse;
import com.capstone.HisTour.domain.heritage.repository.HeritageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HeritageService {

    private final HeritageRepository heritageRepository;
    private final RestClient restClient;
    private final RedisTemplate<String, String> redisTemplate;

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

        List<String> imageUrls = getImageUrls(ccbakdcd, ccbaasno, ccbactcd);

        // HeritageResponse 반환
        return HeritageResponse.from(heritage, imageUrls);
    }

    // 근처 유적지 리스트 조회
    public HeritageListResponse getHeritageNearby(Long memberId, double latitude, double longitude, double radius) {

        // 위도, 경도, radius를 사용하여 근처 유적지 조회
        List<Heritage> heritagesNearby = heritageRepository.findNearbyHeritages(latitude, longitude, radius);

        List<HeritageResponse> heritageResponses = heritagesNearby.stream().map(heritage -> {
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

                    List<String> imageUrls = getImageUrls(ccbakdcd, ccbaasno, ccbactcd);

                    return HeritageResponse.from(heritage, imageUrls);
                })
                .toList();

        // HeritageNearbyResponse 반환
        return HeritageListResponse.builder()
                .count(heritageResponses.size())
                .heritages(heritageResponses)
                .build();
    }

    // 근처 유적지 리스트 조회
    public HeritageListResponse getHeritageNearbyForAlarm(Long memberId, double latitude, double longitude, double radius) {

        // 위도, 경도, radius를 사용하여 근처 유적지 조회
        List<Heritage> heritagesNearby = heritageRepository.findNearbyHeritages(latitude, longitude, radius);

        // Redis에서 최근 본 유적지 ID 조회
        String redisKey = "member:" + memberId + ":recent-heritage";
        List<String> viewedIds = redisTemplate.opsForList().range(redisKey, 0, -1);

        Set<Long> viewedIdSet = viewedIds != null
                ? viewedIds.stream().map(Long::valueOf).collect(Collectors.toSet())
                : Collections.emptySet();

        // 중복 제거
        List<Heritage> filteredHeritages = heritagesNearby.stream()
                .filter(heritage -> !viewedIdSet.contains(heritage.getId()))
                .toList();

        if (filteredHeritages.isEmpty()) {
            throw new NoSuchElementException("근처에 새로운 유적지가 없습니다.");
        }
        Heritage mostCLoseHeritage = filteredHeritages.get(0);

        String message = mostCLoseHeritage.getName() + " 외 " + filteredHeritages.size() + "개의 유적지가 근처에 있습니다.";

        // 👉 Redis에 새로 조회된 유적지 ID들 업데이트 (중복 제거 후만 추가)
        List<String> newHeritageIds = filteredHeritages.stream()
                .map(heritage -> String.valueOf(heritage.getId()))
                .toList();

        if (!newHeritageIds.isEmpty()) {
            redisTemplate.delete(redisKey); // 기존 내용 제거 (선택사항: 덧붙이지 않으려면)
            redisTemplate.opsForList().rightPushAll(redisKey, newHeritageIds);
        }

        List<HeritageResponse> heritageResponses = heritagesNearby.stream().map(heritage -> {
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

            List<String> imageUrls = getImageUrls(ccbakdcd, ccbaasno, ccbactcd);

            return HeritageResponse.from(heritage, imageUrls);
        })
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
                .map(heritage -> {
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

                    List<String> imageUrls = getImageUrls(ccbakdcd, ccbaasno, ccbactcd);

                    return HeritageResponse.from(heritage, imageUrls);
                })
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
                .map(heritage -> {
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

                    List<String> imageUrls = getImageUrls(ccbakdcd, ccbaasno, ccbactcd);

                    return HeritageResponse.from(heritage, imageUrls);
                })
                .toList();

        return HeritageListResponse.builder()
                .count(heritageResponseList.size())
                .heritages(heritageResponseList)
                .build();
    }

    // heritage image url 가져오기
    private List<String> getImageUrls(String ccbaKdcd, String ccbaAsno, String ccbaCtcd) {

        String url = "https://www.khs.go.kr/cha/SearchImageOpenapi.do" +
                "?ccbaKdcd=" + ccbaKdcd +
                "&ccbaAsno=" + ccbaAsno +
                "&ccbaCtcd=" + ccbaCtcd;

        //System.out.println(url);

        String xmlString = restClient.get()
                .uri(url)
                .accept(MediaType.APPLICATION_XML)
                .retrieve()
                .body(String.class);

        return parseImageUrl(xmlString);
    }

    // xml 파싱해서 image url 추출
    private List<String> parseImageUrl(String xmlString) {
        List<String> imageUrls = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // 문자열 -> InputStream -> Document
            ByteArrayInputStream input = new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8));
            Document doc = builder.parse(input);

            // 모든 <imageUrl> 태그 찾기
            NodeList imageUrlNodes = doc.getElementsByTagName("imageUrl");

            for (int i = 0; i < imageUrlNodes.getLength(); i++) {
                Node imageUrlNode = imageUrlNodes.item(i);
                String url = imageUrlNode.getTextContent().trim();
                imageUrls.add(url);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return imageUrls;
    }

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
}
