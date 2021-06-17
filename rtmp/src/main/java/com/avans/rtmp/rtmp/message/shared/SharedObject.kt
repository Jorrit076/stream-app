package com.avans.rtmp.rtmp.message.shared

import com.avans.rtmp.rtmp.chunk.ChunkStreamId
import com.avans.rtmp.rtmp.chunk.ChunkType
import com.avans.rtmp.rtmp.message.BasicHeader
import com.avans.rtmp.rtmp.message.RtmpMessage
import java.io.InputStream

/**
 * Created by pedro on 21/04/21.
 */
abstract class SharedObject: RtmpMessage(BasicHeader(ChunkType.TYPE_0, ChunkStreamId.PROTOCOL_CONTROL)) {
  override fun readBody(input: InputStream) {
    TODO("Not yet implemented")
  }

  override fun storeBody(): ByteArray {
    TODO("Not yet implemented")
  }

  override fun getSize(): Int {
    TODO("Not yet implemented")
  }
}