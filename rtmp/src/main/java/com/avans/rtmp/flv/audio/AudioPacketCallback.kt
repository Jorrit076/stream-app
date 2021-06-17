package com.avans.rtmp.flv.audio

import com.avans.rtmp.flv.FlvPacket

/**
 * Created by pedro on 29/04/21.
 */
interface AudioPacketCallback {
  fun onAudioFrameCreated(flvPacket: FlvPacket)
}