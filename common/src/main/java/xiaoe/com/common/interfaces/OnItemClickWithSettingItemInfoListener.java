package xiaoe.com.common.interfaces;

import android.view.View;

import xiaoe.com.common.entitys.SettingItemInfo;

public interface OnItemClickWithSettingItemInfoListener {

    /**
     * item 点击事件
     * @param view 点击的 view
     * @param itemInfo 点击的 view 对应的对象
     */
    public void onItemClick(View view, SettingItemInfo itemInfo);
}
