package com.emmanuel.wechatdemo.util;

import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.emmanuel.wechatdemo.App;

/**
 * Created by user on 2016/8/17.
 */
public class ToastUtil {

    private static Toast toast = null;

    //传入 内容，时间， 位置
    public static void showMessage(final CharSequence msg, final int len, final boolean center) {
        if (msg == null || msg.equals("")) {
            return;
        }
        if (toast != null) {
            toast.setText(msg);
            toast.setDuration(len);
        } else {
            toast = Toast.makeText(App.getInstance().getApplicationContext(), msg, len);
        }
        if (center) {
            toast.setGravity(Gravity.CENTER, 0, 0);
        }
        ViewGroup group = (ViewGroup) toast.getView();
        TextView message = (TextView) group.getChildAt(0);
        message.setPadding(20, 20, 20, 20);
        toast.show();
    }
}
