package com.capstone.HisTour.domain.tts.controller;

import com.capstone.HisTour.domain.tts.service.TTSService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TTSController {

    private final TTSService ttsService;

    @GetMapping("/convert-to-speech")
    public ResponseEntity<byte[]> convertTextToSpeech(@RequestParam String text) throws Exception {
        byte[] audio = ttsService.convertTextToSpeech(text);
        return ResponseEntity.ok()
                .header("Content-Type", "audio/mpeg")
                .body(audio);
    }

}
