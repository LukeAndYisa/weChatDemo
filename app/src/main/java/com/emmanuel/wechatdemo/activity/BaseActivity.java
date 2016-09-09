package com.emmanuel.wechatdemo.activity;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

    private Toolbar toolbar;
    private TextView tvTitle;
    private ImageView ivRight;

    private String title = "";
    private int leftVisibility = View.GONE;
    private int rightRes = 0;

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.left:
                    BaseActivity.this.finish();
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
        ivRight = (ImageView)findViewById(R.id.iv_right_1);
        if(rightRes != 0) {
            ivRight.setImageResource(rightRes);
            ivRight.setVisibility(View.VISIBLE);
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
}
