//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.kteam.palm.common.utils.security.aes;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class KeyTransformer {
    private MessageDigest messageDigestMD5 = null;
    private MessageDigest messageDigestSHA1 = null;
    private SecureRandom secureRandom = null;

    public KeyTransformer() {
        this.secureRandom = new SecureRandom();

        try {
            this.messageDigestMD5 = MessageDigest.getInstance("MD5");
            this.messageDigestSHA1 = MessageDigest.getInstance("SHA1");
        } catch (NoSuchAlgorithmException var2) {
            var2.printStackTrace();
        }

    }

    public byte[] getMD5Digest(String key) {
        this.messageDigestMD5.reset();

        try {
            this.messageDigestMD5.update(key.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException var3) {
            var3.printStackTrace();
        }

        return this.messageDigestMD5.digest();
    }

    public byte[] getRandomKey() {
        byte[] keyBytes = new byte[16];
        this.secureRandom.nextBytes(keyBytes);
        return keyBytes;
    }

    public byte[] getSHA1Digest128Bit(String key) {
        byte[] keyBytes = new byte[16];
        this.messageDigestSHA1.reset();

        try {
            this.messageDigestSHA1.update(key.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException var4) {
            var4.printStackTrace();
        }

        System.arraycopy(this.messageDigestSHA1.digest(), 0, keyBytes, 0, 16);
        return keyBytes;
    }
}
