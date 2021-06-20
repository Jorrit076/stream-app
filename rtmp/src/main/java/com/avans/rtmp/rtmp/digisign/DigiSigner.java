package com.avans.rtmp.rtmp.digisign;


import java.security.*;

public class DigiSigner {

    public static final String ALGORITHM = "SHA256withRSA";
    public byte[] SignData(byte[] data, PrivateKey privateKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance(ALGORITHM);
        signature.initSign(privateKey);

        signature.update(data);
        byte[] digitalSignature = signature.sign();
        return digitalSignature;
    }


    public boolean VerifySignature(byte[] array, PublicKey publicKey, byte[] data) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance(ALGORITHM);
        signature.initVerify(publicKey);
        signature.update(data);
        return signature.verify(array);
    }
}
