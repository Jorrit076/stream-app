package com.avans.rtmp.rtmp.message.shared

import com.avans.rtmp.rtmp.message.MessageType

/**
 * Created by pedro on 21/04/21.
 */
class SharedObjectAmf0: SharedObject() {
  override fun getType(): MessageType = MessageType.SHARED_OBJECT_AMF0
}