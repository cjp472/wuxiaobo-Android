package xiaoe.com.shop.business.setting.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import xiaoe.com.common.entitys.SettingItemInfo;
import xiaoe.com.common.utils.CacheManagerUtil;
import xiaoe.com.common.utils.Dp2Px2SpUtil;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseFragment;
import xiaoe.com.shop.business.setting.presenter.LinearDividerDecoration;
import xiaoe.com.shop.business.setting.presenter.OnItemClickListener;
import xiaoe.com.shop.business.setting.presenter.SettingRecyclerAdapter;

public class MainAccountFragment extends BaseFragment implements OnItemClickListener {

    private static final String TAG = "MainAccountFragment";

    private Unbinder unbinder;
    private Context mContext;
    private boolean destroyView = false;

    @BindView(R.id.setting_account_content)
    RecyclerView accountContent;
    @BindView(R.id.setting_account_btn)
    Button accountBtn;

    List<SettingItemInfo> itemInfoList;
    SettingRecyclerAdapter settingRecyclerAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_main, null, false);
        unbinder = ButterKnife.bind(this, view);
        mContext = getContext();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        initListener();
    }

    private void initData() {
        // 账号设置主页面假数据
        itemInfoList = new ArrayList<>();
        // TODO: 获取最新版本信息和缓存信息
        SettingItemInfo account = new SettingItemInfo("账号设置", "", "");
        SettingItemInfo message = new SettingItemInfo("推送消息设置", "", "");
        // 获取缓存信息
        String cacheSize = "0K";
        try {
            cacheSize = CacheManagerUtil.getTotalCacheSize(getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SettingItemInfo memory = new SettingItemInfo("消除缓存", "", cacheSize);
        SettingItemInfo version = new SettingItemInfo("版本更新", "", "已经是最新版本");
        SettingItemInfo us = new SettingItemInfo("关于我们", "", "");
        SettingItemInfo suggestion = new SettingItemInfo("意见反馈", "", "");
        itemInfoList.add(account);
        itemInfoList.add(message);
        itemInfoList.add(memory);
        itemInfoList.add(version);
        itemInfoList.add(us);
        itemInfoList.add(suggestion);

        settingRecyclerAdapter = new SettingRecyclerAdapter(getActivity(), itemInfoList);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        LinearDividerDecoration linearDividerDecoration = new LinearDividerDecoration(getActivity(), R.drawable.recycler_divider_line, Dp2Px2SpUtil.dp2px(getActivity(), 20));
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        accountContent.setLayoutManager(llm);
        settingRecyclerAdapter.setOnItemClickListener(this);
        accountContent.addItemDecoration(linearDividerDecoration);
        accountContent.setAdapter(settingRecyclerAdapter);
    }

    private void initListener() {
        accountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "退出登录", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: 发送网络请求
    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        super.onMainThreadResponse(iRequest, success, entity);
        Log.d(TAG, "onMainThreadResponse: isSuccess ---- " + success);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        destroyView = true;
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        SettingAccountActivity settingAccountActivity = (SettingAccountActivity) getActivity();
        String tag = settingAccountActivity.int2Str(position);
        settingAccountActivity.replaceFragment(tag);
    }
}