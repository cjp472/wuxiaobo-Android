package com.xiaoe.shop.wxb.adapter.decorate.graphic_navigation;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.List;

import com.xiaoe.common.entitys.GraphicNavItem;
import com.xiaoe.common.interfaces.OnItemClickWithNavItemListener;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.BaseViewHolder;
import com.xiaoe.shop.wxb.common.datareport.EventReportManager;
import com.xiaoe.shop.wxb.common.datareport.MobclickEvent;
import com.xiaoe.shop.wxb.events.OnClickEvent;

public class GraphicNavRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private Context mContext;
    private List<GraphicNavItem> mItemList;

    private OnItemClickWithNavItemListener onItemClickWithNavItemListener;

    public void setOnItemClickWithNavItemListener (OnItemClickWithNavItemListener onItemClickWithNavItemListener) {
        this.onItemClickWithNavItemListener = onItemClickWithNavItemListener;
    }

    public GraphicNavRecyclerAdapter(Context context, List<GraphicNavItem> itemList) {
        this.mContext = context;
        this.mItemList = itemList;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.graphic_navigatioin_item, null);
        return new GraphicNavItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        GraphicNavItemViewHolder graphicNavItemViewHolder = (GraphicNavItemViewHolder) holder;
        final int innerPos = graphicNavItemViewHolder.getAdapterPosition();
        graphicNavItemViewHolder.itemIcon.setImageURI(mItemList.get(innerPos).getNavIcon());
        graphicNavItemViewHolder.itemContent.setText(mItemList.get(innerPos).getNavContent());
        graphicNavItemViewHolder.itemWrap.setOnClickListener(new OnClickEvent(OnClickEvent.DEFAULT_SECOND) {
            @Override
            public void singleClick(View v) {
                if (onItemClickWithNavItemListener != null) {
                    onItemClickWithNavItemListener.onNavItemClick(v, mItemList.get(innerPos));
                }

                HashMap<String, String> map = new HashMap<>(1);
                map.put(MobclickEvent.TITLE, mItemList.get(innerPos).getNavContent());
                EventReportManager.onEvent(mContext, MobclickEvent.COURSE_MENU_CLICK, map);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItemList == null ? 0 : mItemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
}
