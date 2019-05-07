//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.kteam.palm.common.utils.security;

import java.security.MessageDigest;

public class SHA1 {
    private static final String ALGORITHM = "SHA1";
    private static final char[] HEX_DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public SHA1() {
    }

    public static String encode(String str) {
        return str == null?null:encode(str.getBytes());
    }

    public static String encode(byte[] bytes) {
        try {
            MessageDigest e = MessageDigest.getInstance("SHA1");
            e.update(bytes);
            return getFormattedText(e.digest());
        } catch (Exception var2) {
            throw new RuntimeException(var2);
        }
    }

    private static String getFormattedText(byte[] bytes) {
        int len = bytes.length;
        StringBuilder buf = new StringBuilder(len * 2);

        for(int j = 0; j < len; ++j) {
            buf.append(HEX_DIGITS[bytes[j] >> 4 & 15]);
            buf.append(HEX_DIGITS[bytes[j] & 15]);
        }

        return buf.toString();
    }
}
