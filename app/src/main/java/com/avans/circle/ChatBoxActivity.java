package com.avans.circle;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class ChatBoxActivity extends AppCompatActivity {
    public RecyclerView myRecylerView ;
    public List<Message> MessageList ;
    public ChatBoxAdapter chatBoxAdapter;
    public EditText messagetxt ;
    public Button send ;

    private static final String TAG = "ChatBoxActivity";

    //declare socket object
    private DigiSigner digisign;
    private Socket socket;
    private String Nickname;
    private String Chatroom;

    private String timestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_box);
        messagetxt = (EditText) findViewById(R.id.message) ;

        send = (Button)findViewById(R.id.send);
        MessageList = new ArrayList<>();
        myRecylerView = (RecyclerView) findViewById(R.id.messagelist);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        myRecylerView.setLayoutManager(mLayoutManager);
        myRecylerView.setItemAnimator(new DefaultItemAnimator());
        digisign = new DigiSigner(this);
        final KeyReader kr = new KeyReader(this);
        // get the nickame of the user


        Nickname = (String) getIntent().getExtras().getString(MainActivity.NICKNAME);
        Chatroom = (String) getIntent().getExtras().getString(MainActivity.CHATROOM);


        //connect you socket client to the server

        getSupportActionBar().setTitle(Chatroom);
        try {
            //if you are using a phone device you should connect to same local network as your laptop and disable your pubic firewall as well
            socket = IO.socket("http://192.168.178.38:3000");

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
                        Toast.makeText(ChatBoxActivity.this, data, Toast.LENGTH_SHORT).show();

                    }
                });
            }

        });

        send.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override public void onClick(View v) {
//retrieve the nickname and the message content and fire the event //messagedetection


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
                        socket.emit("messagedetection",m.getMessage(),m.getNickname(), m.getTimestamp().toString(),sign,Chatroom);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    messagetxt.setText(" ");
                }

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

}
