/*
 * Copyright (C) 2015-2016 Jacksgong(blog.dreamtobe.cn)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.dreamtobe.kpswitch.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import cn.dreamtobe.kpswitch.IPanelConflictLayout;
import cn.dreamtobe.kpswitch.IPanelHeightTarget;
import cn.dreamtobe.kpswitch.handler.KPSwitchPanelLayoutHandler;
import cn.dreamtobe.kpswitch.util.ViewUtil;

/**
 * Created by Jacksgong on 3/30/16.
 * <p/>
 * The panel container frame layout.
 * Resolve the layout-conflict from switching the keyboard and the Panel.
 * <p/>
 * For full-screen theme window, please use {@link KPSwitchFSPanelFrameLayout} instead.
 *
 * @see KPSwitchPanelLinearLayout
 * @see KPSwitchPanelRelativeLayout
 * @see KPSwitchPanelLayoutHandler
 */
public class KPSwitchPanelFrameLayout extends FrameLayout implements IPanelHeightTarget,
        IPanelConflictLayout {

    private KPSwitchPanelLayoutHandler panelLayoutHandler;

    public KPSwitchPanelFrameLayout(Context context) {
        super(context);
        init();
    }

    public KPSwitchPanelFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public KPSwitchPanelFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public KPSwitchPanelFrameLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        panelLayoutHandler = new KPSwitchPanelLayoutHandler(this);
    }

    @Override
    public void setVisibility(int visibility) {
        if (panelLayoutHandler.filterSetVisibility(visibility)) {
            return;
        }
        super.setVisibility(visibility);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int[] processedMeasureWHSpec = panelLayoutHandler.processOnMeasure(widthMeasureSpec,
                heightMeasureSpec);

        super.onMeasure(processedMeasureWHSpec[0], processedMeasureWHSpec[1]);
    }

    @Override
    public boolean isKeyboardShowing() {
        return panelLayoutHandler.isKeyboardShowing();
    }

    @Override
    public boolean isVisible() {
        return panelLayoutHandler.isVisible();
    }

    @Override
    public void handleShow() {
        super.setVisibility(View.VISIBLE);
    }

    @Override
    public void handleHide() {
        panelLayoutHandler.handleHide();
    }

    @Override
    public void refreshHeight(int panelHeight) {
        ViewUtil.refreshHeight(this, panelHeight);
    }

    @Override
    public void onKeyboardShowing(boolean showing) {
        panelLayoutHandler.setIsKeyboardShowing(showing);
    }
}
