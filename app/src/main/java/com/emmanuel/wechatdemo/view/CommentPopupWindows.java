package com.emmanuel.wechatdemo.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.emmanuel.wechatdemo.R;
import com.emmanuel.wechatdemo.adapter.FriendsAdapter;
import com.emmanuel.wechatdemo.util.DensityUtil;
import com.emmanuel.wechatdemo.util.ToastUtil;

/**
 * Created by user on 2016/8/17.
 */
public class CommentPopupWindows extends PopupWindow {

    private Context context;
    private CommentPopupWindows commentPopupWindows;
    private PopupwindowClickListener popupwindowClickListener;
    private TextView tvZan;

    public CommentPopupWindows(Context context){
        this.context = context;
        commentPopupWindows = this;
        View view = LayoutInflater.from(context).inflate(R.layout.popup_windows_comment, null);
        this.setWidth(DensityUtil.dip2px(context, 150));
        this.setHeight(DensityUtil.dip2px(context, 40));
        this.setContentView(view);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        ColorDrawable dw = new ColorDrawable(0000000000);
        this.setBackgroundDrawable(dw);

        view.findViewById(R.id.pop_win_layout_zan).setOnClickListener(onClickListener);
        view.findViewById(R.id.pop_win_layout_comment).setOnClickListener(onClickListener);
        tvZan = (TextView)view.findViewById(R.id.pop_win_tv_zan);
    }

    public void showLeft(View v){
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        this.showAtLocation(v, Gravity.NO_GRAVITY, location[0]-this.getWidth(), location[1] - (this.getHeight() - v.getHeight())/2);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.pop_win_layout_zan:
                    commentPopupWindows.dismiss();
                    if(popupwindowClickListener != null){
                        popupwindowClickListener.onClick(view.getId());
                    }
                    break;
                case R.id.pop_win_layout_comment:
                    commentPopupWindows.dismiss();
                    if(popupwindowClickListener != null){
                        popupwindowClickListener.onClick(view.getId());
                    }
                    break;
            }
        }
    };

    public void setZanFlag(boolean isZan){
        if(isZan){
            tvZan.setText("取消赞");
        }else{
            tvZan.setText("赞");
        }
    }

    public void setPopupwindowClickListener(PopupwindowClickListener listener){
        this.popupwindowClickListener = listener;
    }

    public interface PopupwindowClickListener{
        public void onClick(int id);
    }

}
