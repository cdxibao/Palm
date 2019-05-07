package org.kteam.common.network.volleyext;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;


public class RequestManager {
    
    private static RequestQueue mRequestQueue;
    
    public static void init(Context context) {
        // 如果SslHttpStack true代表支持ssl
        mRequestQueue = Volley.newRequestQueue(context);
    }
    
    public static RequestQueue getRequestQueue() {
        if (mRequestQueue != null) {
            return mRequestQueue;
        }
        else {
            throw new IllegalStateException("RequestQueue not initialized");
        }
    }
    
    /**
     * 添加请求队列
     * 
     * @param request
     * @param tag
     */
    public static void addRequest(Request<?> request, Object tag) {
        if (tag != null) {
            request.setTag(tag);
        }
        mRequestQueue.add(request);
    }
    
    /**
     * 取消所有请求
     * 
     * @param tag
     */
    public static void cancelAll(Object tag) {
        if (mRequestQueue != null)
            mRequestQueue.cancelAll(tag);
    }
}
