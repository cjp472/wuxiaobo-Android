package xiaoe.com.shop.business.column.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import xiaoe.com.common.entitys.AudioPlayEntity;
import xiaoe.com.common.entitys.ColumnDirectoryEntity;
import xiaoe.com.common.entitys.ColumnSecondDirectoryEntity;
import xiaoe.com.shop.R;
import xiaoe.com.shop.adapter.tree.ParentViewHolder;
import xiaoe.com.shop.adapter.tree.TreeRecyclerAdapter;
import xiaoe.com.shop.base.BaseFragment;
import xiaoe.com.shop.business.audio.presenter.AudioMediaPlayer;
import xiaoe.com.shop.business.audio.presenter.AudioPlayUtil;
import xiaoe.com.shop.business.audio.presenter.AudioPresenter;
import xiaoe.com.shop.business.download.ui.DownloadActivity;
import xiaoe.com.shop.common.JumpDetail;
import xiaoe.com.shop.events.AudioPlayEvent;
import xiaoe.com.shop.interfaces.OnClickListPlayListener;

public class ColumnDirectoryFragment extends BaseFragment implements View.OnClickListener, OnClickListPlayListener {
    private static final String TAG = "ColumnDirectoryFragment";
    private View rootView;
    private RecyclerView directoryRecyclerView;
    private LinearLayout btnBatchDownload;
    private TreeRecyclerAdapter directoryAdapter;
    private boolean isHasBuy = false;
    private boolean isAddPlayList = false;
    private int playParentPosition = -1;

    public ColumnDirectoryFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_column_directory, null, false);
        EventBus.getDefault().register(this);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initData();
    }

    private void initView() {
        directoryRecyclerView = (RecyclerView) rootView.findViewById(R.id.directory_recycler_view);
        btnBatchDownload = (LinearLayout) rootView.findViewById(R.id.btn_batch_download);
        btnBatchDownload.setOnClickListener(this);
    }
    private void initData() {
        LinearLayoutManager treeLinearLayoutManager = new LinearLayoutManager(getContext());
        treeLinearLayoutManager.setAutoMeasureEnabled(true);
        directoryRecyclerView.setLayoutManager(treeLinearLayoutManager);
        directoryRecyclerView.setNestedScrollingEnabled(false);
        directoryAdapter = new TreeRecyclerAdapter(getContext(), this);
        directoryRecyclerView.setAdapter(directoryAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_batch_download:
                clickBatchDownload();
                break;
            default:
                break;
        }
    }

    private void clickBatchDownload() {
        Intent intent = new Intent(getContext(), DownloadActivity.class);
        startActivity(intent);
    }

    public void addData(List<ColumnDirectoryEntity> list){
        directoryAdapter.addAll(list);
    }
    public void setData(List<ColumnDirectoryEntity> list){
        directoryAdapter.setData(list);
    }

    @Override
    public void onPlayPosition(View view, int parentPosition, int position) {
        if(!isHasBuy){
            toastCustom("未购买课程");
            return;
        }
        ColumnDirectoryEntity parentEntity = directoryAdapter.getPositionData(parentPosition);
        isAddPlayList = playParentPosition == parentPosition;
        if(position == -1){
            //播放全部
            List<AudioPlayEntity> playList = setAudioPlayList(parentEntity.getResource_list());
            clickPlayAll(playList);

        }else{
            //播放某一个，同时获取播放列表
            ColumnSecondDirectoryEntity playEntity = parentEntity.getResource_list().get(position);
            if(playEntity.getResource_type() == 2){
                //音频
                List<AudioPlayEntity> playList = setAudioPlayList(parentEntity.getResource_list());
                if(!isAddPlayList){
                    AudioPlayUtil.getInstance().setAudioList(playList);
                }
                isAddPlayList = true;
                playPosition(playList, playEntity.getResource_id(), playEntity.getColumnId(), playEntity.getBigColumnId());
            }
        }
        if(playParentPosition < 0){
            playParentPosition = parentPosition;
        }
        notifyDataSetChanged(parentPosition, position);
        playParentPosition = parentPosition;
    }
    private void notifyDataSetChanged(int parentPosition, int position){
        ParentViewHolder parentViewHolder= (ParentViewHolder) directoryAdapter.getViewHolderList().get(parentPosition);
        parentViewHolder.getTreeChildRecyclerAdapter().notifyDataSetChanged();
        if(parentPosition != playParentPosition){
            ParentViewHolder oldParentViewHolder= (ParentViewHolder) directoryAdapter.getViewHolderList().get(playParentPosition);
            oldParentViewHolder.getTreeChildRecyclerAdapter().notifyDataSetChanged();
        }
//        if(position > 0){
//            parentViewHolder.getTreeChildRecyclerAdapter().notifyItemChanged(position);
//        }else{
//            parentViewHolder.getTreeChildRecyclerAdapter().notifyDataSetChanged();
//        }
    }
    @Override
    public void onJumpDetail(ColumnSecondDirectoryEntity itemData) {
        if(!isHasBuy){
            toastCustom("未购买课程");
            return;
        }
        int resourceType = itemData.getResource_type();
        String resourceId = itemData.getResource_id();
        if(resourceType == 1){
            //图文
            JumpDetail.jumpImageText(getContext(), resourceId, null);
        }else if(resourceType == 2){
            //音频
            JumpDetail.jumpAudio(getContext(), resourceId);
        }else if(resourceType == 3){
            //视频
            JumpDetail.jumpVideo(getContext(), resourceId, "");
        }else{
            toastCustom("未知课程");
            return;
        }
    }

    public void setHasBuy(boolean hasBuy) {
        isHasBuy = hasBuy;
    }

    /**
     * 设置播放列表
     * @param list
     */
    private List<AudioPlayEntity> setAudioPlayList(List<ColumnSecondDirectoryEntity> list){
        List<AudioPlayEntity> playList = new ArrayList<AudioPlayEntity>();
        int index = 0;
        for (ColumnSecondDirectoryEntity entity : list) {
            if(entity.getResource_type() != 2){
                continue;
            }
            AudioPlayEntity playEntity = new AudioPlayEntity();
            playEntity.setAppId(entity.getApp_id());
            playEntity.setResourceId(entity.getResource_id());
            playEntity.setIndex(index);
            playEntity.setCurrentPlayState(0);
            playEntity.setTitle(entity.getTitle());
            playEntity.setState(0);
            playEntity.setPlay(false);
            playEntity.setPlayUrl(entity.getAudio_url());
            playEntity.setCode(-1);
            playEntity.setHasBuy(isHasBuy ? 1 : 0);
            playEntity.setColumnId(entity.getColumnId());
            playEntity.setBigColumnId(entity.getBigColumnId());
            index++;
            playList.add(playEntity);
        }
        return playList;
    }

    private void clickPlayAll(List<AudioPlayEntity> playList) {

        if(playList.size() > 0){
            if(!isAddPlayList){
                AudioPlayUtil.getInstance().setAudioList(playList);
            }
            isAddPlayList = true;
            AudioMediaPlayer.stop();
            AudioPlayEntity playEntity = playList.get(0);
            playEntity.setPlay(true);
            AudioMediaPlayer.setAudio(playEntity, true);
            new AudioPresenter(null).requestDetail(playEntity.getResourceId());
        }
    }

    private void playPosition(List<AudioPlayEntity> playList, String resourceId, String columnId, String bigColumnId){
        boolean resourceEquals = false;
        AudioPlayEntity playAudio = AudioMediaPlayer.getAudio();
        if(playAudio != null){
            resourceEquals = AudioPlayUtil.resourceEquals(playAudio.getResourceId(), playAudio.getColumnId(), playAudio.getBigColumnId(),
                                                        resourceId, columnId, bigColumnId);
        }
        //正在播放的资源和点击的资源相同，则播放暂停操作
        if(playAudio != null && resourceEquals){
            if(AudioMediaPlayer.isStop()){
                AudioMediaPlayer.start();
            }else{
                AudioMediaPlayer.play();
            }
            return;
        }
        AudioMediaPlayer.stop();
        for (AudioPlayEntity playEntity : playList) {
            if(playEntity.getResourceId().equals(resourceId)){
                playEntity.setPlay(true);
                AudioMediaPlayer.setAudio(playEntity, true);
                new AudioPresenter(null).requestDetail(playEntity.getResourceId());
                break;
            }
        }
    }

    @Subscribe
    public void onEventMainThread(AudioPlayEvent event) {
        switch (event.getState()){
            case AudioPlayEvent.NEXT:
            case AudioPlayEvent.LAST:
                notifyDataSetChanged(playParentPosition, -1);
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
