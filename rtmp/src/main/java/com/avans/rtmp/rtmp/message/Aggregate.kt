package com.avans.rtmp.rtmp.message

import com.avans.rtmp.rtmp.chunk.ChunkStreamId
import com.avans.rtmp.rtmp.chunk.ChunkType
import java.io.InputStream

/**
 * Created by pedro on 21/04/21.
 */
class Aggregate: RtmpMessage(BasicHeader(ChunkType.TYPE_0, ChunkStreamId.PROTOCOL_CONTROL)) {

  override fun readBody(input: InputStream) {
    TODO("Not yet implemented")
  }

  override fun storeBody(): ByteArray {
    TODO("Not yet implemented")
  }

  override fun getType(): MessageType = MessageType.AGGREGATE

  override fun getSize(): Int {
    TODO("Not yet implemented")
  }
}