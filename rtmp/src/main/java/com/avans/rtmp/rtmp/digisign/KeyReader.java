package com.avans.rtmp.rtmp.digisign;


import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.*;
import java.util.Scanner;

public class KeyReader {

    private Context context;

    public KeyReader(Context context){
        this.context=context;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public PrivateKey getPrivate(String filename) throws Exception {



        byte[] buffer = new byte[8192];
        int bytesRead;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
//        InputStream inputStream = (InputStream) Paths.get(filename);
//        while ((bytesRead = inputStream.read(buffer)) != -1) {
//            output.write(buffer, 0, bytesRead);
//        }
//
//
//
//
//
//        byte[] keyBytes = output.toByteArray();
        byte[] keyBytes = Files.readAllBytes(Paths.get(filename));


        PKCS8EncodedKeySpec spec =
                new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static PublicKey getPublic(String filename)
            throws Exception {

        byte[] keyBytes = Files.readAllBytes(Paths.get(filename));

        X509EncodedKeySpec spec =
                new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }
}