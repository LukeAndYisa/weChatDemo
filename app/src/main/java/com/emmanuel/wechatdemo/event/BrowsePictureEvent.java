package com.emmanuel.wechatdemo.event;

import com.emmanuel.wechatdemo.bean.Picture;

import java.util.List;

/**
 * 图片浏览事件
 * Created by user on 2016/9/12.
 */
public class BrowsePictureEvent {

    private List<Picture>listPic;

    public BrowsePictureEvent(List<Picture>list){
        this.listPic = list;
    }

    public List<Picture>getListPic(){
        return listPic;
    }

}
