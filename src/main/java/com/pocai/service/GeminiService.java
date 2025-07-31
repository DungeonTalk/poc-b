package com.pocai.service;

import com.pocai.dto.GeminiRequest;
import com.pocai.dto.GeminiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GeminiService {

    private static final Logger logger = LoggerFactory.getLogger(GeminiService.class);

    @Autowired
    private WebClient geminiWebClient;

    @Autowired
    @Qualifier("geminiApiKey")
    private String apiKey;

    // 세션별 대화 히스토리 저장
    private final Map<String, StringBuilder> sessionHistory = new ConcurrentHashMap<>();

    private static final String TRPG_SYSTEM_PROMPT = 
        "당신은 TRPG(테이블탑 롤플레잉 게임)의 던전마스터입니다. " +
        "플레이어들을 판타지 세계로 안내하고, 흥미진진한 모험을 제공합니다. " +
        "상황을 생생하게 묘사하고, 플레이어의 행동에 따라 스토리를 전개시킵니다. " +
        "항상 한국어로 대답하며, 몰입감 있는 롤플레잉을 제공합니다. " +
        "현재 상황을 기억하고 일관성 있게 반응합니다.";

    public Mono<GeminiResponse> sendMessage(String userMessage, String sessionId) {
        logger.debug("Sending message to Gemini: {}", userMessage);
        
        // 세션 히스토리 관리
        String fullPrompt = buildPromptWithHistory(userMessage, sessionId);
        
        GeminiRequest.Part part = new GeminiRequest.Part(fullPrompt);
        GeminiRequest.Content content = new GeminiRequest.Content(List.of(part));
        GeminiRequest request = new GeminiRequest(List.of(content));

        return geminiWebClient.post()
                .uri("?key=" + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(GeminiResponse.class)
                .doOnSuccess(response -> {
                    logger.debug("Gemini response: {}", response.getText());
                    updateSessionHistory(sessionId, userMessage, response.getText());
                })
                .doOnError(error -> logger.error("Gemini API error: ", error));
    }

    private String buildPromptWithHistory(String userMessage, String sessionId) {
        StringBuilder prompt = new StringBuilder();
        prompt.append(TRPG_SYSTEM_PROMPT).append("\n\n");
        
        // 세션 히스토리 추가
        if (sessionId != null && sessionHistory.containsKey(sessionId)) {
            prompt.append("이전 대화:\n");
            prompt.append(sessionHistory.get(sessionId).toString());
            prompt.append("\n");
        }
        
        prompt.append("플레이어: ").append(userMessage).append("\n");
        prompt.append("던전마스터:");
        
        return prompt.toString();
    }

    private void updateSessionHistory(String sessionId, String userMessage, String aiResponse) {
        if (sessionId == null) return;
        
        StringBuilder history = sessionHistory.computeIfAbsent(sessionId, k -> new StringBuilder());
        history.append("플레이어: ").append(userMessage).append("\n");
        history.append("던전마스터: ").append(aiResponse).append("\n");
        
        // 히스토리가 너무 길어지면 앞부분 삭제 (토큰 제한 고려)
        if (history.length() > 2000) {
            int cutIndex = history.indexOf("\n", 500);
            if (cutIndex > 0) {
                history.delete(0, cutIndex + 1);
            }
        }
    }

    public Mono<GeminiResponse> startNewSession(String userMessage) {
        String sessionId = "session_" + System.currentTimeMillis();
        return sendMessage(userMessage, sessionId)
                .map(response -> {
                    // 응답에 세션 ID 추가 (클라이언트가 사용할 수 있도록)
                    logger.info("New session started: {}", sessionId);
                    return response;
                });
    }
}