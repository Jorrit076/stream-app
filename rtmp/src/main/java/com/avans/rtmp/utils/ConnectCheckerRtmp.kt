package com.avans.rtmp.utils

import android.content.Context

/**
 * Created by pedro on 8/04/21.
 */
interface ConnectCheckerRtmp {
  fun onConnectionStartedRtmp(rtmpUrl: String)
  fun onConnectionSuccessRtmp()
  fun onConnectionFailedRtmp(reason: String, context: Context, name: String)
  fun onNewBitrateRtmp(bitrate: Long)
  fun onDisconnectRtmp()
  fun onAuthErrorRtmp()
  fun onAuthSuccessRtmp()
}