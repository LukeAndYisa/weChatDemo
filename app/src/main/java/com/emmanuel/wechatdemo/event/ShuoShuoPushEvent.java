package com.emmanuel.wechatdemo.event;

import com.emmanuel.wechatdemo.bean.ShuoShuo;

/**
 * 发布说说的事件
 * Created by user on 2016/9/12.
 */
public class ShuoShuoPushEvent {

    private ShuoShuo mySS;

    public ShuoShuoPushEvent(ShuoShuo shuoShuo){
        this.mySS = shuoShuo;
    }

    public ShuoShuo getMySS(){
        return mySS;
    }

}
