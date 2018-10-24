package xiaoe.com.shop.business.coupon.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import xiaoe.com.shop.R;
import xiaoe.com.shop.adapter.coupon.CouponListAdapter;
import xiaoe.com.shop.base.BaseFragment;

public class CouponFragment extends BaseFragment {

    private static final String TAG = "CouponFragment";

    private Unbinder unbinder;
    private Context mContext;
    private boolean destroyView = false;
    protected View viewWrap;

    private int layoutId = -1;
    CouponActivity couponActivity;
    private RecyclerView couponRecyclerView;

    public static CouponFragment newInstance(int layoutId) {
        CouponFragment couponFragment = new CouponFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("layoutId", layoutId);
        couponFragment.setArguments(bundle);
        return couponFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            layoutId = bundle.getInt("layoutId");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewWrap = inflater.inflate(layoutId, null, false);
        unbinder = ButterKnife.bind(this, viewWrap);
        mContext = getContext();
        couponActivity = (CouponActivity) getActivity();
        return viewWrap;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (layoutId != -1) {
            initView();
        }
    }

    private void initView() {
        couponRecyclerView = (RecyclerView) viewWrap.findViewById(R.id.coupon_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setAutoMeasureEnabled(true);
        couponRecyclerView.setLayoutManager(layoutManager);
        CouponListAdapter couponListAdapter = new CouponListAdapter(mContext);
        couponRecyclerView.setAdapter(couponListAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        destroyView = true;
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}