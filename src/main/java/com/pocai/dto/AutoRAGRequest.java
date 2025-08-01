package com.pocai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

public class AutoRAGRequest {
    
    @JsonProperty("query")
    private String query;
    
    @JsonProperty("per_page")
    private Integer perPage = 5;
    
    @JsonProperty("max_tokens")
    private Integer maxTokens = 8000; // 응답 길이 제한
    
    @JsonProperty("filters")
    @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
    private Map<String, Object> filters;
    
    public AutoRAGRequest() {}
    
    public AutoRAGRequest(String query) {
        this.query = query;
    }
    
    public AutoRAGRequest(String query, int perPage) {
        this.query = query;
        this.perPage = perPage;
    }
    
    public AutoRAGRequest(String query, int perPage, Map<String, Object> filters) {
        this.query = query;
        this.perPage = perPage;
        this.filters = filters;
    }
    
    public String getQuery() {
        return query;
    }
    
    public void setQuery(String query) {
        this.query = query;
    }
    
    public Integer getPerPage() {
        return perPage;
    }
    
    public void setPerPage(Integer perPage) {
        this.perPage = perPage;
    }
    
    public Integer getMaxTokens() {
        return maxTokens;
    }
    
    public void setMaxTokens(Integer maxTokens) {
        this.maxTokens = maxTokens;
    }
    
    public Map<String, Object> getFilters() {
        return filters;
    }
    
    public void setFilters(Map<String, Object> filters) {
        this.filters = filters;
    }
}