package com.xiaoe.network.requests;

import com.xiaoe.network.NetworkEngine;
import com.xiaoe.network.network_interface.IBizCallback;

public class CouponCanResourceRequest extends IRequest {

    public CouponCanResourceRequest(IBizCallback iBizCallback) {
        super(NetworkEngine.API_THIRD_BASE_URL + "xe.coupon.info/1.0.0", iBizCallback);
    }
}
