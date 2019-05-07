package org.kteam.palm.network;

import android.content.Context;

import com.utils.security.SHA1;
import com.utils.security.aes.AesUtils;

import org.kteam.palm.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

/**
 * @Package org.kteam.palm.common.utils
 * @Project Palm
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2015-12-05 15:31
 */
public class NetworkUtils {

    public static String getToken(Context context, Map<String,String> paramsMap, String cmd) {
        Iterator paraIt = paramsMap.keySet().iterator();
        ArrayList paramList = new ArrayList();

        String token;
        while(paraIt.hasNext()) {
            token = (String)paraIt.next();
            Object value = paramsMap.get(token);

            if(!"token".equals(token) && !"cache".equals(token)) {
                paramList.add(token + "=" + value);
            }

        }
        token = getToken(context.getString(R.string.child), context.getString(R.string.tokenKey),  cmd, paramList);
        return token;
    }

    private static String getToken(String chid, String tokenKey, String cmd, ArrayList<String> paramList) {
        StringBuilder key = new StringBuilder(cmd);
        if (paramList != null && !paramList.isEmpty()) {
            try {
                Collections.sort(paramList);
            } catch (Exception var8) {
                var8.printStackTrace();
            }

            Iterator var7 = paramList.iterator();

            while (var7.hasNext()) {
                String s = (String) var7.next();
                key.append("&").append(s);
            }
        }
        return chid + SHA1.encode(AesUtils.encrypt(tokenKey, key.toString()));
    }
}
