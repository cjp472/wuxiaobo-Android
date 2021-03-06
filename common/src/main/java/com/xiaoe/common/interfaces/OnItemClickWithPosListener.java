package com.xiaoe.common.interfaces;

import android.view.View;

public interface OnItemClickWithPosListener {

    /**
     * item 点击事件
     * @param view 点击的 view
     * @param position 点击的位置
     */
    public void onItemClick(View view, int position);
}
