package com.pocai.controller;

import com.pocai.dto.GeminiResponse;
import com.pocai.service.GeminiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    @Autowired
    private GeminiService geminiService;
    
    // 세계관별 채팅 메시지 저장 (임시 WebSocket 대체용)
    private final Map<String, List<Map<String, Object>>> worldMessages = new ConcurrentHashMap<>();

    @PostMapping("/message")
    public GeminiResponse sendMessage(@RequestParam String message, 
                                     @RequestParam(required = false) String sessionId) {
        logger.info("Gemini 메시지 전송: {}", message);
        return geminiService.sendMessage(message, sessionId);
    }

    @PostMapping("/start")
    public GeminiResponse startNewChat(@RequestParam String message) {
        logger.info("새 채팅 세션 시작: {}", message);
        return geminiService.startNewSession(message);
    }

    @GetMapping("/test")
    public String test() {
        logger.info("테스트 API 호출됨");
        return "Gemini TRPG Chat API is working!";
    }
    
    // 임시 멀티플레이어 채팅 (WebSocket 대체)
    @PostMapping("/send/{worldType}")
    public Map<String, Object> sendChatMessage(
            @PathVariable String worldType,
            @RequestBody Map<String, Object> messageData) {
        
        logger.info("채팅 메시지 수신: {} -> {}", worldType, messageData);
        
        Map<String, Object> message = new HashMap<>();
        message.put("playerId", messageData.get("playerId"));
        message.put("playerName", messageData.get("playerName"));
        message.put("message", messageData.get("message"));
        message.put("timestamp", System.currentTimeMillis());
        
        // 세계관별 메시지 목록에 추가
        worldMessages.computeIfAbsent(worldType, k -> new ArrayList<>()).add(message);
        
        // 최근 50개 메시지만 유지
        List<Map<String, Object>> messages = worldMessages.get(worldType);
        if (messages.size() > 50) {
            messages.remove(0);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("messageId", System.currentTimeMillis());
        return response;
    }
    
    @GetMapping("/messages/{worldType}")
    public Map<String, Object> getMessages(
            @PathVariable String worldType,
            @RequestParam(defaultValue = "0") long since) {
        
        List<Map<String, Object>> messages = worldMessages.getOrDefault(worldType, new ArrayList<>());
        
        // since 타임스탬프 이후의 메시지만 필터링
        List<Map<String, Object>> newMessages = messages.stream()
                .filter(msg -> (Long)msg.get("timestamp") > since)
                .toList();
        
        Map<String, Object> response = new HashMap<>();
        response.put("messages", newMessages);
        response.put("timestamp", System.currentTimeMillis());
        response.put("worldType", worldType);
        
        return response;
    }
}