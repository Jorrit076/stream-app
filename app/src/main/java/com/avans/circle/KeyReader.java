package com.avans.circle;


import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class KeyReader {

    private Context context;

    public KeyReader(Context context){
        this.context=context;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public PrivateKey getPrivate(String filename) throws Exception {

//        String string = "";
//                                InputStream inputStream = this.context.getAssets().open("yoo.txt");
//                        int size = inputStream.available();
//                        byte[] buffer = new byte[size];
//                        inputStream.read(buffer);
//                        string = new String(buffer);
//
//                        System.out.println(string);
        InputStream inputStream = this.context.getAssets().open(filename);

        byte[] buffer = new byte[8192];
        int bytesRead;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }

        byte[] keyBytes = output.toByteArray();
//        byte[] keyBytes = Files.readAllBytes(Paths.get(filename));


        PKCS8EncodedKeySpec spec =
                new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public PublicKey getPublic(String filename)
            throws Exception {
        InputStream inputStream = this.context.getAssets().open(filename);

        byte[] buffer = new byte[8192];
        int bytesRead;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }

        byte[] keyBytes = output.toByteArray();
//        byte[] keyBytes = Files.readAllBytes(Paths.get(filename));


        X509EncodedKeySpec spec =
                new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);

//        byte[] keyBytes = Files.readAllBytes(Paths.get(filename));
//
//        X509EncodedKeySpec spec =
//                new X509EncodedKeySpec(keyBytes);
//        KeyFactory kf = KeyFactory.getInstance("RSA");
//        return kf.generatePublic(spec);
    }
}
