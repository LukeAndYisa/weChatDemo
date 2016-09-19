package com.emmanuel.wechatdemo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.emmanuel.wechatdemo.R;
import com.emmanuel.wechatdemo.bean.Word;
import com.emmanuel.wechatdemo.util.DensityUtil;

/**
 * Created by user on 2016/9/13.
 */
public class ContactsDividerItemDecoration extends RecyclerView.ItemDecoration {

    private Context context;
    private Word titleIndex[];
    private Paint paint;
    private int bgColor;
    private int textColor;
    private int lineColor;
    private float titleHeight;
    private Rect rect;

    public ContactsDividerItemDecoration(Context context, Word index[]){
        this.context = context;
        this.titleIndex = index;
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize(DensityUtil.sp2px(context, 14) * 1.0f);
//        paint.setColor(ContextCompat.getColor(context, R.color.text_color_gray_deep));
        bgColor = ContextCompat.getColor(context, R.color.text_color_gray_light);
        textColor = ContextCompat.getColor(context, R.color.text_color_gray_deep);
        lineColor = ContextCompat.getColor(context, R.color.color_line);
        titleHeight = DensityUtil.dip2px(context, 30);
        rect = new Rect();
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
        if(position > -1){
            if(isTitle(position) > -1)
                outRect.set(0, (int)titleHeight, 0, 0);
            else
                super.getItemOffsets(outRect, view, parent, state);
        }
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int position = params.getViewLayoutPosition();

            if (position > -1) {
                if (isTitle(position) > -1) {
                    drawTitle(c, left, right, child, params, isTitle(position));
                } else {
                    drawLine(c, left, right, child);
                }
            }
        }
    }

    private void drawTitle(Canvas c, int left, int right, View child, RecyclerView.LayoutParams params, int index) {
        String title = titleIndex[index].title;
        paint.setColor(bgColor);
        c.drawRect(left, child.getTop() - params.topMargin - titleHeight, right, child.getTop() - params.topMargin, paint);
        paint.setColor(textColor);
        paint.getTextBounds(title, 0, title.length(), rect);
        c.drawText(title, child.getPaddingLeft(), child.getTop() - params.topMargin - (titleHeight / 2 - rect.height() / 2), paint);
    }

    private void drawLine(Canvas c, int left, int right, View child){
        paint.setColor(lineColor);
        c.drawLine(left, child.getTop(), right, child.getTop()+1, paint);
    }


    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {

    }

    private int isTitle(int i){
        if(titleIndex == null)
            return -1;
        for(int k=0; k<titleIndex.length; k++){
            if(titleIndex[k] != null && i == titleIndex[k].index)
                return k;
        }
        return -1;
    }

}
