package com.emmanuel.wechatdemo.util;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by user on 2016/9/8.
 */
public class SoftKeyBoardUtil {
    public static void hideSoftKeyBoard(EditText etInput){
        InputMethodManager inputManager = (InputMethodManager)etInput.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(etInput.getWindowToken(), 0);
    }

    public static void showSoftKeyBoard(EditText etInput){
        InputMethodManager inputManager = (InputMethodManager)etInput.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
//            inputManager.showSoftInput(etInput, 0);
    }
}
