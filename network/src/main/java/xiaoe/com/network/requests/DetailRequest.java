package xiaoe.com.network.requests;

import xiaoe.com.network.NetworkEngine;
import xiaoe.com.network.network_interface.IBizCallback;

public class DetailRequest extends IRequest {
    /**
     * 购买前详情接口
     */
    public DetailRequest(IBizCallback iBizCallback) {
        super(NetworkEngine.API_THIRD_BASE_URL +"xe.goods.detail.get/1.0.0", iBizCallback);
    }
    public void sendRequest(){
        NetworkEngine.getInstance().sendRequest(this);
    }
}
