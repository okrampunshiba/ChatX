package com.app.chatx;

public class Messages {
    public String senderId, message, mediaUrl, type;
    public Messages(){}
    public Messages(String senderId, String message, String mediaUrl, String type){
        this.senderId=senderId;
        this.message=message;
        this.mediaUrl=mediaUrl;
        this.type=type;
    }
}