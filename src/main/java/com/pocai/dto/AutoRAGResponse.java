package com.pocai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class AutoRAGResponse {
    
    @JsonProperty("success")
    private boolean success;
    
    @JsonProperty("result")
    private SearchResultWrapper result;
    
    @JsonProperty("errors")
    private List<Object> errors;
    
    public static class SearchResultWrapper {
        @JsonProperty("data")
        private List<SearchResult> data;
        
        @JsonProperty("info")
        private Object info;
        
        public List<SearchResult> getData() {
            return data;
        }
        
        public void setData(List<SearchResult> data) {
            this.data = data;
        }
        
        public Object getInfo() {
            return info;
        }
        
        public void setInfo(Object info) {
            this.info = info;
        }
    }
    
    public static class SearchResult {
        @JsonProperty("text")
        private String text;
        
        @JsonProperty("score")
        private double score;
        
        @JsonProperty("metadata")
        private Object metadata;
        
        public String getText() {
            return text;
        }
        
        public void setText(String text) {
            this.text = text;
        }
        
        public double getScore() {
            return score;
        }
        
        public void setScore(double score) {
            this.score = score;
        }
        
        public Object getMetadata() {
            return metadata;
        }
        
        public void setMetadata(Object metadata) {
            this.metadata = metadata;
        }
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public SearchResultWrapper getResult() {
        return result;
    }
    
    public void setResult(SearchResultWrapper result) {
        this.result = result;
    }
    
    public List<Object> getErrors() {
        return errors;
    }
    
    public void setErrors(List<Object> errors) {
        this.errors = errors;
    }
    
    @Override
    public String toString() {
        return "AutoRAGResponse{" +
                "success=" + success +
                ", result=" + result +
                ", errors=" + errors +
                '}';
    }
}