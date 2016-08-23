package com.emmanuel.wechatdemo.util;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by user on 2016/8/23.
 */
public class Helper {
    public static int getScreenWidth(Context context){
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    public static int getScreenHeight(Context context){
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }
}
