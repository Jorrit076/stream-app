package com.avans.circle;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Message implements Serializable {

    private String nickname;
    private String message ;
    private LocalDateTime timestamp;


    public Message(){

    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Message(String message, String nickname) {
        this.nickname = nickname;
        this.message = message;
        this.timestamp =  LocalDateTime.now();
    }

    public Message(String message, String nickname, LocalDateTime timeStamp) {
        this.nickname = nickname;
        this.message = message;
        this.timestamp =  timeStamp;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}