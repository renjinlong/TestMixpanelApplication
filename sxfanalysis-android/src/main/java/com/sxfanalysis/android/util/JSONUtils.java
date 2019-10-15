package com.sxfanalysis.android.util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class JSONUtils {

    /***
     * Workaround for broken JSONObject.
     *
     *     JSONObject wat = new JSONObject("{\"k\":null}");
     *     assert("null".equals(wat.optString("k")));
     *
     * Just let that sink in for a sec. I'll wait.
     *
     * @param o a JSONObject
     * @param k a key
     */
    public static String optionalStringKey(JSONObject o, String k)
            throws JSONException {
        if (o.has(k) && !o.isNull(k)) {
            return o.getString(k);
        }

        return null;
    }


    /**
     * 将srcJObjStr和addJObjStr合并，如果有重复字段，以addJObjStr为准
     *
     * @param srcJObjStr 原jsonObject字符串
     * @param addJObjStr 需要加入的jsonObject字符串
     * @return srcJObjStr
     */
    public static String combineJson(String srcJObjStr, String addJObjStr) throws JSONException {
        if (addJObjStr == null || addJObjStr.isEmpty()) {
            return srcJObjStr;
        }
        if (srcJObjStr == null || srcJObjStr.isEmpty()) {
            return addJObjStr;
        }

        JSONObject srcJObj = new JSONObject(srcJObjStr);
        JSONObject addJObj = new JSONObject(addJObjStr);

        combineJson(srcJObj, addJObj);

        return srcJObj.toString();
    }

    @SuppressWarnings("unchecked")
    public static JSONObject combineJson(JSONObject srcObj, JSONObject addObj) throws JSONException {

        Iterator<String> itKeys = addObj.keys();
        String key, value;
        while (itKeys.hasNext()) {
            key = itKeys.next();
            value = addObj.optString(key);

            srcObj.put(key, value);
        }
        return srcObj;
    }
}
