//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.kteam.palm.common.utils.security.aes;



import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;
import org.kteam.palm.common.utils.security.Base64;

public class AesEncryptor {
    private Logger logger = Logger.getLogger(this.getClass());
    private String charset = "UTF-8";

    public AesEncryptor() {
    }

    public byte[] encrypt(byte[] key, byte[] iv, byte[] data) {
        Cipher cipher = null;

        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        } catch (NoSuchAlgorithmException var13) {
            this.logger.error(var13.getMessage(), var13);
        } catch (NoSuchPaddingException var14) {
            this.logger.error(var14.getMessage(), var14);
        }

        SecretKeySpec sKeySpec = new SecretKeySpec(key, "AES");
        IvParameterSpec ivParamSpec = new IvParameterSpec(iv);

        try {
            cipher.init(1, sKeySpec, ivParamSpec);
        } catch (InvalidKeyException var11) {
            this.logger.error(var11.getMessage(), var11);
        } catch (InvalidAlgorithmParameterException var12) {
            this.logger.error(var12.getMessage(), var12);
        }

        byte[] encryptedData = (byte[])null;

        try {
            encryptedData = cipher.doFinal(data);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return encryptedData;
    }

    public byte[] encryptBytes2Bytes(String key, byte[] data) {
        KeyTransformer keyTransformer = new KeyTransformer();
        return this.encrypt(keyTransformer.getMD5Digest(key), keyTransformer.getSHA1Digest128Bit(key), data);
    }

    public String encryptString2Base64(String key, String data) {
//        return Base64.encodeToString(this.encryptString2Bytes(key, data), Base64.DEFAULT);
//        return Base64.encodeToString(this.encryptString2Bytes(key, data), Base64.DEFAULT);
        return Base64.encode(this.encryptString2Bytes(key, data));

    }

    public byte[] encryptString2Bytes(String key, String data) {
        byte[] dataBytes = (byte[])null;

        try {
            dataBytes = data.getBytes(this.charset);
        } catch (UnsupportedEncodingException var5) {
            this.logger.error(var5.getMessage(), var5);
        }

        return this.encryptBytes2Bytes(key, dataBytes);
    }

    public String getCharset() {
        return this.charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }
}
