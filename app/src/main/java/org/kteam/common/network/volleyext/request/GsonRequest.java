package org.kteam.common.network.volleyext.request;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.JsonSyntaxException;
import org.kteam.common.utils.GsonUtil;

import org.apache.http.protocol.HTTP;
import org.apache.log4j.Logger;
import org.kteam.palm.BaseApplication;
import org.kteam.palm.common.utils.Constants;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * Json请求基类
 *
 */
public class GsonRequest<T> extends Request<T> {

    private final Logger mLogger = Logger.getLogger(getClass());

    // GZip类型支持
    private static final String GZIP_CONTENT_TYPE = "application/gzip;charset=UTF-8";

    private static final String ENCODING_GZIP = "gzip";

    private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
    private String url = "";

    private boolean mGzipEnabled = false;

    private final Class<T> clazz;

    private Map<String, String> headers;

    private final Map<String, String> params;

    private final Listener<T> listener;

    /**
     * GET请求方式
     *
     * @param url     URL of the request to make
     * @param clazz   Relevant class object, for Gson's reflection
     * @param headers Map of request headers
     */
    public GsonRequest(String url,
                       Class<T> clazz,
                       Map<String, String> headers,
                       Listener<T> listener,
                       ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        this.clazz = clazz;
        this.headers = headers;
        this.listener = listener;
        this.params = null;
    }

    /**
     * 自定义请求方式
     *
     * @param url   URL of the request to make
     * @param clazz Relevant class object, for Gson's reflection
     */
    public GsonRequest(int method,
                       String url,
                       Class<T> clazz,
                       Map<String, String> params,
                       Listener<T> listener,
                       ErrorListener errorListener) {

        super(method, url, errorListener);
        this.clazz = clazz;
        this.params = params;
        this.listener = listener;
        this.headers = null;
        this.url = url;

    }

    /**
     * 自定义请求方式
     *
     * @param url   URL of the request to make
     * @param clazz Relevant class object, for Gson's reflection
     */
    public GsonRequest(int method,
                       String url,
                       Class<T> clazz,
                       Map<String, String> params,
                       Map<String, String> headers,
                       Listener<T> listener,
                       ErrorListener errorListener) {

        super(method, url, errorListener);
        this.clazz = clazz;
        this.params = params;
        this.listener = listener;
        this.headers = headers;
        this.url = url;

    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        if (headers == null || headers.equals(Collections.emptyMap())) {
            headers = new HashMap<String, String>();
        }

        // add zip header
        if (mGzipEnabled) {
            headers.put(HEADER_ACCEPT_ENCODING, ENCODING_GZIP);
        }

        if (url.contains(Constants.BASE_URL)) {
            BaseApplication.addSessionCookie(headers);
        }
        return headers;

    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = null;

            String contentType = response.headers.get(HTTP.CONTENT_TYPE);

            // ContentType为gzip的话，做zip流处理
            if (contentType != null && contentType.equalsIgnoreCase(GZIP_CONTENT_TYPE)) {
                GZIPInputStream zis = new GZIPInputStream(new BufferedInputStream(new ByteArrayInputStream(response.data)));
                try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int count;
                    while ((count = zis.read(buffer)) != -1) {
                        baos.write(buffer, 0, count);
                    }
                    byte[] bytes = baos.toByteArray();
                    jsonString = new String(bytes);
                    baos.close();
                } finally {
                    zis.close();
                }
            } else {
                BaseApplication.checkSessionCookie(response.headers);
                jsonString = new String(response.data, "UTF-8");
            }
            mLogger.debug("======jsonStr======= ");
            mLogger.debug(jsonString);
            mLogger.debug("======jsonStr======= ");
            return Response.success(GsonUtil.fromJson(jsonString, clazz),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        } catch (IOException e) {
            return Response.error(new ParseError(e));
        }
    }

    /**
     * 禁用Gzip，默认为false
     */
    public void disableGzip() {
        mGzipEnabled = false;
    }
}
