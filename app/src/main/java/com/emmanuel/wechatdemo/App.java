package com.emmanuel.wechatdemo;

import android.app.Application;

import com.emmanuel.wechatdemo.bean.User;
import com.emmanuel.wechatdemo.util.UserUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * Created by user on 2016/8/17.
 */
public class App extends Application {

    private static App instance;
    private static User user;

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        //创建默认的ImageLoader配置参数
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(this);
        //Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(configuration);

        initUserInfo();
    }

    private void initUserInfo() {
        user = new User();
        user.name = "Emmanuel";
        user.sex = "男";
        user.signature = "zhang显个性, xu势待发";
        user.age = 24;

        UserUtil.getInstance(this).putString(UserUtil.KEY_NAME, user.name);
        UserUtil.getInstance(this).putString(UserUtil.KEY_SEX, user.sex);
        UserUtil.getInstance(this).putString(UserUtil.KEY_SIGNATURE, user.signature);
    }

    public static User getUser(){
        if(user != null)
            return user;
        return null;
    }

}
