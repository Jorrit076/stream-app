package com.avans.circle;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.avans.encoder.input.video.CameraOpenException;
import com.avans.rtmp.utils.ConnectCheckerRtmp;
import com.avans.rtplibrary.rtmp.RtmpCamera1;
import com.avans.circle.R;
import com.avans.circle.utils.PathUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity
        implements ConnectCheckerRtmp, View.OnClickListener, SurfaceHolder.Callback {

  private RtmpCamera1 rtmpCamera1;
  private Button button;
  public static Context context;



  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    setContentView(R.layout.main_activity);
    SurfaceView surfaceView = findViewById(R.id.surfaceView);
    button = findViewById(R.id.b_start_stop);
    button.setOnClickListener(this);
    Button switchCamera = findViewById(R.id.switch_camera);
    switchCamera.setOnClickListener(this);
    rtmpCamera1 = new RtmpCamera1(surfaceView, this);
    rtmpCamera1.setReTries(10);
    surfaceView.getHolder().addCallback(this);
    context = this.getBaseContext();
  }

  @Override
  public void onConnectionStartedRtmp(String rtmpUrl) {
  }

  @Override
  public void onConnectionSuccessRtmp() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        Toast.makeText(MainActivity.this, "Connection success", Toast.LENGTH_SHORT).show();
      }
    });
  }

  @Override
  public void onConnectionFailedRtmp(final String reason) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (rtmpCamera1.reTry(5000, reason)) {
          Toast.makeText(MainActivity.this, "Retry", Toast.LENGTH_SHORT)
                  .show();
        } else {
          Toast.makeText(MainActivity.this, "Connection failed. " + reason, Toast.LENGTH_SHORT)
                  .show();
          rtmpCamera1.stopStream();
          button.setText(R.string.start_button);
        }
      }
    });
  }

  @Override
  public void onNewBitrateRtmp(long bitrate) {

  }

  @Override
  public void onDisconnectRtmp() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        Toast.makeText(MainActivity.this, "Disconnected", Toast.LENGTH_SHORT).show();
      }
    });
  }

  @Override
  public void onAuthErrorRtmp() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        Toast.makeText(MainActivity.this, "Auth error", Toast.LENGTH_SHORT).show();
      }
    });
  }

  @Override
  public void onAuthSuccessRtmp() {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        Toast.makeText(MainActivity.this, "Auth success", Toast.LENGTH_SHORT).show();
      }
    });
  }

  @Override
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.b_start_stop:
        if (!rtmpCamera1.isStreaming()) {
          if (rtmpCamera1.isRecording()
                  || rtmpCamera1.prepareAudio() && rtmpCamera1.prepareVideo()) {
            button.setText(R.string.stop_button);
            rtmpCamera1.startStream("rtmp://128.199.58.122/live/STREAM_NAME");
          } else {
            Toast.makeText(this, "Error preparing stream, This device cant do it",
                    Toast.LENGTH_SHORT).show();
          }
        } else {
          button.setText(R.string.start_button);
          rtmpCamera1.stopStream();
        }
        break;
      case R.id.switch_camera:
        try {
          rtmpCamera1.switchCamera();
        } catch (CameraOpenException e) {
          Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        break;
      default:
        break;
    }
  }

  @Override
  public void surfaceCreated(SurfaceHolder surfaceHolder) {

  }

  @Override
  public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
    rtmpCamera1.startPreview();
  }

  @Override
  public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
    if (rtmpCamera1.isStreaming()) {
      rtmpCamera1.stopStream();
      button.setText(getResources().getString(R.string.start_button));
    }
    rtmpCamera1.stopPreview();
  }
}
