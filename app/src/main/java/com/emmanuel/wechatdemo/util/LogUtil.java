package com.emmanuel.wechatdemo.util;

import android.util.Log;

import com.emmanuel.wechatdemo.BuildConfig;

/**
 * Created by user on 2016/9/12.
 */
public class LogUtil {

    public void logE(String TAG, String msg){
        if (BuildConfig.logFlag)
            Log.e(TAG, msg);
    }

    public void logD(String TAG, String msg){
        if (BuildConfig.logFlag)
            Log.d(TAG, msg);
    }

    public void logI(String TAG, String msg){
        if (BuildConfig.logFlag)
            Log.i(TAG, msg);
    }

    public void logW(String TAG, String msg){
        if (BuildConfig.logFlag)
            Log.w(TAG, msg);
    }

}
