package com.emmanuel.wechatdemo.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by user on 2016/8/22.
 */
public class ColorFilterImageView extends ImageView implements View.OnTouchListener {

    private boolean canTouchSwitch = true;

    public ColorFilterImageView(Context context) {
        this(context, null);
    }

    public ColorFilterImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorFilterImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (canTouchSwitch)
                    turnOff();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (canTouchSwitch)
                    turnOn();
                break;
            default:
                break;
        }
        return false;
    }

    public void turnOn(){
        setColorFilter(Color.TRANSPARENT);
    }

    public void turnOff(){
        setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
    }

    public void setCanTouchSwitch(boolean touchSwitch){
        this.canTouchSwitch = touchSwitch;
    }
}
