package com.xiaoe.shop.wxb.common.pay.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.xiaoe.common.app.Constants;
import com.xiaoe.network.NetworkEngine;
import com.xiaoe.network.network_interface.IBizCallback;
import com.xiaoe.network.network_interface.INetworkResponse;
import com.xiaoe.network.requests.IRequest;
import com.xiaoe.network.requests.PayOrderRequest;
import com.xiaoe.network.requests.PaySuperVipRequest;

public class PayPresenter implements IBizCallback {
    private static final String TAG = "PayPresenter";
    private INetworkResponse iNetworkResponse;
    private IWXAPI wxapi;
    public PayPresenter(Context context, INetworkResponse iNetworkResponse) {
        this.iNetworkResponse = iNetworkResponse;
        wxapi = WXAPIFactory.createWXAPI(context, Constants.getWXAppId(),true);
        wxapi.registerApp(Constants.getWXAppId());
    }

    @Override
    public void onResponse(final IRequest iRequest, final boolean success, final Object entity) {
        iNetworkResponse.onResponse(iRequest, success, entity);
    }

    /**
     * 下单支付
     * @param paymentType
     * @param resourceType
     * @param resourceId
     * @param productId
     */
    public void payOrder(int paymentType, int resourceType, String resourceId, String productId, String couponId){
        PayOrderRequest payOrderRequest = new PayOrderRequest(this);
        payOrderRequest.addBUZDataParam("user_account_type", "0");
        payOrderRequest.addBUZDataParam("force_collection", "0");
        payOrderRequest.addBUZDataParam("payment_type", ""+paymentType);
        payOrderRequest.addBUZDataParam("resource_type", ""+resourceType);
        payOrderRequest.addBUZDataParam("resource_id", ""+resourceId);
        payOrderRequest.addBUZDataParam("product_id", ""+productId);
        if(!TextUtils.isEmpty(couponId)){
            payOrderRequest.addBUZDataParam("cu_id", couponId);
        }
        payOrderRequest.sendRequest();
    }

    /**
     * 呼起微信支付
     */
    public void pullWXPay(String appId, String partnerId, String prepayId, String noncestr, String timestamp, String wxPay, String sign){
        if(TextUtils.isEmpty(wxPay)){
            wxPay = "Sign=WXPay";
        }
        PayReq req = new PayReq();
        //req.appId = "wxf8b4f85f3a794e77";  // 测试用appId
        req.appId			= appId;
        req.partnerId		= partnerId;
        req.prepayId		= prepayId;
        req.nonceStr		= noncestr;
        req.timeStamp		= timestamp;
        req.packageValue	= wxPay;
        req.sign			= sign;
        wxapi.sendReq(req);
    }

    /**
     * 购买超级会员
     */
    public void paySuperVip() {
        PaySuperVipRequest paySuperVipRequest = new PaySuperVipRequest(this);

        paySuperVipRequest.sendRequest();
    }
}
