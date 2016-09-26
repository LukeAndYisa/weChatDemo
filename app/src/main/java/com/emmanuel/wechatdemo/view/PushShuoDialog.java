package com.emmanuel.wechatdemo.view;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.emmanuel.wechatdemo.R;

/**
 * Created by user on 2016/9/26.
 */
public class PushShuoDialog extends DialogFragment {

    private ItemClickLitener itemClickLitener;

    public interface ItemClickLitener{
        public void onClickItem(int position);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initDialog();
        View view = inflater.inflate(R.layout.dialog_push_shuo, null);
        view.findViewById(R.id.tv_picture_shuo).setOnClickListener(onClickListener);
        view.findViewById(R.id.tv_video_shuo).setOnClickListener(onClickListener);
        return view;
    }

    private void initDialog(){
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.setCanceledOnTouchOutside(true);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//设置背景半透明
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setGravity(Gravity.CENTER);
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(itemClickLitener != null){
                int position = view.getId() == R.id.tv_picture_shuo? 1:2;
                itemClickLitener.onClickItem(position);
            }
        }
    };

    public void setItemClickLitener(ItemClickLitener itemClickLitener){
        this.itemClickLitener = itemClickLitener;
    }
}
