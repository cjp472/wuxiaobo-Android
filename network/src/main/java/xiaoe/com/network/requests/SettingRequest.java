package xiaoe.com.network.requests;

import xiaoe.com.network.network_interface.IBizCallback;

public class SettingRequest extends IRequest {

    public SettingRequest(String cmd, Class entityClass, IBizCallback iBizCallback) {
        super(cmd, entityClass, iBizCallback);
    }
}
