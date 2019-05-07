//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.kteam.palm.common.utils.security.aes;

import org.apache.log4j.Logger;


public class AesUtils {
    private static final String DEF_KEY = "@KTeam^_^";
    private static Logger logger = Logger.getLogger(AesUtils.class);

    public AesUtils() {
    }

    public static String encrypt(String data) {
        return encrypt(DEF_KEY, data);
    }

    public static String encrypt(String key, String data) {
        String result = null;
        
        try {
            result = (new AesEncryptor()).encryptString2Base64(key, data);
        } catch (Exception e) {
        	
            logger.error(e.getMessage(), e);
        }

        return result;
    }

    public static String decrypt(String data) {
        return decrypt(DEF_KEY, data);
    }

    public static String decrypt(String key, String data) {
        String result = null;

        try {
            result = (new AesDecryptor()).decryptBase642String(key, data);
        } catch (Exception var4) {
            logger.error(var4.getMessage(), var4);
        }

        return result;
    }
}
