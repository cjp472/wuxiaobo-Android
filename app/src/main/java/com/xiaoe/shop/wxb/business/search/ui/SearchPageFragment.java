package com.xiaoe.shop.wxb.business.search.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.xiaoe.common.entitys.ComponentInfo;
import com.xiaoe.common.entitys.DecorateEntityType;
import com.xiaoe.common.entitys.KnowledgeCommodityItem;
import com.xiaoe.common.entitys.SearchHistory;
import com.xiaoe.common.interfaces.OnItemClickWithPosListener;
import com.xiaoe.common.db.SQLiteUtil;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.adapter.decorate.DecorateRecyclerAdapter;
import com.xiaoe.shop.wxb.base.BaseFragment;
import com.xiaoe.shop.wxb.business.search.presenter.HistoryRecyclerAdapter;
import com.xiaoe.shop.wxb.business.search.presenter.RecommendRecyclerAdapter;
import com.xiaoe.shop.wxb.business.search.presenter.SearchSQLiteCallback;

public class SearchPageFragment extends BaseFragment implements OnItemClickWithPosListener {

    private static final String TAG = "SearchPageFragment";

    private Unbinder unbinder;
    private Context mContext;
    private boolean destroyView = false;
    protected View viewWrap;

    private int layoutId = -1;
    SearchActivity searchActivity;
    Object dataList; // 搜索结果

    // 软键盘
    InputMethodManager imm;

    // 首页 fragment 所需要的基础内容
    SearchContentView historyContentView;
    SearchContentView recommendContentView;
    List<SearchHistory> historyList; // 历史搜索记录集合
    List<String> recommendData; // 推荐集合（暂时写死）

    // 搜索结果 fragment 需要的基础内容
    RecyclerView searchResultRecycler;
    List<ComponentInfo> itemComponentList;
    List<JSONObject> itemJsonList; // 单品 json 集合
    List<JSONObject> groupJsonList; // 专栏、大专栏 json 集合
    List<SearchHistory> historyData; // 历史数据
    HistoryRecyclerAdapter historyAdapter; // 历史数据适配器

    public static SearchPageFragment newInstance(int layoutId) {
        SearchPageFragment searchPageFragment = new SearchPageFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("layoutId", layoutId);
        searchPageFragment.setArguments(bundle);
        return searchPageFragment;
    }

    protected void setDataList(Object dataList) {
        if (this.dataList != null) { // 保证数据都是最新的
            this.dataList = null;
            // 组件集合、专栏/大专栏集合、单品集合都需要清空
            this.itemComponentList.clear();
            this.groupJsonList.clear();
            this.itemJsonList.clear();
        }
        this.dataList = dataList;
        if (this.searchResultRecycler != null) { // recyclerView 已经初始化过一次，重新加载数据
            initDataList();
        }
    }

    protected void setHistoryList(List<SearchHistory> historyList) {
        this.historyList = historyList;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            layoutId = bundle.getInt("layoutId");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewWrap = inflater.inflate(layoutId, null, false);
        unbinder = ButterKnife.bind(this, viewWrap);
        mContext = getContext();
        searchActivity = (SearchActivity) getActivity();
        imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        return viewWrap;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (layoutId != -1) {
            initView();
        }
    }

    private void initView() {
        switch (layoutId) {
            case R.layout.fragment_search_main:
                initSearchMainFragment();
                break;
            case R.layout.fragment_search_empty:
                // do nothing;
                break;
            case R.layout.fragment_search_result:
                initSearchResultFragment();
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        destroyView = true;
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    // 初始化搜索主页 fragment
    private void initSearchMainFragment() {
        historyContentView = (SearchContentView) viewWrap.findViewById(R.id.history_content);

        if (historyList.size() == 0) { // 没有历史记录
            historyContentView.setVisibility(View.GONE);
        } else { // 有历史记录
            historyContentView.setTitleStartText("历史搜索");
            historyContentView.setTitleEndText("清空历史搜索");

            historyContentView.setTitleEndClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 删除数据库中全部数据
                    SQLiteUtil.execSQL("delete from " + SearchSQLiteCallback.TABLE_NAME_CONTENT);
                    Toast.makeText(searchActivity, "删除成功", Toast.LENGTH_SHORT).show();
                    historyContentView.setVisibility(View.GONE);
                }
            });

            historyData = historyList;
            historyAdapter = new HistoryRecyclerAdapter(mContext, historyData);
            historyAdapter.setOnItemClickWithPosListener(this);
            historyContentView.setHistoryContentAdapter(historyAdapter);
        }

        recommendContentView = (SearchContentView) viewWrap.findViewById(R.id.recommend_content);
        recommendContentView.setTitleEndVisibility(View.GONE);
        recommendContentView.setTitleStartText("大家都在搜");

        recommendData = new ArrayList<>();
        // 默认的四个关键词
        recommendData.add("我的财富计划");
        recommendData.add("吴晓波频道");
        recommendData.add("我的职场计划");
        recommendData.add("避免败局");
        RecommendRecyclerAdapter recommendAdapter = new RecommendRecyclerAdapter(mContext, recommendData);
        recommendAdapter.setOnTabClickListener(this);
        recommendContentView.setRecommendContentAdapter(recommendAdapter);
    }

    // 初始化搜索结果 fragment
    private void initSearchResultFragment() {
        // 初始化基础设施
        itemJsonList = new ArrayList<>();
        groupJsonList = new ArrayList<>();
        // 初始化组件容器
        itemComponentList = new ArrayList<>();
        searchResultRecycler = (RecyclerView) viewWrap.findViewById(R.id.result_content);
        initDataList();
    }

    // 初始化搜索结果数据类型，分类依据 -- 单品 / 专栏、大专栏
    private void initDataList() {
        JSONArray dataJsonList = (JSONArray) dataList;
        for (Object jsonItem : dataJsonList) {
            JSONObject dataListItem = (JSONObject) jsonItem;
            String label = dataListItem.getString("label");
            // 内容 list
            JSONArray list = (JSONArray) dataListItem.get("list");
            if (label.equals("图文") || label.equals("音频") || label.equals("视频")) {
                for (Object listItem : list) {
                    JSONObject item = (JSONObject) listItem;
                    item.put("resource_type", dataListItem.getInteger("type"));
                    itemJsonList.add(item);
                }
            } else if (label.equals("专栏") || label.equals("大专栏")) {
                for (Object listItem : list) {
                    JSONObject item = (JSONObject) listItem;
                    item.put("resource_type", dataListItem.getInteger("type"));
                    groupJsonList.add(item);
                }
            }
        }

        initSearchResultData();
    }

    // 初始化搜索结果数据
    private void initSearchResultData() {
        // 专栏、大专栏
        initPageDataByJson(groupJsonList, true);
        // 单品
        initPageDataByJson(itemJsonList, false);
        // 初始化 recyclerView
        initRecycler();
    }

    /**
     * 初始化页面数据 -- 大于 3 个宫格显示，否则列表显示
     *
     * @param listData 页面数据（包括单品、专栏、大专栏）
     * @param isGroup 是否为专栏/大专栏
     */
    private void initPageDataByJson(List<JSONObject> listData, boolean isGroup) {
        ComponentInfo itemComponentInfo = new ComponentInfo();
        if (itemJsonList.size() > 0 && itemJsonList.size() <= 3) { // 列表显示
            itemComponentInfo.setType(DecorateEntityType.KNOWLEDGE_COMMODITY_STR);
            itemComponentInfo.setSubType(DecorateEntityType.KNOWLEDGE_LIST_STR);
        } else { // 宫格显示
            itemComponentInfo.setType(DecorateEntityType.KNOWLEDGE_COMMODITY_STR);
            itemComponentInfo.setSubType(DecorateEntityType.KNOWLEDGE_GROUP_STR);
        }
        itemComponentInfo.setHideTitle(false);
        if (isGroup) {
            itemComponentInfo.setTitle("系列课程");
        } else {
            itemComponentInfo.setTitle("单品课程");
        }
        // 目的将 title 的查看更多隐藏掉
        itemComponentInfo.setDesc("");
        List<KnowledgeCommodityItem> contentList = new ArrayList<>();
        // 遍历系列课程
        for (Object itemContent : listData) {
            KnowledgeCommodityItem commodityItem = new KnowledgeCommodityItem();
            JSONObject item = (JSONObject) itemContent;
            String resourceId = item.getString("id");
            String resourceType = convertInt2Str((int) item.get("resource_type"));
            String imgUrl = item.getString("img_url");
            String title = item.getString("title");
            String columnDesc = item.getString("summary");
            float price = item.getFloat("price");
            String priceStr;
            if (price == 0) {
                priceStr = "";
            } else {
                priceStr = "￥" + price;
            }
            commodityItem.setItemTitle(title);
            commodityItem.setItemImg(imgUrl);
            commodityItem.setItemTitleColumn(columnDesc);
            commodityItem.setSrcType(resourceType);
            commodityItem.setResourceId(resourceId);
            commodityItem.setItemPrice(priceStr);
            contentList.add(commodityItem);
        }
        // 有数据才显示
        if (contentList.size() > 0) {
            itemComponentInfo.setKnowledgeCommodityItemList(contentList);
            itemComponentList.add(itemComponentInfo);
        }
    }

    // 初始化 recyclerView
    private void initRecycler() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        searchResultRecycler.setLayoutManager(layoutManager);
        DecorateRecyclerAdapter decorateRecyclerAdapter = new DecorateRecyclerAdapter(mContext, itemComponentList);
        searchResultRecycler.setAdapter(decorateRecyclerAdapter);
        decorateRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(View view, int position) {
        toggleSoftKeyboard();
        if (historyContentView.getVisibility() == View.VISIBLE && historyContentView.isMatchRecycler(view.getParent())) {
            String searchContent = historyList.get(position).getmContent();
            ((SearchActivity) getActivity()).searchContent.setText(searchContent);
            ((SearchActivity) getActivity()).searchContent.setSelection(searchContent.length());
            ((SearchActivity) getActivity()).obtainSearchResult(((SearchActivity) getActivity()).searchContent.getText().toString());
        } else if (recommendContentView.getVisibility() == View.VISIBLE && recommendContentView.isMatchRecycler(view.getParent())) {
            String searchContent = recommendData.get(position);
            ((SearchActivity) getActivity()).searchContent.setText(searchContent);
            ((SearchActivity) getActivity()).searchContent.setSelection(searchContent.length());
            ((SearchActivity) getActivity()).obtainSearchResult(((SearchActivity) getActivity()).searchContent.getText().toString());
            if (!searchActivity.hasData(searchContent)) { // 点击推荐，查库没有后需要入库
                String currentTime = searchActivity.obtainCurrentTime();
                SearchHistory searchHistory = new SearchHistory(searchContent, currentTime);
                SQLiteUtil.insert(SearchSQLiteCallback.TABLE_NAME_CONTENT, searchHistory);

                // 入库后集合需要改变
                if (historyData != null) {
                    if (historyData.size() == 5) {
                        historyData.add(0, searchHistory);
                        historyData.remove(historyData.size() - 1); // 去掉最后一个
                    } else { // 否则直接添加
                        historyData.add(0, searchHistory);
                    }
                }
                historyAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * 如果软键盘弹出，就关闭软键盘
     */
    private void toggleSoftKeyboard() {
        if (imm != null && imm.isActive()) {
            View view = searchActivity.getCurrentFocus();
            if (view != null) {
                IBinder iBinder = view.getWindowToken();
                imm.hideSoftInputFromWindow(iBinder, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    /**
     * 资源类型转换 int - str
     * @param resourceType 资源类型
     * @return 资源类型的字符串形式
     */
    protected String convertInt2Str(int resourceType) {
        switch (resourceType) {
            case 1: // 图文
                return DecorateEntityType.IMAGE_TEXT;
            case 2: // 音频
                return DecorateEntityType.AUDIO;
            case 3: // 视频
                return DecorateEntityType.VIDEO;
            case 6: // 专栏
                return DecorateEntityType.COLUMN;
            case 8: // 大专栏
                return DecorateEntityType.TOPIC;
            default:
                return null;
        }
    }
}
