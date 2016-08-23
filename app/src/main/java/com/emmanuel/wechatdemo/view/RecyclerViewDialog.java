package com.emmanuel.wechatdemo.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.emmanuel.wechatdemo.R;
import com.emmanuel.wechatdemo.adapter.BaseRecycleViewAdapter;
import com.emmanuel.wechatdemo.util.Helper;

/**
 * Created by user on 2016/8/23.
 */
public class RecyclerViewDialog extends DialogFragment {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private BaseRecycleViewAdapter adapter;
    private TextView tvTitle;
    private Button btnClose;
    private static RecyclerViewDialog dialog;

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.view_outsite:
                    dialog.dismiss();
                    break;
                case R.id.btnClose:
                    dialog.dismiss();
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        getDialog().setCanceledOnTouchOutside(true);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//设置背景半透明
        initDialog();
        View view = inflater.inflate(R.layout.dialog_recyclerview, null);
        view.findViewById(R.id.view_outsite).setOnClickListener(onClickListener);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        if(adapter != null)
            recyclerView.setAdapter(adapter);
        tvTitle = (TextView)view.findViewById(R.id.tvTitle);
        view.findViewById(R.id.btnClose).setOnClickListener(onClickListener);
        return view;
    }

    public static RecyclerViewDialog newInstance() {
        if(dialog == null) {
            dialog = new RecyclerViewDialog();
        }
        return dialog;
    }

    private void initDialog(){
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setGravity(Gravity.CENTER | Gravity.BOTTOM);
        }
    }

    public void setAdapter(BaseRecycleViewAdapter adapter){
        this.adapter = adapter;
        if(recyclerView != null)
            recyclerView.setAdapter(this.adapter);
    }

    public void updateAdapter(){
        if(adapter != null){
            adapter.notifyDataSetChanged();
        }
    }

    public void setTitle(String title){
        tvTitle.setText(title);
    }
}
