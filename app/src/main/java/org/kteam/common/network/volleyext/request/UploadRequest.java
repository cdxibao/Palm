package org.kteam.common.network.volleyext.request;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import org.kteam.common.utils.GsonUtil;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.protocol.HTTP;
import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 文件上传request
 */
public class UploadRequest<T> extends Request<T> {
    private final Logger mLogger = Logger.getLogger(getClass());

    private MultipartEntity entity = new MultipartEntity();

    private static final String FILE_PART_NAME = "media";

    private Map<String, String> headers;
    private final File filePart;
    private final HashMap<String, String> paramMap;
    private final Listener<T> listener;
    private final Class<T> clazz;

    /**
     * Creates a new request.
     *
     * @param url           URL to fetch the JSON from
     *                      and indicates no parameters will be posted along with request.
     * @param listener      Listener to receive the JSON response
     * @param errorListener Error listener, or null to ignore errors.
     */
    public UploadRequest(
            String url,
            Class<T> clazz,
            File file,
            HashMap<String, String> params,
            Listener<T> listener,
            ErrorListener errorListener) {
        super(Method.POST,
                url,
                errorListener);
        this.clazz = clazz;
        this.listener = listener;
        this.filePart = file;
        this.paramMap = params;
        buildMultipartEntity();
    }

    /**
     * Creates a new request.
     *
     * @param url           URL to fetch the JSON from
     *                      and indicates no parameters will be posted along with request.
     * @param listener      Listener to receive the JSON response
     * @param errorListener Error listener, or null to ignore errors.
     */
    public UploadRequest(
            String url,
            Class<T> clazz,
            File file,
            HashMap<String, String> params,
            HashMap<String, String> headers,
            Listener<T> listener,
            ErrorListener errorListener) {
        super(Method.POST,
                url,
                errorListener);
        this.clazz = clazz;
        this.listener = listener;
        this.filePart = file;
        this.paramMap = params;
        this.headers = headers;
        buildMultipartEntity();
    }

    private void buildMultipartEntity() {
        entity.addPart(FILE_PART_NAME, new FileBody(filePart));
        try {
            Set<Map.Entry<String, String>> entries = paramMap.entrySet();
            Iterator<Map.Entry<String, String>> iterator = entries.iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> next = iterator.next();
                String key = next.getKey();
                String value = next.getValue();
                entity.addPart(key, new StringBody(value));
            }
        } catch (UnsupportedEncodingException e) {
            VolleyLog.e("UnsupportedEncodingException");
        }
    }


    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers;
    }

    @Override
    public String getBodyContentType() {
        return entity.getContentType().getValue();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            entity.writeTo(bos);
        } catch (IOException e) {
            VolleyLog.e("IOException writing to ByteArrayOutputStream");
        }
        return bos.toByteArray();
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        String contentType = response.headers.get(HTTP.CONTENT_TYPE);
        String jsonString = null;
        try {
            jsonString = new String(response.data, "UTF-8");

            mLogger.debug("======jsonStr======= ");
            mLogger.debug(jsonString);
            mLogger.debug("======jsonStr======= ");

            return Response.success(GsonUtil.fromJson(jsonString, clazz),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }
}
