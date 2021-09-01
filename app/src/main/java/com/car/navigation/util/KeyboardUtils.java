package com.car.navigation.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;


public class KeyboardUtils {

    private KeyboardUtils() {
    }

    /**
     * 隐藏键盘
     *
     * @param view View 对象
     */
    public static void hideKeyboard(View view) {
        InputMethodManager manager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (manager != null) {
            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 隐藏键盘
     *
     * @param activity Activity
     */
    public static void hideKeyboard(Activity activity) {
        View focusView = activity.getCurrentFocus();
        if (focusView != null) {
            KeyboardUtils.hideKeyboard(focusView);
        }
    }

    /**
     * 显示键盘
     *
     * @param view View 对象
     */
    public static void showKeyboard(View view) {
        view.requestFocus();
        InputMethodManager inputManager =
                (InputMethodManager) view.getContext().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(view, 0);
    }

    /**
     * 清除所有焦点, 隐藏键盘
     *
     * @param activity Activity
     */
    public static void clearFocusAndHideKeyboard(Activity activity) {
        View focusView = activity.getCurrentFocus();
        if (focusView != null) {
            KeyboardUtils.hideKeyboard(focusView);
            focusView.clearFocus();
        }
    }
}
