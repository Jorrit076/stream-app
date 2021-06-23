package com.avans.rtmp.rtmp.digisign;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Build;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import androidx.annotation.RequiresApi;

import com.avans.rtmp.flv.FlvPacket;
import org.apache.commons.codec.binary.Base64;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileReader;
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
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

public class StreamSigner {

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static byte[] DataGenerator(String user, long timestamp, String output, Context context) {

        VideoPacket videoPacket = new VideoPacket(user);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(user);
            out.flush();
            byte[] yourBytes = bos.toByteArray();

            DigiSigner digiSigner = new DigiSigner();


            PrivateKey privateKey = getPrivate("privatekey.pem", context);


            byte[] signature = digiSigner.SignData(yourBytes, privateKey);

//            String signatureString = java.util.Base64.getEncoder().encodeToString(signature);
            String signatureString2 = Arrays.toString(signature);



            String url = "http://128.199.58.122:8000/api/server/streamdata";
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // Setting basic post request
            con.setRequestMethod("PUT");
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            con.setRequestProperty("Content-Type", "application/json");

            String postJsonData = "{\"signature\": \"" + signatureString2 + "\"}";

            // Send post request
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(postJsonData);
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();
            System.out.println("nSending 'PUT' request to URL : " + url);
            System.out.println("PUT Data : " + postJsonData);
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

//        AssetFileDescriptor key = context.getAssets().openFd(filename);
//
//        FileReader reader = new FileReader(key.getFileDescriptor());
//
//        try (BufferedReader br = new BufferedReader(
//                new FileReader(key.getFileDescriptor()))) {
//
//            StringBuilder sb = new StringBuilder();
//
//            String line;
//            while ((line = br.readLine()) != null) {
//
//                sb.append(line);
//                sb.append(System.lineSeparator());
//            }

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open(filename)));

            // do reading, usually loop until end of file reading
            StringBuilder sb = new StringBuilder();
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                //process line\
                sb.append(mLine);
                sb.append(System.lineSeparator());
            }

            String keytext = sb.toString();

            String privateKeyPEM = keytext
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replaceAll(System.lineSeparator(), "")
                    .replace("-----END PRIVATE KEY-----", "");

            byte[] encoded = Base64.decodeBase64(privateKeyPEM);

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);

        } catch (IOException e) {
            //log the exception
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }
        return null;


//            System.out.println(sb);







//        InputStream inputStream = context.getAssets().open(filename);
//
//        byte[] buffer = new byte[8192];
//        int bytesRead;
//        ByteArrayOutputStream output = new ByteArrayOutputStream();
//        while ((bytesRead = inputStream.read(buffer)) != -1) {
//            output.write(buffer, 0, bytesRead);
//        }
//
//
//        byte[] keyBytes = output.toByteArray();
//        byte[] keyBytes = Files.readAllBytes(Paths.get(filename));


//        PKCS8EncodedKeySpec spec =
//                new PKCS8EncodedKeySpec(keyBytes);
//        KeyFactory kf = KeyFactory.getInstance("RSA");
//        System.out.println("test test testicel" + kf);
//        return kf.generatePrivate(spec);
    }

}
