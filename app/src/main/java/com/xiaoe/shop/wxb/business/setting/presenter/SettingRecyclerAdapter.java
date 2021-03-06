package com.xiaoe.shop.wxb.business.setting.presenter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.List;

import com.xiaoe.common.entitys.SettingItemInfo;
import com.xiaoe.common.interfaces.OnItemClickWithSettingItemInfoListener;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.BaseViewHolder;

import butterknife.internal.DebouncingOnClickListener;

public class SettingRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private Context mContext;
    private List<SettingItemInfo> itemList;

    private OnItemClickWithSettingItemInfoListener onItemClickWithSettingItemInfoListener;

    // 默认会有底部 margin
    public SettingRecyclerAdapter(Context context, List<SettingItemInfo> itemList) {
        this.mContext = context;
        this.itemList = itemList;
    }

    public void setOnItemClickWithSettingItemInfoListener(OnItemClickWithSettingItemInfoListener listener) {
        this.onItemClickWithSettingItemInfoListener = listener;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.setting_item, null);
        view.setLayoutParams(layoutParams);
        return new SettingItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        SettingItemViewHolder viewHolder = (SettingItemViewHolder) holder;
        final int tempPos = viewHolder.getAdapterPosition();
        final SettingItemInfo currentItem = itemList.get(tempPos);
        if (tempPos == 0) { // 是头像，约定如果有 title 会优先显示 title
            if (mContext.getString(R.string.avatar_text).equals(currentItem.getItemTitle())) {
                viewHolder.itemIcon.setVisibility(View.VISIBLE);
                viewHolder.itemContent.setVisibility(View.GONE);
                viewHolder.itemGo.setVisibility(View.GONE);
                viewHolder.itemTitle.setText(currentItem.getItemTitle());
                viewHolder.itemIcon.setImageURI(currentItem.getItemIcon());
            } else if (mContext.getString(R.string.connect_us).equals(currentItem.getItemTitle())) {
                viewHolder.itemIcon.setVisibility(View.VISIBLE);
                viewHolder.itemContent.setVisibility(View.VISIBLE);
                viewHolder.itemGo.setVisibility(View.GONE);
                viewHolder.itemTitle.setText(currentItem.getItemTitle());
                viewHolder.itemContent.setText(currentItem.getItemContent());
                viewHolder.itemIcon.setImageURI(""); // 占位
            } else {
                viewHolder.itemIcon.setVisibility(View.VISIBLE);
                viewHolder.itemContent.setVisibility(View.GONE);
                viewHolder.itemGo.setVisibility(View.VISIBLE);
                viewHolder.itemTitle.setText(currentItem.getItemTitle());
                viewHolder.itemIcon.setImageURI(""); // 占位
            }
        } else { // 其他内容（手机号暂不支持修改因此将 go 箭头去掉）
            viewHolder.itemIcon.setVisibility(View.GONE);
            viewHolder.itemContent.setVisibility(View.VISIBLE);
            if (mContext.getString(R.string.phone_number).equals(currentItem.getItemTitle())) {
                viewHolder.itemGo.setVisibility(View.GONE);
            }
            viewHolder.itemTitle.setText(currentItem.getItemTitle());
            viewHolder.itemContent.setText(currentItem.getItemContent());
        }
        if (tempPos != itemList.size()) {
            viewHolder.itemContainer.setOnClickListener(new DebouncingOnClickListener() {
                @Override
                public void doClick(View v) {
                    if (onItemClickWithSettingItemInfoListener != null) {
                        onItemClickWithSettingItemInfoListener.onItemClick(v, currentItem);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
}
