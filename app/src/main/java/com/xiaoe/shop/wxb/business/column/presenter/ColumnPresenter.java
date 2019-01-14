package com.xiaoe.shop.wxb.business.column.presenter;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.xiaoe.common.entitys.ColumnDirectoryEntity;
import com.xiaoe.common.entitys.ColumnSecondDirectoryEntity;
import com.xiaoe.common.entitys.ExpandableItem;
import com.xiaoe.common.entitys.ExpandableLevel;
import com.xiaoe.network.network_interface.IBizCallback;
import com.xiaoe.network.network_interface.INetworkResponse;
import com.xiaoe.network.requests.ColumnListRequst;
import com.xiaoe.network.requests.CourseDetailRequest;
import com.xiaoe.network.requests.DownloadListRequest;
import com.xiaoe.network.requests.IRequest;
import com.xiaoe.network.requests.QueryProductTypeRequest;
import com.xiaoe.shop.wxb.adapter.download.DownLoadExpandAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ColumnPresenter implements IBizCallback {
    private static final String TAG = "ColumnPresenter";

    private INetworkResponse iNetworkResponse;

    public ColumnPresenter(INetworkResponse iNetworkResponse) {
        this.iNetworkResponse = iNetworkResponse;
    }

    @Override
    public void onResponse(final IRequest iRequest, final boolean success, final Object entity) {
        iNetworkResponse.onResponse(iRequest, success, entity);
    }
    /**
     * 获取购买前商品详情
     */
    public void requestDetail(String resourceId, String resourceType){
        CourseDetailRequest courseDetailRequest = new CourseDetailRequest( this);
        courseDetailRequest.addDataParam("goods_id",resourceId);
        courseDetailRequest.addDataParam("goods_type",Integer.parseInt(resourceType));
        courseDetailRequest.addDataParam("agent_type",2);
        courseDetailRequest.setNeedCache(true);
        courseDetailRequest.setCacheKey(resourceId);
        courseDetailRequest.sendRequest();
    }

    public void requestColumnList(String resourceId, String resourceType, int page, int pageSize, boolean needCache, String resourceTag){
        ColumnListRequst columnListRequst = new ColumnListRequst(this);
        columnListRequst.addDataParam("goods_id",resourceId);
        columnListRequst.addDataParam("resource_type",resourceType);
        columnListRequst.addDataParam("page", ""+page);
        columnListRequst.addDataParam("page_size", ""+pageSize);
        columnListRequst.setNeedCache(needCache);
        columnListRequst.setCacheKey(resourceId+"_list");
        columnListRequst.setRequestTag(resourceTag);
        columnListRequst.sendRequest();
    }

    /**
     * 格式化一级目录数据
     * @param jsonArray
     */
    public List<ColumnDirectoryEntity> formatColumnEntity(JSONArray jsonArray, String bigColumnId, int hasBuy){
        List<ColumnDirectoryEntity> directoryEntityList = new ArrayList<ColumnDirectoryEntity>();
        for (Object object : jsonArray) {
            ColumnDirectoryEntity directoryEntity = new ColumnDirectoryEntity();
            JSONObject jsonObject = (JSONObject) object;
            directoryEntity.setApp_id(jsonObject.getString("app_id"));
            directoryEntity.setTitle(jsonObject.getString("title"));
            String columnId = jsonObject.getString("resource_id");
            directoryEntity.setImg_url(jsonObject.getString("img_url"));
            directoryEntity.setImg_url_compress(jsonObject.getString("img_url_compress"));
            directoryEntity.setResource_id(columnId);
            directoryEntity.setBigColumnId(bigColumnId);
            directoryEntity.setResource_type(jsonObject.getIntValue("resource_type"));
            List<ColumnSecondDirectoryEntity> childList = formatSingleResourceEntity(jsonObject.getJSONArray("resource_list"), directoryEntity.getTitle(), columnId , bigColumnId, hasBuy);
            directoryEntity.setResource_list(childList);
            directoryEntityList.add(directoryEntity);
        }
        return directoryEntityList;
    }


    /**
     * 格式化二级目录数据
     * @param jsonArray
     * @return
     */
    public List<ColumnSecondDirectoryEntity> formatSingleResourceEntity(JSONArray jsonArray, String columnTitle, String columnId, String bigColumnId, int hasBuy){
        List<ColumnSecondDirectoryEntity> directoryEntityList = new ArrayList<ColumnSecondDirectoryEntity>();
        for (Object object : jsonArray) {
            ColumnSecondDirectoryEntity secondDirectoryEntity = new ColumnSecondDirectoryEntity();
            JSONObject jsonObject = (JSONObject) object;
            int resourceType = jsonObject.getIntValue("resource_type");
            int[] types = new int[]{1 , 2, 3};
            if(Arrays.binarySearch(types, resourceType) < 0){
                //过滤掉非图文音视频资源
                continue;
            }
            secondDirectoryEntity.setApp_id(jsonObject.getString("app_id"));
            secondDirectoryEntity.setResource_id(jsonObject.getString("resource_id"));
            secondDirectoryEntity.setTitle(jsonObject.getString("title"));
            secondDirectoryEntity.setImg_url(jsonObject.getString("img_url"));
            secondDirectoryEntity.setImg_url_compress(jsonObject.getString("img_url_compress"));
            secondDirectoryEntity.setResource_type(jsonObject.getIntValue("resource_type"));
            secondDirectoryEntity.setAudio_length(jsonObject.getIntValue("audio_length"));
            secondDirectoryEntity.setVideo_length(jsonObject.getIntValue("video_length"));
            secondDirectoryEntity.setAudio_url(jsonObject.getString("audio_url"));
            secondDirectoryEntity.setVideo_url(jsonObject.getString("video_url"));
            secondDirectoryEntity.setColumnTitle(columnTitle);
            secondDirectoryEntity.setColumnId(columnId);
            secondDirectoryEntity.setBigColumnId(bigColumnId);
            secondDirectoryEntity.setIsTry(jsonObject.getIntValue("is_try"));
            secondDirectoryEntity.setIsHasBuy(hasBuy);

            directoryEntityList.add(secondDirectoryEntity);
        }
        return directoryEntityList;
    }

    /**
     * 格式化二级目录数据
     * @param jsonArray
     * @return
     */
    public List<ColumnSecondDirectoryEntity> formatSingleResourceEntity2(JSONArray jsonArray, String columnTitle, String columnId, String bigColumnId, int hasBuy){
        List<ColumnSecondDirectoryEntity> directoryEntityList = new ArrayList<ColumnSecondDirectoryEntity>();
        if (jsonArray == null)  return directoryEntityList;
        for (Object object : jsonArray) {
            ColumnSecondDirectoryEntity secondDirectoryEntity = new ColumnSecondDirectoryEntity();
            JSONObject jsonObject = (JSONObject) object;
            int resourceType = jsonObject.getIntValue("goods_type");
            int[] types = new int[]{1 , 2, 3};
            if(Arrays.binarySearch(types, resourceType) < 0){
                //过滤掉非图文音视频资源
                continue;
            }
            secondDirectoryEntity.setApp_id(jsonObject.getString("app_id"));
            secondDirectoryEntity.setResource_id(jsonObject.getString("goods_id"));
            secondDirectoryEntity.setTitle(jsonObject.getString("title"));
            secondDirectoryEntity.setImg_url(jsonObject.getString("img_url"));
            secondDirectoryEntity.setImg_url_compress(jsonObject.getString("img_url_compress"));
            secondDirectoryEntity.setResource_type(jsonObject.getIntValue("goods_type"));
            secondDirectoryEntity.setAudio_length(jsonObject.getIntValue("length"));
//            secondDirectoryEntity.setVideo_length(jsonObject.getIntValue("video_length"));
            secondDirectoryEntity.setAudio_url(jsonObject.getString("download_url"));
//            secondDirectoryEntity.setVideo_url(jsonObject.getString("download_url"));
            secondDirectoryEntity.setColumnTitle(columnTitle);
            secondDirectoryEntity.setColumnId(columnId);
            secondDirectoryEntity.setBigColumnId(bigColumnId);
//            secondDirectoryEntity.setIsTry(jsonObject.getIntValue("is_try"));
            secondDirectoryEntity.setIsHasBuy(hasBuy);

            directoryEntityList.add(secondDirectoryEntity);
        }
        return directoryEntityList;
    }

    /**
     * 格式化一级目录数据
     * @param jsonArray
     */
    public List<MultiItemEntity> formatExpandableEntity(JSONArray jsonArray, String bigColumnId, int hasBuy){
        List<MultiItemEntity> directoryEntityList = new ArrayList<MultiItemEntity>();
        for (Object object : jsonArray) {
            ExpandableLevel directoryEntity = new ExpandableLevel();

            JSONObject jsonObject = (JSONObject) object;
            directoryEntity.setApp_id(jsonObject.getString("app_id"));
            directoryEntity.setTitle(jsonObject.getString("title"));
            String columnId = jsonObject.getString("resource_id");
            directoryEntity.setImg_url(jsonObject.getString("img_url"));
            directoryEntity.setImg_url_compress(jsonObject.getString("img_url_compress"));
            directoryEntity.setResource_id(columnId);
            directoryEntity.setBigColumnId(bigColumnId);
            directoryEntity.setResource_type(jsonObject.getIntValue("resource_type"));

//            List<MultiItemEntity> MultiItemEntitychildList = formatExpandableChildEntity(jsonObject.getJSONArray("resource_list"), directoryEntity.getTitle(), columnId , bigColumnId, hasBuy, directoryEntity);
//            List<MultiItemEntity> childList = new ArrayList<MultiItemEntity>();
            //添加一条空数据，作为“加载状态”条目
            ExpandableItem loadSecondDirectoryEntity = new ExpandableItem();
            loadSecondDirectoryEntity.setItemType(3);
            loadSecondDirectoryEntity.setLoadType(1);
//            childList.add(loadSecondDirectoryEntity);
            directoryEntity.addSubItem(loadSecondDirectoryEntity);
            //添加一条空数据，作为“收起”条目
            ExpandableItem emptySecondDirectoryEntity = new ExpandableItem();
            emptySecondDirectoryEntity.setItemType(2);
//            childList.add(emptySecondDirectoryEntity);
            directoryEntity.addSubItem(emptySecondDirectoryEntity);

//            directoryEntity.setResource_list(childList);
            directoryEntityList.add(directoryEntity);
        }
        return directoryEntityList;
    }
    /**
     * 格式化二级目录数据
     * @param jsonArray
     * @return
     */
    public List<ExpandableItem> formatExpandableChildEntity(JSONArray jsonArray, String columnTitle, String columnId, String bigColumnId, int hasBuy){
        List<ExpandableItem> directoryEntityList = new ArrayList<ExpandableItem>();
        for (Object object : jsonArray) {
            ExpandableItem secondDirectoryEntity = new ExpandableItem();
            JSONObject jsonObject = (JSONObject) object;
            int resourceType = jsonObject.getIntValue("resource_type");
            int[] types = new int[]{1 , 2, 3};
            if(Arrays.binarySearch(types, resourceType) < 0){
                //过滤掉非图文音视频资源
                continue;
            }
            secondDirectoryEntity.setApp_id(jsonObject.getString("app_id"));
            secondDirectoryEntity.setResource_id(jsonObject.getString("resource_id"));
            secondDirectoryEntity.setTitle(jsonObject.getString("title"));
            secondDirectoryEntity.setImg_url(jsonObject.getString("img_url"));
            secondDirectoryEntity.setImg_url_compress(jsonObject.getString("img_url_compress"));
            secondDirectoryEntity.setResource_type(jsonObject.getIntValue("resource_type"));
            secondDirectoryEntity.setAudio_length(jsonObject.getIntValue("audio_length"));
            secondDirectoryEntity.setVideo_length(jsonObject.getIntValue("video_length"));
            secondDirectoryEntity.setAudio_url(jsonObject.getString("audio_url"));
            secondDirectoryEntity.setVideo_url(jsonObject.getString("video_url"));
            secondDirectoryEntity.setColumnTitle(columnTitle);
            secondDirectoryEntity.setColumnId(columnId);
            secondDirectoryEntity.setBigColumnId(bigColumnId);
            secondDirectoryEntity.setIsTry(jsonObject.getIntValue("is_try"));
            secondDirectoryEntity.setIsHasBuy(hasBuy);
            secondDirectoryEntity.setLoadType(1);

            directoryEntityList.add(secondDirectoryEntity);
//            directoryEntity.addSubItem(secondDirectoryEntity);
        }
        return directoryEntityList;
    }

    /**
     * 根据每页大小请求专栏列表
     * @param resourceId    资源 id
     * @param resourceType  资源类型
     * @param pageSize      每页数量
     */
    public void requestColumnListByNum(String resourceId, String resourceType, int pageSize) {
        ColumnListRequst columnListRequst = new ColumnListRequst(this);
        columnListRequst.addDataParam("goods_id",resourceId);
        columnListRequst.addDataParam("resource_type",resourceType);
        columnListRequst.addDataParam("page", "1");
        columnListRequst.addDataParam("page_size", pageSize + "");
        columnListRequst.sendRequest();
    }


    public static ColumnSecondDirectoryEntity ExpandableItem2ColumnSecondDirectoryEntity(ExpandableItem expandableItem){
        String json =  JSONObject.toJSONString(expandableItem);
        return JSONObject.parseObject(json, ColumnSecondDirectoryEntity.class);
    }

    public void productTypeRequest(String productId){
        QueryProductTypeRequest productTypeRequest = new QueryProductTypeRequest(this);
        productTypeRequest.addRequestParam("product_id", productId);
        productTypeRequest.sendRequest();
    }

    /**
     * 格式化一级目录数据
     * @param jsonArray
     */
    public List<MultiItemEntity> formatDownloadExpandableEntity(JSONArray jsonArray, String bigColumnId, int hasBuy){
        List<MultiItemEntity> directoryEntityList = new ArrayList<MultiItemEntity>();
        for (Object object : jsonArray) {
            ExpandableLevel directoryEntity = new ExpandableLevel();

            JSONObject jsonObject = (JSONObject) object;
            directoryEntity.setApp_id(jsonObject.getString("app_id"));
            directoryEntity.setTitle(jsonObject.getString("title"));
            String columnId = jsonObject.getString("resource_id");
            directoryEntity.setImg_url(jsonObject.getString("img_url"));
            directoryEntity.setImg_url_compress(jsonObject.getString("img_url_compress"));
            directoryEntity.setResource_id(columnId);
            directoryEntity.setBigColumnId(bigColumnId);
            directoryEntity.setResource_type(jsonObject.getIntValue("resource_type"));

            //添加一条空数据，作为“加载状态”条目
            ExpandableItem loadSecondDirectoryEntity = new ExpandableItem();
            loadSecondDirectoryEntity.setItemType(DownLoadExpandAdapter.LOAD_STATE);
            loadSecondDirectoryEntity.setLoadType(DownLoadExpandAdapter.LOADING);
            directoryEntity.addSubItem(loadSecondDirectoryEntity);
            //添加一条空数据，作为“收起”条目
            ExpandableItem emptySecondDirectoryEntity = new ExpandableItem();
            emptySecondDirectoryEntity.setItemType(DownLoadExpandAdapter.GROUP_BOTTOM_ITEM);
            directoryEntity.addSubItem(emptySecondDirectoryEntity);

            directoryEntityList.add(directoryEntity);
        }
        return directoryEntityList;
    }

    /**
     * 请求下载列表数据
     *
     * @param goodsId      商品 id
     * @param goodsType    商品类型
     * @param pageSize     每页大小
     * @param downloadType 下载类型
     * @param lastId       最后一个 id（用于分页请求）
     * @param requestTag   请求的 tag
     */
    public void requestDownloadList(String goodsId, int goodsType, int pageSize, int[] downloadType, String lastId, String requestTag) {
        DownloadListRequest downloadListRequest = new DownloadListRequest(this);

        downloadListRequest.addDataParam("goods_id", goodsId);
        downloadListRequest.addDataParam("goods_type", goodsType);
        downloadListRequest.addDataParam("download_type", downloadType);
        downloadListRequest.addDataParam("last_id", lastId);
        downloadListRequest.addDataParam("page_size", pageSize);
        downloadListRequest.setRequestTag(requestTag);

        downloadListRequest.sendRequest();
    }
}
