package com.capstone.HisTour.domain.heritage.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
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
@Slf4j
public class ImageApiService {

    private final RestClient restClient;

    @Cacheable(value = "heritageImageUrls", key = "#ccbaKdcd + '_' + #ccbaAsno + '_' + #ccbaCtcd")
    public List<String> getCachedImageUrls(String ccbaKdcd, String ccbaAsno, String ccbaCtcd) {
        log.debug("DEBUG: Calling external image API for {} / {}  / {} ", ccbaKdcd, ccbaAsno, ccbaCtcd);
        String url = "https://www.khs.go.kr/cha/SearchImageOpenapi.do" +
                "?ccbaKdcd=" + ccbaKdcd +
                "&ccbaAsno=" + ccbaAsno +
                "&ccbaCtcd=" + ccbaCtcd;

        log.info("DEBUG: Calling external image API for {}", url);

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
