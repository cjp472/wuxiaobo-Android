package xiaoe.com.network.requests;

import xiaoe.com.network.network_interface.IBizCallback;

public class SearchRequest extends IRequest {

    public SearchRequest(String cmd, Class entityClass, IBizCallback iBizCallback) {
        super(cmd, entityClass, iBizCallback);
    }
}
