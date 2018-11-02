package xiaoe.com.shop.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.pnikosis.materialishprogress.ProgressWheel;

import xiaoe.com.shop.R;

public class StatusPagerView extends FrameLayout {
    private static final String TAG = "LoadingView";
    private View rootView;
    private ProgressWheel loadingState;
    private TextView stateText;
    private ImageView stateImage;

    public static final int DETAIL_NONE = R.mipmap.detail_none;
    private TextView btnGoTo;

    public StatusPagerView(@NonNull Context context) {
        this(context,null);
    }

    public StatusPagerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        rootView = LayoutInflater.from(context).inflate(R.layout.layout_status_pager, this, true);
        loadingState = (ProgressWheel) rootView.findViewById(R.id.loading_state);
        setLoadingState(View.GONE);
        stateText = (TextView) rootView.findViewById(R.id.state_text);
        stateImage = (ImageView) rootView.findViewById(R.id.state_image);
        setHintStateVisibility(View.GONE);
        btnGoTo = (TextView) rootView.findViewById(R.id.btn_go_to);

        rootView.findViewById(R.id.pager).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void setLoadingState(int visibility){
        loadingState.setVisibility(visibility);
    }

    public void setHintStateVisibility(int visibility){
        stateImage.setVisibility(visibility);
        stateText.setVisibility(visibility);
    }
    public void setStateImage(int resId){
        stateImage.setImageResource(resId);
    }

    public void setStateText(String text){
        stateText.setText(text);
    }

    public void setBtnGoToText(String text){
        btnGoTo.setText(text);
        if(TextUtils.isEmpty(text)){
            btnGoTo.setVisibility(GONE);
        }else{
            btnGoTo.setVisibility(VISIBLE);
        }
    }

    public void setBtnGoToOnClickListener(OnClickListener listener){
        btnGoTo.setOnClickListener(listener);
    }
}
