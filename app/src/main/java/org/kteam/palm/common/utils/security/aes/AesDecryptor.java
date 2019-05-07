//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.kteam.palm.common.utils.security.aes;


import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;
import org.kteam.palm.common.utils.security.Base64;

public class AesDecryptor {
    private Logger logger = Logger.getLogger(this.getClass());
    private String charset = "UTF-8";

    public AesDecryptor() {
    }

    public byte[] decrypt(byte[] key, byte[] iv, byte[] data) {
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
            cipher.init(2, sKeySpec, ivParamSpec);
        } catch (InvalidKeyException var11) {
            this.logger.error(var11.getMessage(), var11);
        } catch (InvalidAlgorithmParameterException var12) {
            this.logger.error(var12.getMessage(), var12);
        }

        byte[] decryptedData = (byte[])null;

        try {
            decryptedData = cipher.doFinal(data);
        } catch (IllegalBlockSizeException var9) {
            this.logger.error(var9.getMessage(), var9);
        } catch (BadPaddingException var10) {
            this.logger.error(var10.getMessage(), var10);
        }

        return decryptedData;
    }

    public String decryptBase642String(String key, String data) {
        byte[] dataBytes = Base64.decode(data);
        return this.decryptBytes2String(key, dataBytes);
    }

    public byte[] decryptBytes2Bytes(String key, byte[] data) {
        KeyTransformer keyTransformer = new KeyTransformer();
        return this.decrypt(keyTransformer.getMD5Digest(key), keyTransformer.getSHA1Digest128Bit(key), data);
    }

    public String decryptBytes2String(String key, byte[] data) {
        String dataStr = null;

        try {
            dataStr = new String(this.decryptBytes2Bytes(key, data), this.charset);
        } catch (UnsupportedEncodingException var5) {
            this.logger.error(var5.getMessage(), var5);
        }

        return dataStr;
    }

    public String getCharset() {
        return this.charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }
}
