package com.xiaoe.network.requests;

import com.xiaoe.network.NetworkEngine;
import com.xiaoe.network.network_interface.IBizCallback;

public class ColumnListRequst extends IRequest {
    //拉取专栏的资源列表
    private static final String TAG = "ColumnListRequst";

    public ColumnListRequst(IBizCallback iBizCallback) {
        super(NetworkEngine.API_THIRD_BASE_URL+"xe.goods.relation.get/1.0.0", iBizCallback);
    }

    public void sendRequest(){
        NetworkEngine.getInstance().sendRequest(this);
    }
}