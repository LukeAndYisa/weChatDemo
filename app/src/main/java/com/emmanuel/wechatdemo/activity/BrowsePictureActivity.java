package com.emmanuel.wechatdemo.activity;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.emmanuel.wechatdemo.R;
import com.emmanuel.wechatdemo.bean.Picture;
import com.emmanuel.wechatdemo.view.PinchImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2016/9/11.
 */
public class BrowsePictureActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "BrowsePictureActivity";
    private ViewPager viewPager;

    private List<Picture> pictureList;
    private ImageView ivSelect;
    private int curPicturePos = 0;
    private int selectCount = 0;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_browse_picture);
        setLeftBtnVisibility(View.VISIBLE);

        pictureList = (ArrayList<Picture>)getIntent().getSerializableExtra("selectedPictures");
        setTitle("1/" + pictureList.size());
        selectCount = pictureList.size();
        setTvRight1Text(selectCount + "张" + getString(R.string.text_select));

        ivSelect = (ImageView)findViewById(R.id.iv_select);
        ivSelect.setOnClickListener(this);

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


    class MyViewPagerAdapter extends PagerAdapter {
        private List<View> mListViews;

        public MyViewPagerAdapter() {
            mListViews = new ArrayList<>();
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.view_browse_picture, null);
            mListViews.add(view);
            for(int i=1; i<pictureList.size(); i++){
                View view1 = inflater.inflate(R.layout.view_browse_picture, null);
                mListViews.add(view1);
            }
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object)   {
            container.removeView(mListViews.get(position));//删除页卡
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if(position <0 || position >=pictureList.size())
                return null;
            PinchImageView imageView = (PinchImageView)mListViews.get(position).findViewById(R.id.iv_picture);
            ImageLoader.getInstance().displayImage(pictureList.get(position).uri, imageView);
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
