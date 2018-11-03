package xiaoe.com.shop.business.search.presenter;

import xiaoe.com.common.app.Constants;
import xiaoe.com.network.NetworkEngine;
import xiaoe.com.network.network_interface.IBizCallback;
import xiaoe.com.network.network_interface.INetworkResponse;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.network.requests.SearchRequest;

public class SearchPresenter implements IBizCallback {

    private INetworkResponse inr;
    private String cmd;

    public SearchPresenter(INetworkResponse inr) {
        this.inr = inr;
        this.cmd = "api/xe.shop.search/1.0.0"; // 默认接口
    }

    public SearchPresenter(INetworkResponse inr, String cmd) {
        this.inr = inr;
        this.cmd = cmd;
    }

    @Override
    public void onResponse(IRequest iRequest, boolean success, Object entity) {
        inr.onResponse(iRequest, success, entity);
    }

    /**
     * 请求搜索结果（默认请求第一页，十条）
     *
     * @param keyWord 搜索关键词
     */
    public void requestSearchResult(String keyWord) {
        SearchRequest searchRequest = new SearchRequest(NetworkEngine.BASE_URL + cmd, this);
        searchRequest.addRequestParam("shop_id", Constants.getAppId());
        searchRequest.addRequestParam("user_id", "u_591d643ce9c2c_fAbTq44T");
        searchRequest.addDataParam("keyword", keyWord);
        searchRequest.addDataParam("page", 1);
        searchRequest.addDataParam("page_size", 10);
        NetworkEngine.getInstance().sendRequest(searchRequest);
    }

    /**
     * 指定页码、页面大小请求搜索结果
     */
    public void requestSearchResultByPage(String keyWord, int pageIndex, int pageSize) {
        SearchRequest searchRequest = new SearchRequest(NetworkEngine.BASE_URL + cmd, this);
        searchRequest.addRequestParam("shop_id", Constants.getAppId());
        searchRequest.addRequestParam("user_id", "u_591d643ce9c2c_fAbTq44T");
        searchRequest.addDataParam("keyword", keyWord);
        searchRequest.addDataParam("page", pageIndex);
        searchRequest.addDataParam("page_size", pageSize);
        NetworkEngine.getInstance().sendRequest(searchRequest);
    }
}
