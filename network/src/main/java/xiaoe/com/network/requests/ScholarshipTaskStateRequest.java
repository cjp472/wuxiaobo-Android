package xiaoe.com.network.requests;

import xiaoe.com.network.NetworkEngine;
import xiaoe.com.network.network_interface.IBizCallback;

// 任务状态请求
public class ScholarshipTaskStateRequest extends IRequest {

    public ScholarshipTaskStateRequest(IBizCallback iBizCallback) {
//        super(NetworkEngine.API_THIRD_BASE_URL + "get_task_status", iBizCallback);
        super(NetworkEngine.API_THIRD_BASE_URL + "xe.get.task.status/1.0.0", iBizCallback);
    }
}
