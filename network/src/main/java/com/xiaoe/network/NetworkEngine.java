package com.xiaoe.network;

import android.text.TextUtils;
import android.util.Log;

import com.xiaoe.common.app.CommonUserInfo;
import com.xiaoe.common.app.Constants;
import com.xiaoe.common.app.XiaoeApplication;
import com.xiaoe.common.db.SQLiteUtil;
import com.xiaoe.common.entitys.CacheData;
import com.xiaoe.common.utils.CacheDataUtil;
import com.xiaoe.common.utils.NetUtils;
import com.xiaoe.network.requests.ColumnListRequst;
import com.xiaoe.network.requests.IRequest;
import com.xiaoe.network.utils.ThreadPoolUtils;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2018/9/26.
 */
public class NetworkEngine {

    private static final String TAG = "NetworkEngine";
    public final static String BASE_URL_FORMAL = "https://app-server.xiaoeknow.com";
    public final static String BASE_URL_TEST = "http://app-server.inside.xiaoeknow.com";
    //测试环境url http://demo.h5.inside.xiaoe-tech.com/openapp/login
//    private final static String TEST_API_THIRD_URL = "http://app-server.inside.xiaoeknow.com/third/xiaoe_request/";
    //正式环境url http://api.inside.xiaoe-tech.com
//    private final static String FORMAL_API_THIRD_BASE_URL = BASE_URL+"/third/xiaoe_request/";
    public final static String API_THIRD_BASE_URL = (XiaoeApplication.isFormalCondition() ? BASE_URL_FORMAL : BASE_URL_TEST)+"/third/xiaoe_request/";
    public final static String LOGIN_BASE_URL = (XiaoeApplication.isFormalCondition() ? BASE_URL_FORMAL : BASE_URL_TEST)+"/api/"; // 登录接口 url

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final SQLiteUtil cacheSQLiteUtil;

    public enum HttpMethod {
        /**
         * POST 请求方式
         */
        HTTP_POST(1),
        /**
         * GET 请求方式
         */
        HTTP_GET(2);

        HttpMethod(int value) {
            this.value = value;
        }

        public int value;
    }

    private OkHttpClient client;
    private static NetworkEngine networkEngine;

    public static NetworkEngine getInstance() {
        if (networkEngine == null) {
            synchronized (NetworkEngine.class) {
                if (networkEngine == null) {
                    networkEngine = new NetworkEngine();
                }
            }
        }
        return networkEngine;
    }

    private NetworkEngine() {
        client = new OkHttpClient.Builder()
                .connectTimeout(8, TimeUnit.SECONDS)
                .writeTimeout(8, TimeUnit.SECONDS)
                .build();
        cacheSQLiteUtil = SQLiteUtil.init(XiaoeApplication.getmContext(), new CacheDataUtil());
    }

    public void sendRequest(final IRequest iRequest) {
//        CommonUserInfo userInfo = StatusKeepUtil.getUserInfo();
//        if (userInfo == null) {
//            if (!(iRequest instanceof LoginRequest)) {
//                iRequest.onResponse(false, "not login");
//                return;
//            }
//        }
        if (NetUtils.hasNetwork(XiaoeApplication.applicationContext) && NetUtils.hasDataConnection(XiaoeApplication.applicationContext)) {
            ThreadPoolUtils.runTaskOnThread(new Runnable() {
                @Override
                public void run() {
                    POST(iRequest.getCmd(), iRequest);
                }
            });
        } else {
            String jsonString = com.alibaba.fastjson.JSON.toJSONString(new NetworkStateResult());
            iRequest.onResponse(false, com.alibaba.fastjson.JSONObject.parseObject(jsonString));
            Log.d(TAG, "sendRequest: ERROR_NETWORK");
        }
    }

    public void sendRequestByGet(final IRequest iRequest) {
        if (NetUtils.hasNetwork(XiaoeApplication.applicationContext) && NetUtils.hasDataConnection(XiaoeApplication.applicationContext)) {
            ThreadPoolUtils.runTaskOnThread(new Runnable() {
                @Override
                public void run() {
                    GET(iRequest.getCmd(), iRequest);
                }
            });
        } else {
            String jsonString = com.alibaba.fastjson.JSON.toJSONString(new NetworkStateResult());
            iRequest.onResponse(false, com.alibaba.fastjson.JSONObject.parseObject(jsonString));
            Log.e(TAG, "sendRequestByGet: ERROR_NETWORK");
        }
    }

    private void POST(String url, IRequest iRequest) {
        request(HttpMethod.HTTP_POST,url,iRequest);
    }

    private void GET(String url, IRequest iRequest) {
        request(HttpMethod.HTTP_GET,url,iRequest);
    }

    private void request(HttpMethod httpMethod,String url, IRequest iRequest) {
        String formBodyString = iRequest.getWrapedFormBody();
        if (formBodyString == null || TextUtils.isEmpty(formBodyString)) {
            iRequest.onResponse(false, "param error");
            return;
        }
        RequestBody formBody = RequestBody.create(JSON, formBodyString);
        Request.Builder build = null;
        if (httpMethod == HttpMethod.HTTP_POST){
            build = new Request.Builder()
                    .url(url)
                    .post(formBody)
                    .tag(iRequest);
            Log.d(TAG, "request url - " + url + "\n" + formBodyString);
        }else if (httpMethod == HttpMethod.HTTP_GET){
            build = new Request.Builder()
                    .url(iRequest.getUrlWithParams(url))
                    .tag(iRequest)
                    .get();
            Log.d(TAG, "request url - " + iRequest.getUrlWithParams(url));
        }
        if (build == null) {
            iRequest.onResponse(false, "httpMethod error");
            return;
        }

        Request request = build.build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                IRequest mRequest = (IRequest) call.request().tag();
                mRequest.onResponse(false, null);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                IRequest mRequest = (IRequest) (call.request().tag());
                ResponseBody body = response.body();
                if (body == null) {
                    mRequest.onResponse(false, null);
                    return;
                }
                try {
                    String jsonString = body.string();
                    com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(jsonString);
                    if(mRequest.isNeedCache() && !TextUtils.isEmpty(mRequest.getCacheKey()) && jsonObject.getIntValue("code") == NetworkCodes.CODE_SUCCEED){
                        if(mRequest instanceof ColumnListRequst){
                            saveCacheData(mRequest.getCacheKey(), null, jsonString);
                        }else{
                            saveCacheData(mRequest.getCacheKey(), jsonString, null);
                        }
                    }
                    body.close();
                    mRequest.onResponse(true, jsonObject);
                    Log.d(TAG, "request result - " + response.code() + " - " + jsonObject.toJSONString());
                } catch (Exception e) {
                    mRequest.onResponse(false, null);
                    Log.d(TAG, "request result - " + response.code() + " - " + e.getMessage());
                }
            }
        });
    }

    public void saveCacheData(String cacheKey, String data, String dataList){
        if(!cacheSQLiteUtil.tabIsExist(CacheDataUtil.TABLE_NAME)){
            cacheSQLiteUtil.execSQL(CacheDataUtil.CREATE_TABLES_SQL);
        }
        String sql = "select * from "+CacheDataUtil.TABLE_NAME+" where app_id='"+Constants.getAppId()
                +"' and resource_id='"+cacheKey+"' and user_id='"+CommonUserInfo.getLoginUserIdOrAnonymousUserId()+"'";
        List<CacheData> cacheDataList = cacheSQLiteUtil.query(CacheDataUtil.TABLE_NAME, sql, null);
        if(cacheDataList == null || cacheDataList.size() <= 0){
            CacheData cacheData = new CacheData();
            cacheData.setAppId(Constants.getAppId());
            cacheData.setResourceId(cacheKey);
            cacheData.setContent(data);
            cacheData.setResourceList(dataList);
            cacheSQLiteUtil.insert(CacheDataUtil.TABLE_NAME, cacheData);
        }else{
            CacheData cacheData = cacheDataList.get(0);
            if(!TextUtils.isEmpty(data)){
                cacheData.setContent(data);
            }
            if(!TextUtils.isEmpty(dataList)){
                cacheData.setResourceList(dataList);
            }
            String whereVal = "app_id=? and resource_id=? and user_id=?";
            cacheSQLiteUtil.update(CacheDataUtil.TABLE_NAME, cacheData, whereVal, new String[]{Constants.getAppId(), cacheKey, CommonUserInfo.getLoginUserIdOrAnonymousUserId()});
        }
    }

}
