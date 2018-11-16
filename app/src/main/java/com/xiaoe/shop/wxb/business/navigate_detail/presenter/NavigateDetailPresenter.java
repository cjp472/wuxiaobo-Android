package com.xiaoe.shop.wxb.business.navigate_detail.presenter;

import com.xiaoe.common.app.CommonUserInfo;
import com.xiaoe.network.NetworkEngine;
import com.xiaoe.network.network_interface.IBizCallback;
import com.xiaoe.network.network_interface.INetworkResponse;
import com.xiaoe.network.requests.CommodityGroupRequest;
import com.xiaoe.network.requests.IRequest;

public class NavigateDetailPresenter implements IBizCallback {

    private INetworkResponse inr;
    String cmd;

    public NavigateDetailPresenter(INetworkResponse inr) {
        this.inr = inr;
        this.cmd = "xe.goods.more.get/1.0.0";
    }

    public NavigateDetailPresenter(INetworkResponse inr, String cmd) {
        this.inr = inr;
        this.cmd = cmd;
    }

    @Override
    public void onResponse(IRequest iRequest, boolean success, Object entity) {
        inr.onResponse(iRequest, success, entity);
    }

    public void requestData(String groupId) {
        CommodityGroupRequest commodityGroupRequest = new CommodityGroupRequest( null, this);
        commodityGroupRequest.addRequestParam("user_id", CommonUserInfo.getUserId());
        commodityGroupRequest.addRequestParam("shop_id", CommonUserInfo.getShopId());
        commodityGroupRequest.addRequestParam("app_version", "0.1");
        commodityGroupRequest.addRequestParam("client", "1");
        commodityGroupRequest.addRequestParam("is_manager", 0);
        commodityGroupRequest.addRequestParam("agent_type", 1);
        commodityGroupRequest.addRequestParam("agent_version", "1");
        commodityGroupRequest.addBUZDataParam("id", groupId);
        NetworkEngine.getInstance().sendRequest(commodityGroupRequest);
    }
}