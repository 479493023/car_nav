package com.car.navigation.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.car.navigation.Constants;
import com.car.navigation.util.cipher.ByteUtil;
import com.car.navigation.util.cipher.Cipher;
import com.car.navigation.util.cipher.HexUtil;


/**
 * 本地数据存储操作，sp
 * ycc
 */

public class DataKeeper {

    public static SharedPreferences sp;
    public static SharedPreferences.Editor editor;

    public static void init(Context context) {
        if (sp == null || editor == null) {
            sp = context.getSharedPreferences(Constants.SP_NAME, Context.MODE_PRIVATE);
            editor = sp.edit();
        }
    }

    /**
     * *************** get ******************
     */

    public static String get(Context context, String key, String defValue) {
        init(context);
        return sp.getString(key, defValue);
    }

    public static boolean get(Context context, String key, boolean defValue) {
        init(context);
        return sp.getBoolean(key, defValue);
    }

    public static float get(Context context, String key, float defValue) {
        init(context);
        return sp.getFloat(key, defValue);
    }

    public static int get(Context context, String key, int defValue) {
        init(context);
        return sp.getInt(key, defValue);
    }

    public static long get(Context context, String key, long defValue) {
        init(context);
        return sp.getLong(key, defValue);
    }

    public static Object get(Context context, String key) {
        init(context);
        return get(context, key, (Cipher) null);
    }

    public static Object get(Context context, String key, Cipher cipher) {
        init(context);
        try {
            String hex = get(context, key, (String) null);
            if (hex == null) return null;
            byte[] bytes = HexUtil.decodeHex(hex.toCharArray());
            if (cipher != null) bytes = cipher.decrypt(bytes);
            Object obj = ByteUtil.byteToObject(bytes);
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * *************** put ******************
     */
    public static void put(Context context, String key, Object ser) {
        init(context);
        put(context, key, ser, null);
    }

    public static void put(Context context, String key, Object ser, Cipher cipher) {
        init(context);
        try {
            if (ser == null) {
                editor.remove(key).commit();
            } else {
                byte[] bytes = ByteUtil.objectToByte(ser);
                if (cipher != null) bytes = cipher.encrypt(bytes);
                put(context, key, HexUtil.encodeHexStr(bytes));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void put(Context context, String key, String value) {
        init(context);
        if (value == null) {
            editor.remove(key).commit();
        } else {
            editor.putString(key, value).commit();
        }
    }

    public static void put(Context context, String key, boolean value) {
        init(context);
        editor.putBoolean(key, value).commit();
    }

    public static void put(Context context, String key, float value) {
        init(context);
        editor.putFloat(key, value).commit();
    }

    public static void put(Context context, String key, long value) {
        init(context);
        editor.putLong(key, value).commit();
    }

    public static void put(Context context, String key, int value) {
        init(context);
        editor.putInt(key, value).commit();
    }

    public static void remove(Context context, String key) {
        init(context);
        editor.remove(key).commit();
    }

    public static void removeAll(Context context) {
        init(context);
        editor.clear().commit();
    }
}
