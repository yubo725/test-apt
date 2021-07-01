package com.example.testapt.util;

import android.util.Log;

import com.example.annotation.CheckLoginLogic;

public class LoginUtil {

    private static final String MSG = "Hello";

    // 该方法检查是否登录
    @CheckLoginLogic
    public boolean isLogin(String str) {
        if ("Hello".equals(MSG)) {
            return true;
        }
        Log.e("LoginUtil", "need login...");
        return false;
    }
}
