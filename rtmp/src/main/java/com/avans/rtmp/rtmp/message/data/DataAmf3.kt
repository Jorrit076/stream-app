package com.avans.rtmp.rtmp.message.data

import com.avans.rtmp.rtmp.chunk.ChunkStreamId
import com.avans.rtmp.rtmp.chunk.ChunkType
import com.avans.rtmp.rtmp.message.BasicHeader
import com.avans.rtmp.rtmp.message.MessageType

/**
 * Created by pedro on 21/04/21.
 */
class DataAmf3(name: String = "", timeStamp: Int = 0, streamId: Int = 0, basicHeader: BasicHeader = BasicHeader(ChunkType.TYPE_0, ChunkStreamId.OVER_CONNECTION)):
    Data(name, timeStamp, streamId, basicHeader) {
  override fun getType(): MessageType = MessageType.DATA_AMF3
}