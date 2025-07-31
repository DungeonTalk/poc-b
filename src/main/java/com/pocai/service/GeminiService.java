package com.pocai.service;

import com.pocai.dto.GeminiRequest;
import com.pocai.dto.GeminiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GeminiService {

    private static final Logger logger = LoggerFactory.getLogger(GeminiService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    @Qualifier("geminiApiKey")
    private String apiKey;

    // 세션별 대화 히스토리 저장
    private final Map<String, StringBuilder> sessionHistory = new ConcurrentHashMap<>();

    private static final String TRPG_SYSTEM_PROMPT = 
        "당신은 TRPG(테이블탑 롤플레잉 게임)의 던전마스터입니다. " +
        "플레이어들을 다양한 세계로 안내하고, 각 세계관에 맞는 흥미진진한 모험을 제공합니다. " +
        
        "# 답변 형식 규칙:\n" +
        "1. 문장과 문장 사이에는 적절한 줄바꿈을 넣어주세요\n" +
        "2. 긴 설명은 문단으로 나누어 가독성을 높여주세요\n" +
        "3. 중요한 정보나 선택지는 별도 줄로 구분해주세요\n" +
        "4. 상황 묘사와 대화는 구분해서 작성해주세요\n\n" +
        
        "# 세계관별 설정:\n" +
        "## 좀비 아포칼립스 (apocalypse):\n" +
        "- 전사 = 생존자 전투병: 근접 무기와 방어구 특화, 좀비 무리 전투 전문\n" +
        "- 마법사 = 과학자: 화학 지식으로 폭발물/독성 물질 제조, 방사능 지역 생존\n" +
        "- 도적 = 스카벤저: 폐허 탐색 전문가, 은밀 이동과 트랩 제작\n" +
        "- 성직자 = 의료진: 응급처치와 심리 치료, 생존자들의 정신적 지주\n\n" +
        
        "## 정통 판타지 (fantasy):\n" +
        "- 전사 = 기사: 검과 방패, 강력한 물리 공격력과 방어력\n" +
        "- 마법사 = 현자: 원소 마법과 고대 지식, 신비로운 퍼즐 해결\n" +
        "- 도적 = 암살자: 그림자 은신, 정보 수집과 함정 해제\n" +
        "- 성직자 = 치유사: 신성 마법으로 치유와 언데드 퇴치\n\n" +
        
        "## 사이버펑크 (cyberpunk):\n" +
        "- 전사 = 사이버 용병: 사이버네틱 강화, 첨단 무기와 인공 근육\n" +
        "- 마법사 = 해커: 사이버 공간 조작, AI 소통과 시스템 해킹\n" +
        "- 도적 = 사이버 닌자: 광학 위장과 신경 간섭 임플란트\n" +
        "- 성직자 = 백스트리트 닥터: 의료용 나노봇과 바이오 해킹\n\n" +
        
        "## 현대 판타지 (modern):\n" +
        "- 전사 = 특수부대 헌터: 현대 무기와 전술, 초자연적 존재 대응\n" +
        "- 마법사 = 디지털 마법사: 스마트폰과 컴퓨터를 통한 디지털 마법\n" +
        "- 도적 = 현대 괴도: CCTV 회피와 보안 시스템 무력화\n" +
        "- 성직자 = 엑소시스트: 종교 지식과 현대 기술 결합한 악령 퇴치\n\n" +
        
        "상황을 생생하게 묘사하고, 플레이어의 행동과 선택한 세계관에 따라 스토리를 전개시킵니다. " +
        "항상 한국어로 대답하며, 각 세계관의 분위기에 맞는 몰입감 있는 롤플레잉을 제공합니다. " +
        "현재 상황을 기억하고 일관성 있게 반응합니다.";

    public GeminiResponse sendMessage(String userMessage, String sessionId) {
        logger.debug("Sending message to Gemini: {}", userMessage);
        
        // 세션 히스토리 관리
        String fullPrompt = buildPromptWithHistory(userMessage, sessionId);
        
        GeminiRequest.Part part = new GeminiRequest.Part(fullPrompt);
        GeminiRequest.Content content = new GeminiRequest.Content(List.of(part));
        GeminiRequest request = new GeminiRequest(List.of(content));

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<GeminiRequest> entity = new HttpEntity<>(request, headers);
            
            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent?key=" + apiKey;
            GeminiResponse response = restTemplate.postForObject(url, entity, GeminiResponse.class);
            
            if (response != null) {
                logger.debug("Gemini response: {}", response.getText());
                updateSessionHistory(sessionId, userMessage, response.getText());
            }
            
            return response;
        } catch (Exception error) {
            logger.error("Gemini API error: ", error);
            
            // 할당량 초과 오류 처리
            if (error.getMessage() != null && 
                (error.getMessage().contains("429") || 
                 error.getMessage().contains("quota") || 
                 error.getMessage().contains("Too Many Requests") ||
                 error.getMessage().contains("RESOURCE_EXHAUSTED"))) {
                
                GeminiResponse errorResponse = new GeminiResponse();
                errorResponse.setText("⚠️ API 할당량 초과: 잠시 후 다시 시도해주세요. 유료 요금제로 업그레이드하면 더 많이 사용할 수 있습니다.");
                return errorResponse;
            }
            
            // 기타 에러 시 기본 응답 반환
            GeminiResponse errorResponse = new GeminiResponse();
            errorResponse.setText("죄송합니다. 일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
            return errorResponse;
        }
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

    public GeminiResponse startNewSession(String userMessage) {
        String sessionId = "session_" + System.currentTimeMillis();
        GeminiResponse response = sendMessage(userMessage, sessionId);
        logger.info("New session started: {}", sessionId);
        return response;
    }
}