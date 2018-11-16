package com.xiaoe.shop.wxb.business.download.ui;

import android.app.Dialog;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import com.xiaoe.common.app.Global;
import com.xiaoe.common.entitys.DownloadTableInfo;
import com.xiaoe.common.interfaces.OnDownloadListener;
import com.xiaoe.network.downloadUtil.DownloadManager;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.adapter.download.DownloadProceedChildListAdapter;
import com.xiaoe.shop.wxb.base.BaseFragment;
import com.xiaoe.shop.wxb.interfaces.OnDownloadListListener;

public class DownloadProceedFragment extends BaseFragment implements View.OnClickListener, OnDownloadListListener, OnDownloadListener {
    private static final String TAG = "DownloadProceedFragment";
    private View rootView;
    private RecyclerView downloadProceedRecyclerView;
    private DownloadProceedChildListAdapter adapter;
    private TextView btnAllDelete;
    private Dialog dialog;
    private Window window;
    private TextView btnAllStart;
    private boolean isAllDownload = false;
    private boolean runClickAllDownload = false;
    private RelativeLayout bottomButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_download_proceed, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        initData();
    }

    private void initViews() {
        DownloadManager.getInstance().setOnDownloadListener(TAG, this);

        downloadProceedRecyclerView = (RecyclerView) rootView.findViewById(R.id.download_proceed_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setAutoMeasureEnabled(true);
        downloadProceedRecyclerView.setLayoutManager(layoutManager);
        adapter = new DownloadProceedChildListAdapter(getContext(), this);
        downloadProceedRecyclerView.setAdapter(adapter);

        btnAllDelete = (TextView) rootView.findViewById(R.id.btn_all_delete);
        btnAllDelete.setOnClickListener(this);

        btnAllStart = (TextView) rootView.findViewById(R.id.btn_all_start_download);
        btnAllStart.setOnClickListener(this);

        bottomButton = (RelativeLayout) rootView.findViewById(R.id.bottom_button);
    }
    private void initData() {
        List<DownloadTableInfo> list = DownloadManager.getInstance().getDownloadingList();
        if(list == null || list.size() <= 0){
            bottomButton.setVisibility(View.GONE);
        }else {
            bottomButton.setVisibility(View.VISIBLE);
            adapter.addAllData(list);
            isAllDownload = isAllDownload();
        }
        setAllDownloadButton(isAllDownload);
    }

    private void setAllDownloadButton(boolean all) {
        if(all){
            btnAllStart.setText(getString(R.string.all_stop_download));
        }else{
            btnAllStart.setText(getString(R.string.all_start_download));
        }
    }

    private boolean isAllDownload(){
        int downloadCount = 0;
        for(DownloadTableInfo item : adapter.getData()){
            if(item.getDownloadState() == 0 || item.getDownloadState() == 1){
                downloadCount++;
            }
        }
        return downloadCount == adapter.getData().size();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        DownloadManager.getInstance().removeOnDownloadListener(TAG);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_all_delete:
                allDelete();
                break;
            case R.id.btn_all_start_download:
                allStartDownload();
                break;
            default:
                break;
        }
    }

    private void allStartDownload() {
        if(adapter.getItemCount() <= 0 || runClickAllDownload){
            return;
        }
        runClickAllDownload = true;
        if(isAllDownload){
            //先停止等待中的下载任务，因为如果先停止下载中的，会自动下载等待中的
            for (DownloadTableInfo download : adapter.getData()){
                if(download.getDownloadState() == 0){
                    DownloadManager.getInstance().pause(download);
                }
            }
            for (DownloadTableInfo download : adapter.getData()){
                if(download.getDownloadState() == 1){
                    DownloadManager.getInstance().pause(download);
                }
            }
        }else{
            for (DownloadTableInfo download : adapter.getData()){
                if(download.getDownloadState() == 2){
                    DownloadManager.getInstance().start(download);
                }
            }
        }
        runClickAllDownload = false;
        isAllDownload = !isAllDownload;
        setAllDownloadButton(isAllDownload);
    }

    private void allDelete() {
        if(adapter.getItemCount() > 0){
            deleteAll();
        }
    }

    @Override
    public void downloadItem(DownloadTableInfo download, int position, int type) {
        if(type == 0){
            //开始、暂停
            if(download.getDownloadState() == 0 || download.getDownloadState() == 1){
                DownloadManager.getInstance().pause(download);
            }else if(download.getDownloadState() == 2){
                DownloadManager.getInstance().start(download);
            }
        }else if(type == 1){
            //删除
            delete(download, position);
        }
        setAllDownloadButton(isAllDownload());
    }


    public void delete(final DownloadTableInfo download, final int position){
        if(dialog == null){
            dialog = new Dialog(getContext(), R.style.ActionSheetDialogStyle);
        }
        if(window == null){
            window = dialog.getWindow();
        }
        View view = getActivity().getLayoutInflater().inflate(R.layout.layout_delete_dialog, null);

        TextView btnCancel = (TextView) view.findViewById(R.id.radius_dialog_btn_cancel);
        final TextView btnConfirm = (TextView) view.findViewById(R.id.radius_dialog_btn_confirm);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                DownloadManager.getInstance().removeDownloading(download);
                adapter.getData().remove(position);
                adapter.notifyDataSetChanged();
                if(adapter.getItemCount() <= 0){
                    bottomButton.setVisibility(View.GONE);
                }
            }
        });
        // 先 show 后才会有宽高
        dialog.show();

        if (window != null) {
            window.setGravity(Gravity.CENTER);
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            Point point = Global.g().getDisplayPixel();
            layoutParams.width = (int) (point.x * 0.8);
            window.setAttributes(layoutParams);
        }
        dialog.setContentView(view);
    }
    public void deleteAll(){
        if(dialog == null){
            dialog = new Dialog(getContext(), R.style.ActionSheetDialogStyle);
        }
        if(window == null){
            window = dialog.getWindow();
        }
        View view = getActivity().getLayoutInflater().inflate(R.layout.layout_delete_dialog, null);

        TextView btnCancel = (TextView) view.findViewById(R.id.radius_dialog_btn_cancel);
        final TextView btnConfirm = (TextView) view.findViewById(R.id.radius_dialog_btn_confirm);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (DownloadTableInfo download : adapter.getData()){
                    DownloadManager.getInstance().removeDownloading(download);
                }
                adapter.clearData();
                dialog.dismiss();
                bottomButton.setVisibility(View.GONE);
            }
        });
        // 先 show 后才会有宽高
        dialog.show();

        if (window != null) {
            window.setGravity(Gravity.CENTER);
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            Point point = Global.g().getDisplayPixel();
            layoutParams.width = (int) (point.x * 0.8);
            window.setAttributes(layoutParams);
        }
        dialog.setContentView(view);
    }

    @Override
    public void onDownload(DownloadTableInfo downloadInfo, float progress, int status) {
        if(status == 3){
            //下载完成
            if(dialog != null && dialog.isShowing()){
                dialog.dismiss();
            }
            for (DownloadTableInfo item : adapter.getData()){
                if(item.getResourceId().equals(downloadInfo.getResourceId())){
                    adapter.getData().remove(item);
                    adapter.notifyDataSetChanged();
                    break;
                }
            }
            if(adapter.getItemCount() <= 0){
                bottomButton.setVisibility(View.GONE);
            }
        }
        int index = -1;
        for (DownloadTableInfo item : adapter.getData()){
            index++;
            if(item.getResourceId().equals(downloadInfo.getResourceId())){
                item.setProgress(downloadInfo.getProgress());
                adapter.notifyItemChanged(index);
                break;
            }
        }
    }
}