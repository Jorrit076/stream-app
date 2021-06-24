package com.avans.circle;


import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

public class DigiSigner {
    private Context context;


    public DigiSigner(Context context){

    }

    public static final String ALGORITHM = "SHA256withRSA";
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)

    public byte[] SignData(byte[] data, PrivateKey privateKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance(ALGORITHM);
        signature.initSign(privateKey);

        signature.update(data);

        byte[] digitalSignature = signature.sign();

        return digitalSignature;
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


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public boolean VerifySignature(byte[] array, PublicKey publicKey, byte[] data) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature signature = Signature.getInstance(ALGORITHM);
        signature.initVerify(publicKey);
        signature.update(data);

        return signature.verify(array);
    }
}
