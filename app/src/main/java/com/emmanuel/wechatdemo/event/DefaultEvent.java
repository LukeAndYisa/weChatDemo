package com.emmanuel.wechatdemo.event;

/**
 * 主要用于传递基本数据类型,
 * 用途1：通知关闭activity  intType = 0
 * Created by user on 2016/9/12.
 */
public class DefaultEvent {

    public static final int CLOSE_ACTIVITY = 0;

    private int intType = -1;

    public DefaultEvent(int intType){
        this.intType = intType;
    }

    public int getIntType(){
        return intType;
    }

}
