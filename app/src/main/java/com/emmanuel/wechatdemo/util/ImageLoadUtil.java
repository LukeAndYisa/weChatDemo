package com.emmanuel.wechatdemo.util;

import android.graphics.Bitmap;

import com.emmanuel.wechatdemo.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

/**
 * Created by user on 2016/8/17.
 */
public class ImageLoadUtil {
    public static DisplayImageOptions options;
    //加载照片时候的配置
    public static DisplayImageOptions getOptions1() {
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.image_load_error)
                .showImageForEmptyUri(R.drawable.image_load_error)
                .showImageOnFail(R.drawable.image_load_error)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                // .displayer(new RoundedBitmapDisplayer(20))
                .build();
        return options;
    }
    //加载头像时候的配置
    public static DisplayImageOptions getOptions2() {
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.icon_photo)
                .showImageForEmptyUri(R.mipmap.icon_photo)
                .showImageOnFail(R.mipmap.icon_photo)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                // .displayer(new RoundedBitmapDisplayer(20))
                .build();
        return options;
    }

    public static DisplayImageOptions getOptions3() {
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.image_load_error)
                .showImageForEmptyUri(R.drawable.image_load_error)
                .showImageOnFail(R.drawable.image_load_error)
                .cacheInMemory(false)
                .cacheOnDisk(false)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                // .displayer(new RoundedBitmapDisplayer(20))
                .build();
        return options;
    }
}
