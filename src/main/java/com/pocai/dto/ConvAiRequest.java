package com.pocai.dto;

public class ConvAiRequest {
    private String userText;
    private String charID;
    private String sessionID;
    private String voiceResponse;

    public ConvAiRequest() {}

    public ConvAiRequest(String userText, String charID, String sessionID, String voiceResponse) {
        this.userText = userText;
        this.charID = charID;
        this.sessionID = sessionID;
        this.voiceResponse = voiceResponse;
    }

    public String getUserText() {
        return userText;
    }

    public void setUserText(String userText) {
        this.userText = userText;
    }

    public String getCharID() {
        return charID;
    }

    public void setCharID(String charID) {
        this.charID = charID;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public String getVoiceResponse() {
        return voiceResponse;
    }

    public void setVoiceResponse(String voiceResponse) {
        this.voiceResponse = voiceResponse;
    }
}