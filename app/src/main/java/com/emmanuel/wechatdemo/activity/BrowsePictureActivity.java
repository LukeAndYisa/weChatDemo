package com.emmanuel.wechatdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.emmanuel.wechatdemo.R;
import com.emmanuel.wechatdemo.bean.Picture;
import com.emmanuel.wechatdemo.event.DefaultEvent;
import com.emmanuel.wechatdemo.util.Constants;
import com.emmanuel.wechatdemo.util.ImageLoadUtil;
import com.emmanuel.wechatdemo.view.PinchImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2016/9/11.
 */
public class BrowsePictureActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "BrowsePictureActivity";
    private ViewPager viewPager;
    private RelativeLayout layoutBottom;

    private List<Picture> pictureList;
    private ImageView ivSelect;
    private int curPicturePos = 0;
    private int selectCount = 0;

    private int browseType;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_browse_picture);
        browseType = getIntent().getIntExtra("type", Constants.TYPE_BROWSE_AND_SELECTED);
        pictureList = (ArrayList<Picture>)getIntent().getSerializableExtra("selectedPictures");

        setLeftBtnVisibility(View.VISIBLE);
        setTitle("1/" + pictureList.size());

        ivSelect = (ImageView)findViewById(R.id.iv_select);
        ivSelect.setOnClickListener(this);

        layoutBottom = (RelativeLayout)findViewById(R.id.layout_bottom);
        viewPager = (ViewPager)findViewById(R.id.view_pager);
        viewPager.setAdapter(new MyViewPagerAdapter());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                curPicturePos = position;
                setTitle((curPicturePos + 1) + "/" + pictureList.size());
                if(pictureList.get(curPicturePos).isChecked){
                    ivSelect.setImageResource(R.mipmap.icon_checked);
                } else {
                    ivSelect.setImageResource(R.mipmap.icon_unchecked);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //不同的浏览方式，控制不同的控件显示
        if(browseType == Constants.TYPE_BROWSE_AND_SELECTED) {
            selectCount = pictureList.size();
            setTvRight1Text(selectCount + "张" + getString(R.string.text_select));
        } else {
            layoutBottom.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_select:
                pictureList.get(curPicturePos).isChecked = !pictureList.get(curPicturePos).isChecked;
                if(pictureList.get(curPicturePos).isChecked){
                    ivSelect.setImageResource(R.mipmap.icon_checked);
                    selectCount ++;
                } else {
                    ivSelect.setImageResource(R.mipmap.icon_unchecked);
                    selectCount--;
                }
                setTvRight1Text(selectCount + "张" + getString(R.string.text_select));
                break;
        }
    }

    @Override
    protected void onTextRight1() {
        super.onTextRight1();
        ArrayList<Picture>listSelectedPic = new ArrayList<>();
        for(int i=0; i<pictureList.size(); i++){
            if(pictureList.get(i).isChecked){
                listSelectedPic.add(pictureList.get(i));
            }
        }
        Intent intent = new Intent(this, PushShuoActivity.class);
        intent.putExtra("selectedPictures", listSelectedPic);
        startActivity(intent);
        EventBus.getDefault().post(new DefaultEvent(DefaultEvent.CLOSE_ACTIVITY));
        finish();
    }

    class MyViewPagerAdapter extends PagerAdapter {
        private List<View> mListViews;

        public MyViewPagerAdapter() {
            mListViews = new ArrayList<>();
            LayoutInflater inflater = getLayoutInflater();
            for(int i=0; i<pictureList.size(); i++){
                View view = inflater.inflate(R.layout.view_browse_picture, null);
                PinchImageView imageView = (PinchImageView)view.findViewById(R.id.iv_picture);
                ImageLoader.getInstance().displayImage(pictureList.get(i).uri, imageView, ImageLoadUtil.getOptions1());
                mListViews.add(view);
            }
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object)   {
            container.removeView(mListViews.get(position));//删除页卡
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mListViews.get(position), 0);
            return mListViews.get(position);
        }

        @Override
        public int getCount() {
            return  mListViews.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0==arg1;
        }
    }
}
