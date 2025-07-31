package com.pocai.controller;

import com.pocai.dto.GeminiResponse;
import com.pocai.service.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    private GeminiService geminiService;

    @PostMapping("/message")
    public Mono<GeminiResponse> sendMessage(@RequestParam String message, 
                                           @RequestParam(required = false) String sessionId) {
        return geminiService.sendMessage(message, sessionId);
    }

    @PostMapping("/start")
    public Mono<GeminiResponse> startNewChat(@RequestParam String message) {
        return geminiService.startNewSession(message);
    }

    @GetMapping("/test")
    public Mono<String> test() {
        return Mono.just("Gemini TRPG Chat API is working!");
    }
}