package com.xiaoe.network;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.xiaoe.common.app.Constants;
import com.xiaoe.common.app.XiaoeApplication;
import com.xiaoe.network.requests.IRequest;
import com.xiaoe.network.utils.ThreadPoolUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.ArrayList;
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
    //测试环境url http://demo.h5.inside.xiaoe-tech.com/openapp/login
    private final static String TEST_API_THIRD_URL = "http://app-server.inside.xiaoeknow.com/third/xiaoe_request/";
    //正式环境url http://api.inside.xiaoe-tech.com
    private final static String FORMAL_API_THIRD_BASE_URL = "http://app-server.inside.xiaoeknow.com/third/xiaoe_request/";
//    public final static String BASE_URL = "http://134.175.39.17:9380/";
    public final static String API_THIRD_BASE_URL = XiaoeApplication.isFormalCondition() ? FORMAL_API_THIRD_BASE_URL : TEST_API_THIRD_URL;
//    public final static String CLASS_DETAIL_BASE_URL = "http://134.175.39.17:9378/api/";
//    public final static String COMMENT_BASE_URL = "http://134.175.39.17:9379/api/";
//    public final static String COLLECTION_BASE_URL = "http://134.175.39.17:9381/api/"; // 收藏接口 url
//    public final static String PLY_BASE_URL = "http://134.175.39.247:4586/";//支付订单
    public final static String LOGIN_BASE_URL = "http://app-server.inside.xiaoeknow.com/api/"; // 登录接口 url
    public final static String SCHOLARSHIP_BASE_URL = "http://134.175.39.247:14585/api/"; // 奖学金 url
    public final static String EARNING_BASE_URL = "http://134.175.39.247:4586/api/"; // 赚钱 url
    /**
     * 用户登录及绑定注册（极光推送）
     * http://app-server.inside.xiaoeknow.com/api/xe.set.device.token/1.0.0
     */
    public static final String BIND_JG_PUSH_URL = "http://app-server.inside.xiaoeknow.com/api/";
    /**
     * 获取 我的-历史消息列表
     */
    public static final String GET_HISTORY_MESSAGE_URL = "http://134.175.39.17:16688/api/";

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

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
    }

    public void sendRequest(final IRequest iRequest) {
//        CommonUserInfo userInfo = StatusKeepUtil.getUserInfo();
//        if (userInfo == null) {
//            if (!(iRequest instanceof LoginRequest)) {
//                iRequest.onResponse(false, "not login");
//                return;
//            }
//        }
        ThreadPoolUtils.runTaskOnThread(new Runnable() {
            @Override
            public void run() {
                POST(iRequest.getCmd(), iRequest);
            }
        });
    }

    public void sendRequestByGet(final IRequest iRequest) {
        ThreadPoolUtils.runTaskOnThread(new Runnable() {
            @Override
            public void run() {
                GET(iRequest.getCmd(), iRequest);
            }
        });
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

        build.addHeader("app-id", Constants.getWXAppId());
//        Map<String, String> header = iRequest.getHeader();
//        if(header != null && header.size() > 0){
//            for(Map.Entry<String ,String> entry : iRequest.getHeader().entrySet() ){
//                build.addHeader(entry.getKey(),entry.getValue());
//            }
//        }

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
                //登录请求，后续写登录逻辑可放开注释
//                if (mRequest instanceof LoginRequest) {
//                    StatusKeepUtil.clearUserInfo();
//                }
                try {
                    String jsonString = body.string();
                    com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(jsonString);
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

    private Object analyticalJSON(String strJSON, Gson gson, Class clazz) throws JSONException {
        Object json = new JSONTokener(strJSON).nextValue();
        //json是jsonObject对象
        if (json instanceof JSONObject) {
            Object objectJSON = null;
            try {
                objectJSON = gson.fromJson(strJSON, clazz);
            } catch (JsonSyntaxException e) {
                Log.d(TAG, "analyticalJSON: "+e.toString());
                e.printStackTrace();
            }
            return objectJSON;
        } //否则 json是josnArray对象(json instanceof JSONArray)
        JsonParser parser = new JsonParser();
        JsonArray jsonArray = parser.parse(strJSON).getAsJsonArray();
        ArrayList<Object> arrayList = new ArrayList<Object>();

        for (JsonElement jsonElement : jsonArray) {
            Object obj = gson.fromJson(jsonElement, clazz);
            arrayList.add(obj);
        }
        return arrayList;
    }
}