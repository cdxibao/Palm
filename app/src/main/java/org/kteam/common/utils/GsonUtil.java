package org.kteam.common.utils;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

@SuppressLint("SimpleDateFormat")
public class GsonUtil {
    
    private static Gson gson = null;
    
    /** 默认的 日期/时间字段的格式化模式。 */
    public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
    
    static {
        if (gson == null) {
            gson = new Gson();
        }
    }
    
    private GsonUtil() {
        
    }
    
    /**
     * 将对象转换成json格式
     * 
     * @param ts
     * @return
     */
    public static String objectToJson(Object ts) {
        String jsonStr = null;
        if (gson != null) {
            jsonStr = gson.toJson(ts);
        }
        return jsonStr;
    }
    
    /**
     * 将对象转换成json格式(并自定义日期格式)
     * 
     * @param ts
     * @return
     */
    public static String objectToJsonDateSerializer(Object ts,
                                                    final String dateformat) {
        String jsonStr = null;
        gson = new GsonBuilder().registerTypeHierarchyAdapter(Date.class,
                                                              new JsonSerializer<Date>() {
                                                                  public JsonElement serialize(Date src,
                                                                                               Type typeOfSrc,
                                                                                               JsonSerializationContext context) {
                                                                      SimpleDateFormat format = new SimpleDateFormat(dateformat);
                                                                      return new JsonPrimitive(format.format(src));
                                                                  }
                                                              })
                                .setDateFormat(dateformat)
                                .create();
        if (gson != null) {
            jsonStr = gson.toJson(ts);
        }
        return jsonStr;
    }
    
    /**
     * 将json格式转换成list对象
     * 
     * @param jsonStr
     * @return
     */
    public static List<?> jsonToList(String jsonStr) {
        if (TextUtils.isEmpty(jsonStr)) {
            return null;
        }
        
        List<?> objList = null;
        if (gson != null) {
            Type type = new com.google.gson.reflect.TypeToken<List<?>>() {
            }.getType();
            objList = gson.fromJson(jsonStr, type);
        }
        return objList;
    }
    
    /**
     * 将json格式转换成list对象，并准确指定类型
     * 
     * @param jsonStr
     * @param type
     * @return
     */
    public static List<?> jsonToList(String jsonStr, Type type) {
        if (TextUtils.isEmpty(jsonStr)) {
            return null;
        }
        
        List<?> objList = null;
        if (gson != null) {
            objList = gson.fromJson(jsonStr, type);
        }
        return objList;
    }
    
    /**
     * 将json格式转换成map对象
     * 
     * @param jsonStr
     * @return
     */
    public static Map<?, ?> jsonToMap(String jsonStr) {
        if (TextUtils.isEmpty(jsonStr)) {
            return null;
        }
        
        Map<?, ?> objMap = null;
        if (gson != null) {
            Type type = new com.google.gson.reflect.TypeToken<Map<?, ?>>() {
            }.getType();
            objMap = gson.fromJson(jsonStr, type);
        }
        return objMap;
    }
    
    /**
     * 将json转换成bean对象
     * 
     * @param jsonStr
     * @return
     */
    public static Object jsonToBean(String jsonStr, Class<?> cl) {
        if (TextUtils.isEmpty(jsonStr)) {
            return null;
        }
        
        Object obj = null;
        
        // 过滤Static transient volatitle 属性
        gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.STATIC,
                                                            Modifier.TRANSIENT,
                                                            Modifier.VOLATILE)
                                .create();
        
        if (gson != null) {
            obj = gson.fromJson(jsonStr, cl);
        }
        return obj;
    }
    
    /**
     * 将json转换成bean对象
     * 
     * @param jsonStr
     * @param cl
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T jsonToBeanDateSerializer(String jsonStr,
                                                 Class<T> cl,
                                                 final String pattern) {
        if (TextUtils.isEmpty(jsonStr)) {
            return null;
        }
        
        Object obj = null;
        // 过滤Static transient volatitle 属性
         // 注册自定义日期类型
        gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.STATIC,
                                                            Modifier.TRANSIENT,
                                                            Modifier.VOLATILE)
                                .registerTypeAdapter(Date.class,
                                                     new JsonDeserializer<Date>() {
                                                         public Date deserialize(JsonElement json,
                                                                                 Type typeOfT,
                                                                                 JsonDeserializationContext context) throws JsonParseException {
                                                             SimpleDateFormat format = new SimpleDateFormat(pattern);
                                                             String dateStr = json.getAsString();
                                                             try {
                                                                 return format.parse(dateStr);
                                                             }
                                                             catch (ParseException e) {
                                                                 e.printStackTrace();
                                                             }
                                                             return null;
                                                         }
                                                     })
                                .setDateFormat(pattern)
                                .create();
        if (gson != null) {
            obj = gson.fromJson(jsonStr, cl);
        }
        return (T) obj;
    }
    
    /**
     * 将json转换成bean对象 默认日期格式处理
     * 
     * @param jsonStr
     * @param cl
     * @return
     */
    public static <T> T fromJson(String jsonStr, Class<T> cl) {
        return jsonToBeanDateSerializer(jsonStr, cl, DEFAULT_DATE_PATTERN);
    }
    
    /**
     * 根据
     * 
     * @param jsonStr
     * @param key
     * @return
     */
    public static Object getJsonValue(String jsonStr, String key) {
        Object rulsObj = null;
        Map<?, ?> rulsMap = jsonToMap(jsonStr);
        if (rulsMap != null && rulsMap.size() > 0) {
            rulsObj = rulsMap.get(key);
        }
        return rulsObj;
    }
    
}
