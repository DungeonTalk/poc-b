package com.pocai.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class AutoRAGDataUploader {
    
    private static final Logger logger = LoggerFactory.getLogger(AutoRAGDataUploader.class);
    
    @Value("${cloudflare.autorag.account.id}")
    private String accountId;
    
    @Value("${cloudflare.autorag.api.token}")
    private String apiToken;
    
    @Value("${cloudflare.autorag.dataset.id}")
    private String datasetId;
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    public AutoRAGDataUploader(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }
    
    // @PostConstruct - 일시적으로 비활성화
    public void uploadTRPGKnowledgeBase() {
        try {
            logger.info("TRPG 지식 베이스를 AutoRAG에 업로드 시작...");
            
            ClassPathResource resource = new ClassPathResource("trpg-knowledge-base.json");
            JsonNode knowledgeBase = objectMapper.readTree(resource.getInputStream());
            
            if (knowledgeBase.isArray()) {
                int uploadCount = 0;
                for (JsonNode item : knowledgeBase) {
                    if (uploadKnowledgeItem(item)) {
                        uploadCount++;
                    }
                }
                logger.info("TRPG 지식 베이스 업로드 완료: {} 개 항목", uploadCount);
            }
            
        } catch (IOException e) {
            logger.error("TRPG 지식 베이스 파일 읽기 실패: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("TRPG 지식 베이스 업로드 실패: {}", e.getMessage());
        }
    }
    
    private boolean uploadKnowledgeItem(JsonNode item) {
        try {
            String text = item.get("text").asText();
            JsonNode metadata = item.get("metadata");
            
            Map<String, Object> document = new HashMap<>();
            document.put("text", text);
            document.put("metadata", metadata);
            
            return uploadToAutoRAG(document);
            
        } catch (Exception e) {
            logger.warn("지식 항목 업로드 실패: {}", e.getMessage());
            return false;
        }
    }
    
    private boolean uploadToAutoRAG(Map<String, Object> document) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiToken);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(document, headers);
            
            String url = String.format("https://api.cloudflare.com/client/v4/accounts/%s/ai/datasets/%s/documents", 
                                     accountId, datasetId);
            
            restTemplate.postForObject(url, entity, String.class);
            
            logger.debug("문서 업로드 성공: {}", document.get("text").toString().substring(0, 50) + "...");
            return true;
            
        } catch (Exception e) {
            logger.warn("AutoRAG 업로드 실패: {}", e.getMessage());
            return false;
        }
    }
    
    // 수동으로 지식 베이스 재업로드하는 메서드
    public void reloadKnowledgeBase() {
        logger.info("지식 베이스 수동 재업로드 시작...");
        uploadTRPGKnowledgeBase();
    }
}