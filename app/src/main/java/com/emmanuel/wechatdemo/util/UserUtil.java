package com.emmanuel.wechatdemo.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import java.util.Set;

/**
 * Created by user on 2016/8/23.
 */
public class UserUtil {

    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private final String USER_INFO = "USER_INFO";

    private static UserUtil instance;

    public static String KEY_NAME = "NAME";
    public static String KEY_SEX = "SEX";
    public static String KEY_SIGNATURE = "SIGNATURE";


    private UserUtil(Context context){
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(USER_INFO, Context.MODE_PRIVATE);
        this.editor = this.sharedPreferences.edit();
    }

    public static UserUtil getInstance(Context context) {
        if (instance == null) {
            instance = new UserUtil(context);
        }
        return instance;
    }

    //读取设置项
    public boolean getBoolean(String key, boolean defaultValue) {
        return this.sharedPreferences.getBoolean(key, defaultValue);
    }

    public int getInt(String key, int defaultValue) {
        return this.sharedPreferences.getInt(key, defaultValue);
    }

    public long getLong(String key, long defaultValue) {
        return this.sharedPreferences.getLong(key, defaultValue);
    }

    public float getFloat(String key, float defaultValue) {
        return this.sharedPreferences.getFloat(key, defaultValue);
    }

    public String getString(String key, String value) {
        return this.sharedPreferences.getString(key, value);
    }

    public Set<String> getStringSet(String key, @Nullable Set<String> values) {
        return this.sharedPreferences.getStringSet(key, values);
    }

    //保存设置项
    public void putBoolean(String key, boolean value) {
        this.editor.putBoolean(key, value);
        this.editor.apply();
    }

    public void putInt(String key, int value) {
        this.editor.putInt(key, value);
        this.editor.apply();
    }

    public void putLong(String key, long value) {
        this.editor.putLong(key, value);
        this.editor.apply();
    }

    public void putFloat(String key, float value) {
        this.editor.putFloat(key, value);
        this.editor.apply();
    }

    public void putString(String key, @Nullable String value) {
        if (value == null) {
            this.editor.remove(key);
        } else {
            this.editor.putString(key, value);
        }
        this.editor.apply();
    }

    public void putStringSet(String key, @Nullable Set<String> values) {
        this.editor.putStringSet(key, values);
        this.editor.apply();
    }
}
