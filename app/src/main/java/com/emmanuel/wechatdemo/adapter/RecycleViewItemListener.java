package com.emmanuel.wechatdemo.adapter;

import android.view.View;

/**
 * Created by user on 2016/8/16.
 */
public interface RecycleViewItemListener {
    void onItemClick(int position, View view);

    void onCommentClick(int position);

}
