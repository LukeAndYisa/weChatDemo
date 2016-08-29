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
package cn.dreamtobe.kpswitch.handler;

import android.view.View;

import cn.dreamtobe.kpswitch.IPanelConflictLayout;

/**
 * Created by Jacksgong on 3/30/16.
 * <p/>
 * Keyboard->Panel: if the keyboard is showing, and {@code visibility} equal {@link View#VISIBLE}
 * then must by Keyboard->Panel, then show the panel after the keyboard is real gone, and will be
 * show by {@link IPanelConflictLayout#handleShow()}.
 * Easy and Safe way: {@link cn.dreamtobe.kpswitch.util.KPSwitchConflictUtil#showPanel(View)}.
 * <p/>
 * Panel->Keyboard: do not need to invoke {@link View#setVisibility(int)} to let the panel gone,
 * just show keyboard, the panel will be gone automatically when keyboard is real visible, and will
 * be hide by {@link #handleHide()} -> {@link #processOnMeasure(int, int)}.
 * Easy and safe way: {@link cn.dreamtobe.kpswitch.util.KPSwitchConflictUtil#showKeyboard(View, View)}.
 *
 * @see cn.dreamtobe.kpswitch.widget.KPSwitchPanelFrameLayout
 * @see cn.dreamtobe.kpswitch.widget.KPSwitchPanelLinearLayout
 * @see cn.dreamtobe.kpswitch.widget.KPSwitchPanelRelativeLayout
 */
public class KPSwitchPanelLayoutHandler implements IPanelConflictLayout {
    private final View panelLayout;

    /**
     * The real status of Visible or not
     *
     * @see #handleHide()
     * @see #filterSetVisibility(int) (int)
     * <p/>
     * if true, the status is non-Visible or will
     * non-Visible(may delay and handle in {@link #processOnMeasure(int, int)})
     * <p/>
     * The value of {@link View#getVisibility()} maybe be assigned dully for cover the keyboard->panel.
     * In this case, the {@code mIsHide} will mark the right status.
     * Handle by {@link #filterSetVisibility(int)} & {@link #processOnMeasure(int, int)}
     */
    private boolean mIsHide = false;

    public KPSwitchPanelLayoutHandler(final View panelLayout) {
        this.panelLayout = panelLayout;
    }

    /**
     * Filter the {@link View#setVisibility(int)} for handling Keyboard->Panel.
     *
     * @param visibility {@link View#setVisibility(int)}
     * @return whether filtered out or not.
     */
    public boolean filterSetVisibility(final int visibility) {
        if (visibility == View.VISIBLE) {
            this.mIsHide = false;
        }

        if (visibility == panelLayout.getVisibility()) {
            return true;
        }

        /**
         * For handling Keyboard->Panel.
         *
         * Will be handled on {@link KPSwitchRootLayoutHandler#handleBeforeMeasure(int, int)} ->
         * {@link IPanelConflictLayout#handleShow()} Delay show, until the {@link KPSwitchRootLayoutHandler} discover
         * the size is changed by keyboard-show. And will show, on the next frame of the above
         * change discovery.
         */
        if (isKeyboardShowing() && visibility == View.VISIBLE) {
            return true;
        }

        return false;
    }

    private final int[] processedMeasureWHSpec = new int[2];

    /**
     * Handle Panel -> Keyboard.
     * <p/>
     * Process the {@link View#onMeasure(int, int)} for handling the case of Panel->Keyboard.
     *
     * @return the processed measure-width-spec and measure-height-spec.
     * @see #handleHide()
     */
    public int[] processOnMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mIsHide) {
            panelLayout.setVisibility(View.GONE);
            /**
             * The current frame will be visible nil.
             */
            widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.EXACTLY);
            heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.EXACTLY);
        }

        processedMeasureWHSpec[0] = widthMeasureSpec;
        processedMeasureWHSpec[1] = heightMeasureSpec;

        return processedMeasureWHSpec;
    }

    private boolean mIsKeyboardShowing = false;

    public void setIsKeyboardShowing(final boolean isKeyboardShowing) {
        mIsKeyboardShowing = isKeyboardShowing;
    }

    @Override
    public boolean isKeyboardShowing() {
        return mIsKeyboardShowing;
    }


    @Override
    public boolean isVisible() {
        return !mIsHide;
    }

    @Override
    public void handleShow() {
        throw new IllegalAccessError("You can't invoke handle show in handler," +
                " please instead of handling in the panel layout, maybe just need invoke " +
                "super.setVisibility(View.VISIBLE)");
    }

    /**
     * @see #processOnMeasure(int, int)
     */
    @Override
    public void handleHide() {
        this.mIsHide = true;
    }
}
