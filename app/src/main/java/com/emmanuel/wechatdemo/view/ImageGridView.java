package com.emmanuel.wechatdemo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.emmanuel.wechatdemo.util.DensityUtil;

import java.util.List;

/**
 * Created by user on 2016/8/19.
 */
public class ImageGridView extends LinearLayout {

    private List<String> picList; //图片链接

    private final int COLUMN_NUM = 3; //列数
    private final int IMAGE_MARGIN = DensityUtil.dip2px(getContext(), 5);//图片间距

    private int width = 0, height = 0; //自己本身的宽高
    private int imageHeight, imageWidth; //图片宽度和高度
    private List<String> list;

    public ImageGridView(Context context) {
        super(context);
    }

    public ImageGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (width == 0) {
            int w = measureWidth(widthMeasureSpec);
            if (w > 0) {
                width = w;
                if (picList != null && picList.size() > 0) {
                    setList(picList);
                }
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private int measureWidth(int widthMeasureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize;
        } else {
            // Measure the text
            // result = (int) mTextPaint.measureText(mText) + getPaddingLeft()
            // + getPaddingRight();
            if (specMode == MeasureSpec.AT_MOST) {
                // Respect AT_MOST value if that was what is called for by
                // measureSpec
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    public void setList(List<String> list) {
        if(picList==null){
            throw new IllegalArgumentException("list is null");
        }
        picList = list;
        if(width > 0){
            pxMoreWandH = (MAX_WIDTH - pxImagePadding*2 )/3; //解决右侧图片和内容对不齐问题
            pxOneMaxWandH = MAX_WIDTH * 2 / 3;
            initImageLayoutParams();
        }
        initView();
    }

    private void initImageLayoutParams() {

    }
}
