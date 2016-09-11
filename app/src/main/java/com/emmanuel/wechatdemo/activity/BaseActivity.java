package com.emmanuel.wechatdemo.activity;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.emmanuel.wechatdemo.App;
import com.emmanuel.wechatdemo.R;
import com.emmanuel.wechatdemo.util.DensityUtil;

/**
 * Created by user on 2016/8/23.
 */
public abstract class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";

    private Toolbar toolbar;
    private TextView tvTitle;
    private ImageView ivRight1;
    private TextView tvRight1;

    private String title = "";
    private int leftVisibility = View.GONE;
    private int rightRes = 0;

    private String textRight1 = null;

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.left:
                    BaseActivity.this.finish();
                    break;
                case R.id.iv_right_1:
                    onBtnRight1();
                    break;
                case R.id.tv_right_1:
                    onTextRight1();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor(this, ContextCompat.getColor(this, R.color.colorPrimary));
        initView(savedInstanceState);
        initToolBar();
    }

    private void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tvTitle = (TextView)toolbar.findViewById(R.id.title);
        tvTitle.setText(title);
        toolbar.findViewById(R.id.left).setVisibility(leftVisibility);
        toolbar.findViewById(R.id.left).setOnClickListener(onClickListener);
        //右边第一个imageview
        ivRight1 = (ImageView)findViewById(R.id.iv_right_1);
        ivRight1.setOnClickListener(onClickListener);
        if(rightRes != 0) {
            ivRight1.setImageResource(rightRes);
            ivRight1.setVisibility(View.VISIBLE);
        }
        //右边第一个textview
        tvRight1 = (TextView)findViewById(R.id.tv_right_1);
        tvRight1.setOnClickListener(onClickListener);
        tvRight1.setText(textRight1);
        if(textRight1 != null){
            tvRight1.setVisibility(View.VISIBLE);
        }
        setSupportActionBar(toolbar);

    }

    public static void setStatusBarColor(Activity activity, int color) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            ViewGroup decorViewGroup = (ViewGroup) activity.getWindow().getDecorView();
            View statusBarView = new View(activity);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(activity));
            statusBarView.setLayoutParams(params);
            statusBarView.setBackgroundColor(color);
            decorViewGroup.addView(statusBarView);
        }
    }

    public static int getStatusBarHeight(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Resources res = activity.getResources();
            int id = res.getIdentifier("status_bar_height", "dimen", "android");
            int height;
            try {
                height = res.getDimensionPixelSize(id);
            } catch (Resources.NotFoundException e) {
                height = DensityUtil.dip2px(App.getInstance(), 25);
            }
            return height;
        }
        return 0;
    }

    protected abstract void initView(Bundle savedInstanceState);

    protected void setTitle(String title){
        this.title = title;
    }

    protected void setLeftBtnVisibility(int visibility){
        leftVisibility = visibility;
    }

    protected void setRightRes(int res){
        this.rightRes = res;
    }

    protected void setTvRight1Text(String text){
        textRight1 = text;
        if(tvRight1 != null)
            tvRight1.setText(textRight1);
    }

    protected void onBtnRight1() {
        Log.d(TAG, "Click right btn 1");
    }

    protected void onTextRight1(){
        Log.d(TAG, "Click right text 1");
    }
}
