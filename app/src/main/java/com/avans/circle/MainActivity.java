package com.avans.circle;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import com.avans.encoder.input.video.CameraOpenException;
import com.avans.rtmp.utils.ConnectCheckerRtmp;
import com.avans.rtplibrary.rtmp.RtmpCamera1;
import com.avans.circle.R;
import com.avans.circle.utils.PathUtils;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity
        implements ConnectCheckerRtmp, View.OnClickListener, SurfaceHolder.Callback {

  private RtmpCamera1 rtmpCamera1;
  private ImageButton button;

  public static Context context;
  private EditText name;
  public static final String NICKNAME = "usernickname";
  public static final String CHATROOM = "userchatroom";
  public RecyclerView myRecylerView ;
  public List<Message> MessageList ;
  public ChatBoxAdapter chatBoxAdapter;
  public EditText messagetxt ;
  public ImageButton send ;

  private DigiSigner digisign;
  private Socket socket;
  private String Nickname;
  private String Chatroom;



  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    setContentView(R.layout.container_view);
    SurfaceView surfaceView = findViewById(R.id.surfaceView);
    button = findViewById(R.id.b_start_stop);
    button.setOnClickListener(this);
    ImageButton switchCamera = findViewById(R.id.switch_camera);
    switchCamera.setOnClickListener(this);
    name = findViewById(R.id.name);
    name.setHint(R.string.name);
    rtmpCamera1 = new RtmpCamera1(surfaceView, this);
    rtmpCamera1.setReTries(10);
    surfaceView.getHolder().addCallback(this);
    context = this.getBaseContext();


    messagetxt = (EditText) findViewById(R.id.message) ;

    send = findViewById(R.id.send);
    send.setOnClickListener(this);
    MessageList = new ArrayList<>();
    myRecylerView = (RecyclerView) findViewById(R.id.messagelist);
    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
    myRecylerView.setLayoutManager(mLayoutManager);
    myRecylerView.setItemAnimator(new DefaultItemAnimator());
    digisign = new DigiSigner(this);


  }

  private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
  public String bytesToHex(byte[] bytes) {
    char[] hexChars = new char[bytes.length * 2];
    for (int j = 0; j < bytes.length; j++) {
      int v = bytes[j] & 0xFF;
      hexChars[j * 2] = HEX_ARRAY[v >>> 4];
      hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
    }
    return new String(hexChars);
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
  public void onConnectionFailedRtmp(final String reason, Context context, String name) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (rtmpCamera1.reTry(5000, reason, context, name)) {
          Toast.makeText(MainActivity.this, "Retry", Toast.LENGTH_SHORT)
                  .show();
        } else {
          Toast.makeText(MainActivity.this, "Connection failed. " + reason, Toast.LENGTH_SHORT)
                  .show();
          rtmpCamera1.stopStream();
//          button.setText(R.string.start_button);
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

  @RequiresApi(api = Build.VERSION_CODES.O)
  @Override
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.b_start_stop:
        if (!rtmpCamera1.isStreaming() && name.getText().toString().length() > 0) {
          if (rtmpCamera1.isRecording()
                  || rtmpCamera1.prepareAudio() && rtmpCamera1.prepareVideo()) {
//            button.setText(R.string.stop_button);
            rtmpCamera1.startStream("rtmp://128.199.58.122/live/" + name.getText().toString(), this, name.getText().toString());

            final KeyReader kr = new KeyReader(this);
            Nickname = name.getText().toString();
            Chatroom = name.getText().toString();

            try {
              //if you are using a phone device you should connect to same local network as your laptop and disable your pubic firewall as well
              socket = IO.socket("https://truyouapi.herokuapp.com");

              //create connection
              socket.connect();
              // emit the event join along side with the nickname

              socket.emit("join", Nickname,Chatroom);
            } catch (URISyntaxException e) {
              e.printStackTrace();

            }
            socket.on("userjoinedthechat", new Emitter.Listener() {
              @Override
              public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                    String data = (String) args[0];
                    // get the extra data from the fired event and display a toast
                    Toast.makeText(MainActivity.this, data, Toast.LENGTH_SHORT).show();

                  }
                });
              }
            });


            socket.on("message", new Emitter.Listener() {
              @Override public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                  @RequiresApi(api = Build.VERSION_CODES.O)
                  @Override public void run() {
                    JSONObject data = (JSONObject) args[0];
                    JSONObject sign = (JSONObject) args[1];
                    try {
                      //extract data from fired event

                      String nickname = data.getString("senderNickname");
                      String message = data.getString("message");
                      String timeStamp = data.getString("timeStamp");
                      String signature = sign.getString("signature");

                      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
                      LocalDateTime dateTime = LocalDateTime.parse(timeStamp, formatter);

                      byte[] array = java.util.Base64.getDecoder().decode(signature);

                      ByteArrayOutputStream bos = new ByteArrayOutputStream();
                      Message msg = new Message(message, nickname, dateTime);
                      byte[] yourBytes = new byte[8];


                      try {
                        ObjectOutputStream out = new ObjectOutputStream(bos);
                        out.writeObject(msg);
                        out.flush();

                        yourBytes = bos.toByteArray();


                      } catch (Exception e) {
                        e.printStackTrace();
                      }
                      PublicKey pubKey = kr.getPublic("public.der");

                      if(digisign.VerifySignature(array, pubKey, yourBytes)){
                        MessageList.add(msg);
                      } else{
                        System.out.println("Error");
                      }



                      // add the new updated list to the adapter
                      chatBoxAdapter = new ChatBoxAdapter(MessageList);

                      // notify the adapter to update the recycler view

                      chatBoxAdapter.notifyDataSetChanged();

                      //set the adapter for the recycler view

                      myRecylerView.setAdapter(chatBoxAdapter);


                    } catch (JSONException | IOException e) {
                      e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                      e.printStackTrace();
                    } catch (SignatureException e) {
                      e.printStackTrace();
                    } catch (InvalidKeyException e) {
                      e.printStackTrace();
                    } catch (Exception e) {
                      e.printStackTrace();
                    }


                  }
                });

              }
            });



          } else {
            Toast.makeText(this, "Error preparing stream, This device cant do it",
                    Toast.LENGTH_SHORT).show();
          }
        } else {
//          button.setText(R.string.start_button);
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
      case R.id.send:
        Nickname = name.getText().toString();
        Chatroom = name.getText().toString();
        final KeyReader kr = new KeyReader(this);
        if(!messagetxt.getText().toString().isEmpty()){
          ByteArrayOutputStream bos = new ByteArrayOutputStream();
          Message m = new Message(messagetxt.getText().toString(), Nickname);


          try {
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(m);
            out.flush();

            byte[] yourBytes = bos.toByteArray();

            PrivateKey privateKey = kr.getPrivate("private.der");
            byte[] signature = digisign.SignData(yourBytes, privateKey);
            String sign = java.util.Base64.getEncoder().encodeToString(signature);
            socket.emit("messagedetection",m.getMessage(), Nickname, m.getTimestamp().toString(),sign,Chatroom);

          } catch (Exception e) {
            e.printStackTrace();
          }
          messagetxt.setText(" ");
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
//      button.setText(getResources().getString(R.string.start_button));
    }
    rtmpCamera1.stopPreview();
  }


}
