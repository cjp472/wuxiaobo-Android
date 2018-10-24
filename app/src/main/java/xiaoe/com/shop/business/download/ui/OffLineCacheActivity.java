package xiaoe.com.shop.business.download.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import xiaoe.com.shop.R;
import xiaoe.com.shop.adapter.download.CacheViewPagerAdapter;
import xiaoe.com.shop.base.XiaoeActivity;
import xiaoe.com.shop.widget.ScrollViewPager;

public class OffLineCacheActivity extends XiaoeActivity implements View.OnClickListener {
    private static final String TAG = "OffLineCacheActivity";
    private RelativeLayout btnFinish;
    private TextView finishTitle;
    private View finishSelect;
    private RelativeLayout btnDownloadProceed;
    private TextView downloadProceedTitle;
    private View downloadProceedSelect;
    private ScrollViewPager tabViewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_off_line_cache);
        initViews();
    }

    private void initViews() {
        //已完成按钮
        btnFinish = (RelativeLayout) findViewById(R.id.btn_finish);
        btnFinish.setOnClickListener(this);
        finishTitle = (TextView) findViewById(R.id.finish_title);
        finishSelect = findViewById(R.id.finish_select);
        //下载中按钮
        btnDownloadProceed = (RelativeLayout) findViewById(R.id.btn_download_proceed);
        btnDownloadProceed.setOnClickListener(this);
        downloadProceedTitle = (TextView) findViewById(R.id.download_proceed_title);
        downloadProceedSelect = findViewById(R.id.download_proceed_select);
        setTabSelect(0);

        //tab切换页
        tabViewPager = (ScrollViewPager) findViewById(R.id.tab_view_page);
        tabViewPager.setScroll(false);
        tabViewPager.setAdapter(new CacheViewPagerAdapter(getSupportFragmentManager()));
    }

    private void setTabSelect(int type){
        boolean finish = type == 0;
        btnFinish.setEnabled(!finish);
        finishSelect.setEnabled(!finish);
        finishTitle.setEnabled(!finish);

        btnDownloadProceed.setEnabled(finish);
        downloadProceedSelect.setEnabled(finish);
        downloadProceedTitle.setEnabled(finish);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_finish:
                setTabSelect(0);
                tabViewPager.setCurrentItem(0);
                break;
            case R.id.btn_download_proceed:
                setTabSelect(1);
                tabViewPager.setCurrentItem(1);
                break;
            default:
                break;
        }
    }
}