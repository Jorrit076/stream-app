package com.avans.rtmp.rtmp.digisign;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.avans.rtmp.flv.FlvPacket;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;

public class StreamSigner {

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static byte[] DataGenerator(String user, long timestamp, FlvPacket output) {

        VideoPacket videoPacket = new VideoPacket(user, timestamp, output);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(videoPacket);
            out.flush();
            byte[] yourBytes = bos.toByteArray();

            DigiSigner digiSigner = new DigiSigner();

            KeyReader k = new KeyReader(null);
            PrivateKey privateKey = k.getPrivate("./resources/private.der");


            byte[] signature = digiSigner.SignData(yourBytes, privateKey);
            return signature;


        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
                // ignore close exception
            }
        }

        return null;

    }
}
