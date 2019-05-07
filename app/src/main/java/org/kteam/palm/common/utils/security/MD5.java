//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.kteam.palm.common.utils.security;

import java.security.MessageDigest;

public class MD5 {
    private static final String[] hexDigits = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    public MD5() {
    }

    public static String byteArrayToHexString(byte[] iI1l) {
        StringBuffer illl1111I1I = new StringBuffer();

        for(int i11I = 0; i11I < iI1l.length; ++i11I) {
            illl1111I1I.append(byteToHexString(iI1l[i11I]));
        }

        return illl1111I1I.toString();
    }

    private static String byteToHexString(byte iI1l) {
        int ilIl = iI1l;
        if(iI1l < 0) {
            ilIl = iI1l + 256;
        }

        int iIII1 = ilIl / 16;
        int i1I1I = ilIl % 16;
        return hexDigits[iIII1] + hexDigits[i1I1I];
    }

    public static String MD5Encode(String iIIllI1II) {
        String i1I1lIll1Ill1I1 = null;

        try {
            i1I1lIll1Ill1I1 = new String(iIIllI1II);
            MessageDigest iIl1l = MessageDigest.getInstance("MD5");
            i1I1lIll1Ill1I1 = byteArrayToHexString(iIl1l.digest(i1I1lIll1Ill1I1.getBytes()));
        } catch (Exception var3) {
            ;
        }

        return i1I1lIll1Ill1I1;
    }

    public static void main(String[] args) {
        System.out.println(MD5Encode("000000").length());
    }
}
