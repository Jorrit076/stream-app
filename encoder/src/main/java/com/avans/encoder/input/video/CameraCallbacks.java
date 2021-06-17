package com.avans.encoder.input.video;

public interface CameraCallbacks {
  void onCameraChanged(CameraHelper.Facing facing);
  void onCameraError(String error);
}
