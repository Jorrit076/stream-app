package com.avans.rtmp.amf.v3

import java.io.InputStream
import java.io.OutputStream

/**
 * Created by pedro on 29/04/21.
 */
class Amf3Object: Amf3Data() {

  override fun readBody(input: InputStream) {
    TODO("Not yet implemented")
  }

  override fun writeBody(output: OutputStream) {
    TODO("Not yet implemented")
  }

  override fun getType(): Amf3Type = Amf3Type.OBJECT

  override fun getSize(): Int {
    TODO("Not yet implemented")
  }
}