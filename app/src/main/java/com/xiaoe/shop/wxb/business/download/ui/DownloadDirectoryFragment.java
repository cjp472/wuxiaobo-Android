package com.xiaoe.shop.wxb.business.download.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

import com.xiaoe.common.entitys.ColumnDirectoryEntity;
import com.xiaoe.common.entitys.ColumnSecondDirectoryEntity;
import com.xiaoe.network.downloadUtil.DownloadManager;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.adapter.download.BatchDownloadAdapter;
import com.xiaoe.shop.wxb.base.BaseFragment;
import com.xiaoe.shop.wxb.interfaces.OnSelectListener;

public class DownloadDirectoryFragment extends BaseFragment implements View.OnClickListener, OnSelectListener {
    private static final String TAG = "DownloadDirectory";
    private RecyclerView downloadRecyclerView;
    private BatchDownloadAdapter adapter;
    private ImageView allSetectImage;
    private TextView allSelectText;
    private RelativeLayout btnAllSelect;
    private boolean isAllSelect = false;
    private View mRootView;
    private TextView btnDownload;
    private String resourceId;
    private String fromType;
    private TextView selectCount;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_download_directory, container, false);
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initData();
    }

    private void initData() {
        Intent intent = getActivity().getIntent();
        String dataJson = intent.getStringExtra("bundle_dataJSON");
        resourceId = intent.getStringExtra("resourceId");
        fromType = intent.getStringExtra("from_type");
        List<ColumnDirectoryEntity> dataList = JSONObject.parseArray(dataJson, ColumnDirectoryEntity.class);
        adapter.addAllData(dataList);
    }

    private void initView() {
        downloadRecyclerView = (RecyclerView) mRootView.findViewById(R.id.download_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setAutoMeasureEnabled(true);
        downloadRecyclerView.setLayoutManager(layoutManager);
        adapter = new BatchDownloadAdapter(getContext());
        adapter.setSelectListener(this);
        downloadRecyclerView.setAdapter(adapter);

        allSetectImage = (ImageView) mRootView.findViewById(R.id.all_select_image);
        allSelectText = (TextView) mRootView.findViewById(R.id.all_select_text);
        btnAllSelect = (RelativeLayout) mRootView.findViewById(R.id.btn_all_select);
        btnAllSelect.setOnClickListener(this);

        btnDownload = (TextView) mRootView.findViewById(R.id.btn_download);
        btnDownload.setOnClickListener(this);

        selectCount = (TextView) mRootView.findViewById(R.id.select_size);
        selectCount.setText(String.valueOf("已选 0条"));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_all_select:
                clickAllSelect();
                break;
            case R.id.btn_download:
                clickDownload();
                break;
            default:
                break;
        }
    }

    private void clickDownload() {
        boolean download = false;
        for (ColumnDirectoryEntity directoryEntity : adapter.getDate()){
            for (ColumnSecondDirectoryEntity secondDirectoryEntity : directoryEntity.getResource_list()){
                if(secondDirectoryEntity.isSelect() && secondDirectoryEntity.isEnable()){
                    download = true;
                    secondDirectoryEntity.setEnable(false);
                    DownloadManager.getInstance().addDownload(null, null, secondDirectoryEntity);
                }
            }
        }
        if(download){
            download = false;
            toastCustom(getString(R.string.add_download_list));
            adapter.notifyDataSetChanged();
        }else{
            toastCustom(getString(R.string.please_select_download));
        }
    }

    private void setAllsetectState(){
        if(isAllSelect){
            allSelectText.setText(getResources().getString(R.string.cancel_text));
            allSetectImage.setImageResource(R.mipmap.download_checking);
        }else{
            allSelectText.setText(getResources().getString(R.string.all_select_text));
            allSetectImage.setImageResource(R.mipmap.download_tocheck);
        }
    }
    private void clickAllSelect() {
        isAllSelect = !isAllSelect;
        setAllsetectState();
        int count = 0;
        for (ColumnDirectoryEntity item : adapter.getDate()) {
            item.setSelect(isAllSelect);
            for (ColumnSecondDirectoryEntity childItem : item.getResource_list()) {
                if(childItem.isEnable()){
                    childItem.setSelect(isAllSelect);
                    count = isAllSelect ? count + 1 : 0;
                }
            }
        }
        adapter.notifyDataSetChanged();
        selectCount.setText(String.valueOf("已选 "+count+"条"));
    }

    @Override
    public void onSelect(int positiont) {
        int count = 0;
        int childCount = 0;
        for (ColumnDirectoryEntity item: adapter.getDate()) {
            if(item.isSelect()){
                count++;
            }
            for (ColumnSecondDirectoryEntity childItem : item.getResource_list()){
                if(childItem.isSelect()){
                    childCount++;
                }
            }
        }
        isAllSelect = count == adapter.getDate().size();
        setAllsetectState();
        selectCount.setText(String.valueOf("已选 "+childCount+"条"));

    }
}