package com.xiaoe.shop.wxb.adapter.download;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import com.xiaoe.common.entitys.DownloadTableInfo;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.BaseViewHolder;
import com.xiaoe.shop.wxb.interfaces.IonSlidingButtonListener;
import com.xiaoe.shop.wxb.interfaces.OnDownloadListListener;
import com.xiaoe.shop.wxb.widget.SlidingButtonView;

public class DownloadProceedChildListAdapter extends RecyclerView.Adapter<BaseViewHolder>  implements IonSlidingButtonListener {
    private static final String TAG = "DownloadProceedListAdap";
    private final Context mContext;
    private SlidingButtonView bntDelete;
    private List<DownloadTableInfo> downloadResourceList;
    private OnDownloadListListener listener;

    public DownloadProceedChildListAdapter(Context context, OnDownloadListListener listener) {
        mContext = context;
        downloadResourceList = new ArrayList<DownloadTableInfo>();
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position, List<Object> payloads) {
        if (payloads == null || payloads.isEmpty())
            super.onBindViewHolder(holder, position, payloads);
        else {
            DownloadProceedChildListHolder childListHolder = (DownloadProceedChildListHolder) holder;
            childListHolder.bindChangeView(downloadResourceList.get(position));
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(mContext).inflate(R.layout.item_child_download_proceed, parent, false);
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_child_download_proceed_new, parent, false);
        return new DownloadProceedChildListHolder(view, this, listener);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        DownloadProceedChildListHolder childListHolder = (DownloadProceedChildListHolder) holder;
        childListHolder.bindView(downloadResourceList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return downloadResourceList.size();
    }

    @Override
    public void onMenuIsOpen(View view) {
        bntDelete = (SlidingButtonView) view;
    }

    @Override
    public void onDownOrMove(SlidingButtonView slidingButtonView) {
        if(menuIsOpen()){
            if(bntDelete != slidingButtonView){
                closeMenu();
            }
        }
    }

    /**
     * 关闭菜单
     */
    public void closeMenu() {
        bntDelete.closeMenu();
        bntDelete = null;
    }
    /**
     * 判断是否有菜单打开
     */
    public Boolean menuIsOpen() {
        if(bntDelete != null){
            return true;
        }
        return false;
    }
    public void addAllData(List<DownloadTableInfo> list){
        downloadResourceList.addAll(list);
        notifyDataSetChanged();
    }
    public void setData(List<DownloadTableInfo> list){
        downloadResourceList.clear();
        downloadResourceList.addAll(list);
        notifyDataSetChanged();
    }
    public void clearData(){
        downloadResourceList.clear();
        notifyDataSetChanged();
    }

    public List<DownloadTableInfo> getData(){
        return downloadResourceList;
    }
}
