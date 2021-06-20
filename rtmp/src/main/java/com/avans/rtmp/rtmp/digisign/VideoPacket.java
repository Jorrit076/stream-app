package com.avans.rtmp.rtmp.digisign;

import com.avans.rtmp.flv.FlvPacket;

import java.io.OutputStream;
import java.io.Serializable;

public class VideoPacket implements Serializable {

    private String name;
    private long timestamp;
    private FlvPacket output;

    public VideoPacket(String name, long timestamp, FlvPacket output) {
        this.name = name;
        this.timestamp = timestamp;
        this.output = output;
    }
}