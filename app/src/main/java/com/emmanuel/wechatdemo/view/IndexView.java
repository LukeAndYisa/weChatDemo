package com.emmanuel.wechatdemo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.emmanuel.wechatdemo.R;
import com.emmanuel.wechatdemo.util.DensityUtil;
import com.emmanuel.wechatdemo.util.LogUtil;

/**
 * 自定义右侧索引
 * Created by user on 2016/9/13.
 */
public class IndexView extends View{

    private static final String TAG = "IndexView";

    public static final String[] INDEX_KEY = {"↑","A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z", "#"};
    private Paint paint = new Paint();
    private int myHeight = 0;
    private int myWidth = 0;

    private OnIndexListener onIndexListener;

    public IndexView(Context context) {
        super(context);
        init();
    }

    public IndexView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IndexView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        paint.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setAntiAlias(true);
        paint.setTextSize((float)DensityUtil.sp2px(getContext(), 15));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        myHeight = getMeasuredHeight();
        myWidth = getMeasuredWidth();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int perHeight = myHeight / INDEX_KEY.length;
        for(int i=0; i<INDEX_KEY.length; i++){
            float xPos = myWidth / 2 - paint.measureText(INDEX_KEY[i]) / 2;
            float yPos = perHeight * i + perHeight;
            canvas.drawText(INDEX_KEY[i], xPos, yPos, paint);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            setBackgroundColor(ContextCompat.getColor(getContext(), R.color.bg_index_view_touch));
            int y = (int)event.getY();
            int index = calculateIndex(y);
            if(onIndexListener != null){
                onIndexListener.onStart(index, INDEX_KEY[index]);
            }
            return true;
        } else if(event.getAction() == MotionEvent.ACTION_UP){
            setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_null));
            onIndexListener.onEnd();

        } else if(event.getAction() == MotionEvent.ACTION_MOVE){
            int y = (int)event.getY();
            int index = calculateIndex(y);
            LogUtil.logD(TAG, "index = " + index + "  word = " + INDEX_KEY[index]);
            if(onIndexListener != null){
                onIndexListener.onSelectedIndex(index, INDEX_KEY[index]);
            }
        }
        return super.onTouchEvent(event);
    }

    private int calculateIndex(int Y){
        int perHeight = myHeight / INDEX_KEY.length;
        int index = Y/perHeight;
        return index;
    }

    public interface OnIndexListener{
        public void onSelectedIndex(int index, String word);

        public void onStart(int index, String word);

        public void onEnd();
    }

    public void addOnIndexListener(OnIndexListener onIndexListener){
        this.onIndexListener = onIndexListener;
    }

}
