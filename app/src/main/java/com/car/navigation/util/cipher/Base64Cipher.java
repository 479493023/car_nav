package com.car.navigation.util.cipher;


import java.io.UnsupportedEncodingException;

/**
 * @author MaTianyu
 * @date 14-7-31
 */
public class Base64Cipher extends Cipher {
    private Cipher cipher;

    public Base64Cipher() {
    }

    public Base64Cipher(Cipher cipher) {
        this.cipher = cipher;
    }

    @Override
    public byte[] decrypt(byte[] res) {
        res = Base64.decode(res, Base64.DEFAULT);
        if (cipher != null) {
            res = cipher.decrypt(res);
        }
        return res;
    }

    @Override
    public byte[] encrypt(byte[] res) {
        if (cipher != null) {
            res = cipher.encrypt(res);
        }
        return Base64.encode(res, Base64.DEFAULT);
    }

    /**
     * 加密
     * oldWord：需要加密的文字/比如密码
     */
    public static String setEncryption(String oldWord) {
        try {
            return Base64.encodeToString(oldWord.getBytes("utf-8"), Base64.NO_WRAP);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 解密
     * encodeWord：加密后的文字/比如密码
     */
    public static String setDecrypt(String encodeWord){

        try {
            return new String(Base64.decode(encodeWord, Base64.NO_WRAP), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }
}
