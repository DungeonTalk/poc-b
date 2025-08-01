package com.pocai.service;

import com.pocai.dto.AutoRAGRequest;
import com.pocai.dto.AutoRAGResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AutoRAGService {
    
    private static final Logger logger = LoggerFactory.getLogger(AutoRAGService.class);
    
    @Value("${cloudflare.autorag.api.url}")
    private String apiUrl;
    
    @Value("${cloudflare.autorag.account.id}")
    private String accountId;
    
    @Value("${cloudflare.autorag.api.token}")
    private String apiToken;
    
    @Value("${cloudflare.autorag.dataset.id}")
    private String datasetId;
    
    private final RestTemplate restTemplate;
    
    public AutoRAGService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    public String searchTRPGContext(String query, String worldType, String sessionId) {
        try {
            logger.info("Searching TRPG context for query: {}, world: {}, session: {}", query, worldType, sessionId);
            
            // 필터 없이 단순 검색으로 테스트 (결과 수 제한)
            AutoRAGRequest request = new AutoRAGRequest(query, 1);
            request.setMaxTokens(5000); // 응답 토큰 제한
            AutoRAGResponse response = searchWithAutoRAG(request);
            
            logger.info("AutoRAG response received: {}", response);
            
            if (response != null && response.isSuccess() && response.getResult() != null && response.getResult().getData() != null) {
                StringBuilder context = new StringBuilder();
                for (AutoRAGResponse.SearchResult result : response.getResult().getData()) {
                    logger.info("Search result - Score: {}, Text: {}", result.getScore(), result.getText());
                    if (result.getScore() > 0.3) { // 임계값을 낮춰서 더 많은 결과 확인
                        context.append(result.getText()).append("\n\n");
                    }
                }
                
                String contextText = context.toString().trim();
                logger.info("Found context: {}", contextText);
                return contextText.isEmpty() ? null : contextText;
            } else {
                logger.warn("No valid response from AutoRAG");
            }
            
        } catch (Exception e) {
            logger.error("AutoRAG search failed: ", e);
        }
        
        return null;
    }
    
    public String searchRules(String query, String worldType) {
        try {
            // 필터 없이 단순 검색 (일단 테스트용)
            AutoRAGRequest request = new AutoRAGRequest(query, 5);
            AutoRAGResponse response = searchWithAutoRAG(request);
            
            if (response != null && response.isSuccess() && response.getResult() != null && response.getResult().getData() != null) {
                StringBuilder rules = new StringBuilder();
                for (AutoRAGResponse.SearchResult result : response.getResult().getData()) {
                    if (result.getScore() > 0.6) {
                        rules.append("• ").append(result.getText()).append("\n");
                    }
                }
                
                return rules.toString().trim();
            }
            
        } catch (Exception e) {
            logger.warn("AutoRAG rules search failed: {}", e.getMessage());
        }
        
        return null;
    }
    
    private AutoRAGResponse searchWithAutoRAG(AutoRAGRequest request) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiToken);
            
            HttpEntity<AutoRAGRequest> entity = new HttpEntity<>(request, headers);
            
            String url = apiUrl.replace("{account_id}", accountId).replace("{id}", datasetId);
            
            return restTemplate.exchange(url, HttpMethod.POST, entity, AutoRAGResponse.class).getBody();
            
        } catch (Exception e) {
            logger.error("AutoRAG API call failed: ", e);
            return null;
        }
    }
    
    // 세션 기록을 AutoRAG에 저장하는 메서드 (향후 구현)
    public void indexSessionHistory(String sessionId, String playerMessage, String dmResponse, String worldType) {
        logger.debug("Indexing session history - Session: {}, World: {}", sessionId, worldType);
        // TODO: Cloudflare AutoRAG 인덱싱 API 구현
    }
}