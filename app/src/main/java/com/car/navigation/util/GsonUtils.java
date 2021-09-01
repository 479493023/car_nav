package com.car.navigation.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;

import com.car.navigation.entity.CityModel;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Ycc Gson数据转换
 * @version v1.0
 * @Time 2018-8-16
 */
public class GsonUtils {
    /**
     * 实现单例
     */
    private static Gson gson = null;

    static {
        if (gson == null) {
            gson = new Gson();
        }
    }

    /**
     * 隐藏默认的构造方法
     */
    private GsonUtils() {

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
     * 返回cla 类型的list数组
     *
     * @param s
     * @param cla
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T extends Object> T jsonToBeanList(String s, Class<?> cla) {

        List<Object> ls = new ArrayList<Object>();
        JSONArray ss;
        try {
            ss = new JSONArray(s);
            for (int i = 0; i < ss.length(); i++) {
                String str = ss.getString(i);
                Object a = jsonToBean(str, cla);
                ls.add(a);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return (T) ls;
    }


    /**
     * 将jsonStr转换成cl对象
     *
     * @param jsonStr
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T extends Object> T jsonToBean(String jsonStr, Class<?> cl) {
        Object obj = null;
        if (gson != null) {
            if (!TextUtils.isEmpty(jsonStr))
                obj = gson.fromJson(jsonStr, cl);
        }
        return (T) obj;
    }

    /**
     * 将json格式转换成map对象
     *
     * @param jsonStr
     * @return
     */
    public static Map<?, ?> jsonToMap(String jsonStr) {
        Map<?, ?> objMap = null;
        if (gson != null) {
            java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<Map<?, ?>>() {
            }.getType();
            objMap = gson.fromJson(jsonStr, type);
        }
        return objMap;
    }

    /**
     *获取assets中的指定文件
     *
     * @param context
     * @param fileName
     * @return
     */
    public static String getJsonAssets(Context context, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    /**
     * 解析assets中的指定文件
     *
     * @param result
     * @return
     */
    public static ArrayList<CityModel> parseAssetsData(String result) {//Gson 解析
        ArrayList<CityModel> detail = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(result);
            for (int i = 0; i < data.length(); i++) {
                CityModel entity = gson.fromJson(data.optJSONObject(i).toString(), CityModel.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }
}
