package com.avans.rtmp.rtmp.digisign;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) throws Exception {
	// write your code here

//        Message message = new VideoMessage("a name", "Hoi", LocalDateTime.now());
//
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        ObjectOutputStream out = null;
//        try {
//            out = new ObjectOutputStream(bos);
//            out.writeObject(message);
//            out.flush();
//            byte[] yourBytes = bos.toByteArray();
//
//            DigiSigner digiSigner = new DigiSigner();
//
//            PrivateKey privateKey = KeyReader.getPrivate("./resources/private.der");
//            PublicKey publicKey = KeyReader.getPublic("./resources/public.der");
//
//
//            byte[] signature = digiSigner.SignData(yourBytes, privateKey);
//
//            Boolean verified = digiSigner.VerifySignature(signature, publicKey, yourBytes);
//
//
//        } finally {
//            try {
//                bos.close();
//            } catch (IOException ex) {
//                // ignore close exception
//            }
//        }


    }
}
