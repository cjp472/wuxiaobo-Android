package xiaoe.com.shop.business.earning.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import xiaoe.com.common.entitys.EarningItem;
import xiaoe.com.common.utils.MeasureUtil;
import xiaoe.com.network.NetworkCodes;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.network.requests.IntegralRequest;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.XiaoeActivity;
import xiaoe.com.shop.business.earning.presenter.EarningListAdapter;
import xiaoe.com.shop.business.earning.presenter.EarningPresenter;
import xiaoe.com.shop.utils.StatusBarUtil;

// 积分页面
public class IntegralActivity extends XiaoeActivity {

    private static final String TAG = "IntegralActivity";

    @BindView(R.id.integral_wrap)
    LinearLayout integralWrap;

    @BindView(R.id.title_back)
    ImageView integralBack;
    @BindView(R.id.title_content)
    TextView integralTitle;
    @BindView(R.id.title_end)
    TextView integralDesc;

    @BindView(R.id.integral_content)
    TextView integralContent;
    @BindView(R.id.integral_be_super_vip)
    TextView integralBeSuperVip;

    @BindView(R.id.earning_list_title)
    TextView integralListTitle;
    @BindView(R.id.earning_list)
    ListView integralList;
    @BindView(R.id.earning_empty_tip)
    TextView integralTip;

    Unbinder unbinder;

    boolean isSuperVip;

    EarningPresenter earningPresenter;
    List<EarningItem> dataList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
        setContentView(R.layout.activity_integral);

        unbinder = ButterKnife.bind(this);

        integralWrap.setPadding(0, StatusBarUtil.getStatusBarHeight(this), 0, 0);
        earningPresenter = new EarningPresenter(this);
        earningPresenter.requestIntegralData(1, 10);

        initData();
        initListener();
    }

    private void initData() {
        dataList = new ArrayList<>();
        integralTitle.setText("积分");
        integralDesc.setVisibility(View.GONE);
        integralTip.setText("暂无积分记录，快去做任务领取积分吧");
        integralTip.setVisibility(View.VISIBLE);
        if (isSuperVip) {
            integralBeSuperVip.setVisibility(View.VISIBLE);
        } else {
            integralBeSuperVip.setVisibility(View.GONE);
        }
        integralListTitle.setText("积分流水");
        integralContent.setText("168");
    }

    private void initListener() {
        integralBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        super.onMainThreadResponse(iRequest, success, entity);
        JSONObject result = (JSONObject) entity;
        if (success) {
            if (iRequest instanceof IntegralRequest) {
                int code = result.getInteger("code");
                if (code == NetworkCodes.CODE_SUCCEED) {
                    JSONObject data = (JSONObject) result.get("data");
                    initPageData(data);
                } else {
                    Log.d(TAG, "onMainThreadResponse: request fail");
                }
            }
        } else {
            Log.d(TAG, "onMainThreadResponse: request fail...");
        }
    }

    // 初始化页面数据
    private void initPageData(JSONObject data) {
        int integral = data.getInteger("balance");
        JSONArray flowList = (JSONArray) data.get("flow_list");
        for (Object item : flowList) {
            JSONObject jsonItem = (JSONObject) item;
            int flowInfo = jsonItem.getInteger("flow_type");
            String detailId = jsonItem.getString("detail_id");
            int count = jsonItem.getInteger("count");
            String createdAt = jsonItem.getString("created_at");
            String desc = jsonItem.getString("desc");

            EarningItem earningItem = new EarningItem();
            earningItem.setItemTitle(desc);
            earningItem.setItemContent(createdAt.split(" ")[0]);
            earningItem.setItemMoney(String.valueOf(count));

            dataList.add(earningItem);
        }
        integralContent.setText(String.valueOf(integral));
        if (dataList.size() > 0) {
            EarningListAdapter earningListAdapter = new EarningListAdapter(this, dataList);
            integralList.setAdapter(earningListAdapter);
            MeasureUtil.setListViewHeightBasedOnChildren(integralList);
            integralTip.setVisibility(View.GONE);
        } else {
            integralTip.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
