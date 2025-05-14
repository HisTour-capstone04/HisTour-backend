package com.capstone.HisTour.domain.heritage.service;

import com.capstone.HisTour.domain.heritage.domain.Heritage;
import com.capstone.HisTour.domain.heritage.dto.HeritageNearbyResponse;
import com.capstone.HisTour.domain.heritage.dto.HeritageResponse;
import com.capstone.HisTour.domain.heritage.repository.HeritageRepository;
import com.capstone.HisTour.global.annotation.MeasureExecutionTime;
import lombok.RequiredArgsConstructor;
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
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HeritageService {

    private final HeritageRepository heritageRepository;
    private final RestClient restClient;

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
    public HeritageNearbyResponse getHeritageNearby(double latitude, double longitude, double radius) {

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
        return HeritageNearbyResponse.builder()
                .count(heritageResponses.size())
                .heritages(heritageResponses)
                .build();
    }

    // 유적지 이름으로 조회
    public List<HeritageResponse> searchHeritageByName(String name) {

        // heritage 이름으로 검색
        List<Heritage> heritages = heritageRepository.findByNameContainingIgnoreCase(name);

        // List<HeritageResponse> 반환
        return heritages.stream()
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
}
