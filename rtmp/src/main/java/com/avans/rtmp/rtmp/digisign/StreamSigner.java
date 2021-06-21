package com.avans.rtmp.rtmp.digisign;

import android.content.Context;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.avans.rtmp.flv.FlvPacket;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

public class StreamSigner {

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static byte[] DataGenerator(String user, long timestamp, String output, Context context) {

        VideoPacket videoPacket = new VideoPacket(user, timestamp, output);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(videoPacket);
            out.flush();
            byte[] yourBytes = bos.toByteArray();

            DigiSigner digiSigner = new DigiSigner();



            PrivateKey privateKey = getPrivate("private.der", context);


            byte[] signature = digiSigner.SignData(yourBytes, privateKey);

            String signatureString = java.util.Base64.getEncoder().encodeToString(signature);

            String url = "https://truyouapi.herokuapp.com/api/streamdata";
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // Setting basic post request
            con.setRequestMethod("PUT");
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            con.setRequestProperty("Content-Type","application/json");

            String postJsonData = "{\"signature\": \"" +signatureString + "\"}";

            // Send post request
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(postJsonData);
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();
            System.out.println("nSending 'POST' request to URL : " + url);
            System.out.println("Post Data : " + postJsonData);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String output2;
            StringBuffer response = new StringBuffer();

            while ((output2 = in.readLine()) != null) {
                response.append(output2);
            }
            in.close();

            //printing result from response
            System.out.println(response.toString());




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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static PrivateKey getPrivate(String filename, Context context) throws Exception {

        InputStream inputStream = context.getAssets().open(filename);

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

}
