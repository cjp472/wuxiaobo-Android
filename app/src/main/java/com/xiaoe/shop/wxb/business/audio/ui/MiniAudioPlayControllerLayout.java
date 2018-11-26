package com.xiaoe.shop.wxb.business.audio.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoe.common.app.XiaoeApplication;
import com.xiaoe.common.db.SQLiteUtil;
import com.xiaoe.common.entitys.AudioPlayTable;
import com.xiaoe.common.utils.Dp2Px2SpUtil;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.anim.TranslationAnimator;
import com.xiaoe.shop.wxb.business.audio.presenter.AudioMediaPlayer;
import com.xiaoe.shop.wxb.business.audio.presenter.AudioPlayUtil;
import com.xiaoe.shop.wxb.business.audio.presenter.AudioSQLiteUtil;
import com.xiaoe.shop.wxb.events.AudioPlayEvent;
import com.xiaoe.shop.wxb.widget.TasksCompletedView;

public class MiniAudioPlayControllerLayout extends FrameLayout implements View.OnClickListener {
    private static final String TAG = "MiniAudioPlayController";
    private Context mContext;
    private View rootView;
    private TasksCompletedView audioPlayProgress;
    private ImageView btnClose;
    private RelativeLayout btnPlay;
    private ImageView playStateIcon;
    private TextView title;
    private TextView columnTitle;
    private boolean isClose = false;
    private int miniPlayerAnimHeight;
    private TranslationAnimator translationAnimator;

    public MiniAudioPlayControllerLayout(@NonNull Context context) {
        this(context,null);
    }

    public MiniAudioPlayControllerLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(mContext);
    }

    private void initView(Context context) {
        rootView = View.inflate(context, R.layout.layout_mini_audio_play_controller, this);
        audioPlayProgress = (TasksCompletedView) rootView.findViewById(R.id.id_audio_play_progress);
        audioPlayProgress.setMaxProgress(0);
        audioPlayProgress.setProgress(0);
        btnClose = (ImageView) rootView.findViewById(R.id.id_audio_mini_close);
        if(AudioMediaPlayer.isPlaying()){
            btnClose.setVisibility(View.GONE);
        }else{
            btnClose.setVisibility(View.VISIBLE);
        }
        btnClose.setOnClickListener(this);
        btnPlay = (RelativeLayout) rootView.findViewById(R.id.id_btn_play_state);
        btnPlay.setOnClickListener(this);
        playStateIcon = (ImageView) rootView.findViewById(R.id.id_img_state);
        title = (TextView) rootView.findViewById(R.id.id_audio_title);
        columnTitle = (TextView) rootView.findViewById(R.id.id_column_title);
        translationAnimator = new TranslationAnimator();
    }

    public void setAudioTitle(String text){
        title.setText(text);
    }

    public void setColumnTitle(String text){
        if (!TextUtils.isEmpty(text)){
            columnTitle.setText(String.format("——《%s》",text));
            title.setPadding(0,0,0,0);
        }else{
            columnTitle.setText("");
            //若没有所属专栏，把标题上下居中显示
            title.setPadding(0,Dp2Px2SpUtil.dp2px(mContext,5),0,0);
        }

    }

    public void setPlayState(int state){
        isClose = false;
        if(state == AudioPlayEvent.PLAY){
            playStateIcon.setImageResource(R.mipmap.audiolist_stop);
            btnClose.setVisibility(View.GONE);
        }else{
            playStateIcon.setImageResource(R.mipmap.audiolist_play);
            btnClose.setVisibility(View.VISIBLE);
        }

    }

    public void setMaxProgress(int maxProgress){
        audioPlayProgress.setMaxProgress(maxProgress);
    }

    public void setProgress(int progress){
        isClose = false;
        audioPlayProgress.setProgress(progress);
    }

    public boolean isClose() {
        return isClose;
    }

    public void setIsClose(boolean close) {
        isClose = close;
        AudioPlayUtil.getInstance().setCloseMiniPlayer(false);
    }

    public void close(){
        if(miniPlayerAnimHeight <= 0){
            return;
        }
        isClose = true;
        AudioPlayUtil.getInstance().setCloseMiniPlayer(true);
        translationAnimator.setAnimator(this)
                .remove(miniPlayerAnimHeight);
        SQLiteUtil audioSQLiteUtil = SQLiteUtil.init(XiaoeApplication.getmContext(), new AudioSQLiteUtil());
        String sql = "UPDATE "+AudioPlayTable.TABLE_NAME +" SET current_play_state = 0 where current_play_state = 1";
        audioSQLiteUtil.execSQL(sql);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.id_btn_play_state){
            if(!AudioMediaPlayer.isStop()){
                AudioMediaPlayer.play();
            }else{
                AudioMediaPlayer.start();
            }
        }else if(v.getId() == R.id.id_audio_mini_close){
            close();
        }
    }

    public void setMiniPlayerAnimHeight(int height) {
        miniPlayerAnimHeight = height;
    }

    public void setPlayButtonEnabled(boolean enabled){
        btnPlay.setEnabled(enabled);
    }
}
