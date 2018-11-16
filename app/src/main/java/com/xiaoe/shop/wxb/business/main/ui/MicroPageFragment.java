package com.xiaoe.shop.wxb.business.main.ui;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.SharedElementCallback;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.view.SimpleDraweeView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.xiaoe.common.entitys.ComponentInfo;
import com.xiaoe.common.entitys.DecorateEntityType;
import com.xiaoe.common.entitys.FlowInfoItem;
import com.xiaoe.common.entitys.GraphicNavItem;
import com.xiaoe.common.entitys.KnowledgeCommodityItem;
import com.xiaoe.common.entitys.RecentUpdateListItem;
import com.xiaoe.common.entitys.ShufflingItem;
import com.xiaoe.common.utils.Dp2Px2SpUtil;
import com.xiaoe.network.NetworkCodes;
import com.xiaoe.network.requests.ColumnListRequst;
import com.xiaoe.network.requests.IRequest;
import com.xiaoe.network.requests.PageFragmentRequest;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.adapter.decorate.DecorateRecyclerAdapter;
import com.xiaoe.shop.wxb.base.BaseFragment;
import com.xiaoe.shop.wxb.business.column.presenter.ColumnPresenter;
import com.xiaoe.shop.wxb.business.main.presenter.PageFragmentPresenter;
import com.xiaoe.shop.wxb.business.search.presenter.SpacesItemDecoration;
import com.xiaoe.shop.wxb.common.JumpDetail;
import com.xiaoe.shop.wxb.events.AudioPlayEvent;
import com.xiaoe.shop.wxb.interfaces.OnCustomScrollChangedListener;
import com.xiaoe.shop.wxb.utils.StatusBarUtil;
import com.xiaoe.shop.wxb.widget.CustomScrollView;
import com.xiaoe.shop.wxb.widget.StatusPagerView;

public class MicroPageFragment extends BaseFragment implements OnCustomScrollChangedListener {

    private static final String TAG = "MicroPageFragment";

    private Unbinder unbinder;
    private Context mContext;
    private String microPageId = "";

    @BindView(R.id.micro_page_scroller)
    CustomScrollView microPageScroller;

    @BindView(R.id.micro_page_wrap)
    FrameLayout microPageWrap;
    @BindView(R.id.micro_page_title_bg)
    SimpleDraweeView microPageTitleBg;
    @BindView(R.id.micro_page_content)
    RecyclerView microPageContent;

    @BindView(R.id.micro_page_toolbar)
    FrameLayout microPageToolbar;
    @BindView(R.id.micro_page_toolbar_title)
    TextView microPageToolbarTitle;
    @BindView(R.id.micro_page_toolbar_search)
    ImageView microPageToolbarSearch;

    @BindView(R.id.micro_page_loading)
    StatusPagerView microPageLoading;

    List<ComponentInfo> microPageList;

    int toolbarHeight;
    boolean isMain = true;
    boolean hasSearch = false;

    MainActivity mainActivity;
    private String mColumnTitle;

    public static MicroPageFragment newInstance(String microPageId) {
        MicroPageFragment microPageFragment = new MicroPageFragment();
        Bundle bundle = new Bundle();
        bundle.putString("microPageId", microPageId);
        microPageFragment.setArguments(bundle);
        return microPageFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_micro_page, null, false);
        unbinder = ButterKnife.bind(this, view);
        mContext = getContext();
        mainActivity = (MainActivity) getActivity();
        view.setPadding(0, StatusBarUtil.getStatusBarHeight(mContext), 0, 0);
        EventBus.getDefault().register(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            // 获取微页面 id
            microPageId = bundle.getString("microPageId");
        }
    }

    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);
        if (isVisible) {
            // fragment 显示的时候显示 loading
            microPageLoading.setHintStateVisibility(View.GONE);
            microPageLoading.setLoadingState(View.VISIBLE);
        }
    }

    @Override
    protected void onFragmentFirstVisible() {
        super.onFragmentFirstVisible();
        // 网络请求数据代码
        PageFragmentPresenter hp = new PageFragmentPresenter(this);
        hp.requestMicroPageData(microPageId);
    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        if(isFragmentDestroy){
            return;
        }
        super.onMainThreadResponse(iRequest, success, entity);
        if (success) { // 请求成功
            JSONObject result = (JSONObject) entity;
            int code = result.getInteger("code");
            if (iRequest instanceof PageFragmentRequest) {
                if (code == NetworkCodes.CODE_SUCCEED) {
                    JSONObject data = (JSONObject) result.get("data");
                    initPageData(data);
                    initMainContent();
                    // 请求成功之后隐藏 loading
                    microPageLoading.setVisibility(View.GONE);
                } else if (code == NetworkCodes.CODE_GOODS_DELETE) { // 微页面不存在
                    Log.d(TAG, "onMainThreadResponse: micro_page --- " + result.get("msg"));
                } else if (code == NetworkCodes.CODE_TINY_PAGER_NO_FIND) { // 微页面已被删除
                    Log.d(TAG, "onMainThreadResponse: micro_page --- " + result.get("msg"));
                }
            } else if (iRequest instanceof ColumnListRequst) {
                if (code == NetworkCodes.CODE_SUCCEED) {
                    JSONArray data = (JSONArray) result.get("data");
                    refreshRecentComponent(data, iRequest.getDataParams().getString("goods_id"));
                } else {
                    Log.d(TAG, "onMainThreadResponse: 获取最近更新组件的列表失败..");
                }
            }
        } else {
            Log.d(TAG, "onMainThreadResponse: fail");
        }
    }

    // 刷新最近更新组件的子列表
    private void refreshRecentComponent(JSONArray data, String columnId) {
        List<RecentUpdateListItem> itemList = new ArrayList<>();
        int audioCount = 0; // 记录音频数据条数
        // 获取专栏 list
        for (Object dataItem : data) {
            JSONObject dataJsonItem = (JSONObject) dataItem;
            RecentUpdateListItem recentUpdateListItem = new RecentUpdateListItem();
            String title = dataJsonItem.getString("title");
            // 频道组件如果 playState 传空的话会隐藏播放 icon
            String resourceType = convertInt2Str(dataJsonItem.getInteger("resource_type"));
            String resourceId = dataJsonItem.getString("resource_id");
            recentUpdateListItem.setListTitle(title);
            recentUpdateListItem.setColumnTitle(mColumnTitle);
            if (resourceType.equals(DecorateEntityType.AUDIO)) { // 只有音频才需要显示播放 icon
                recentUpdateListItem.setListPlayState(DecorateEntityType.ITEM_RECENT_PLAY);
                audioCount++; // 视音频就加 1
            } else {
                recentUpdateListItem.setListPlayState("");
            }
            recentUpdateListItem.setListResourceId(resourceId);

            recentUpdateListItem.setAppId(dataJsonItem.getString("app_id"));
            recentUpdateListItem.setImgUrl(dataJsonItem.getString("img_url"));
            recentUpdateListItem.setImgUrlCompress(dataJsonItem.getString("img_url_compress"));
            recentUpdateListItem.setResourceType(dataJsonItem.getIntValue("resource_type"));
            recentUpdateListItem.setAudioLength(dataJsonItem.getIntValue("audio_length"));
            recentUpdateListItem.setVideoLength(dataJsonItem.getIntValue("video_length"));
            recentUpdateListItem.setAudioUrl(dataJsonItem.getString("audio_url"));
            recentUpdateListItem.setColumnId(columnId);
            recentUpdateListItem.setBigColumnId("");
            recentUpdateListItem.setListIsFormUser(mainActivity.isFormalUser);

            itemList.add(recentUpdateListItem);
        }
        // 更新频道组件
        for (ComponentInfo componentInfo : microPageList) {
            if (componentInfo.getType().equals(DecorateEntityType.RECENT_UPDATE_STR)) {
                componentInfo.setSubList(itemList);
                if (audioCount == 3) { // 3 个都是音频，就显示收听全部按钮，不想加字段，就用这个 hideTitle 来判断
                    componentInfo.setHideTitle(false);
                } else {
                    componentInfo.setHideTitle(true);
                }
            }
        }
        // 刷新操作
        microPageAdapter.notifyDataSetChanged();
    }

    // 初始化微页面数据
    private void initPageData(JSONObject data) {
        JSONArray microPageData = (JSONArray) data.get("components");
        if (microPageData == null) {
            return;
        }
        for (Object item : microPageData) {
            JSONObject itemObj = ((JSONObject) item);
            switch (itemObj.getString("type")) {
                case DecorateEntityType.SEARCH_STR: // 搜索组件
                    ComponentInfo componentInfo_title = new ComponentInfo();
                    componentInfo_title.setTitle("课程");
                    componentInfo_title.setType(DecorateEntityType.SEARCH_STR);
                    microPageList.add(componentInfo_title);
                    hasSearch = true;
                    break;
                case DecorateEntityType.SHUFFLING_FIGURE_STR: // 轮播图
                    ComponentInfo componentInfo_shuffling = new ComponentInfo();
                    componentInfo_shuffling.setType(DecorateEntityType.SHUFFLING_FIGURE_STR);
                    List<ShufflingItem> urlList = new ArrayList<>();
                    JSONArray shufflingSubList = (JSONArray) itemObj.get("list");
                    for (Object subItem : shufflingSubList) {
                        JSONObject subItemObj = (JSONObject) subItem;
                        String title = subItemObj.getString("title");
                        String imgUrl = subItemObj.getString("img_url");
                        String resourceType = subItemObj.getString("src_type");
                        String resourceId = subItemObj.getString("src_id");

                        ShufflingItem shufflingItem = new ShufflingItem();
                        shufflingItem.setTitle(title);
                        shufflingItem.setImgUrl(imgUrl);
                        shufflingItem.setSrcId(resourceId);
                        shufflingItem.setSrcType(resourceType);

                        urlList.add(shufflingItem);
                    }
                    componentInfo_shuffling.setShufflingList(urlList);
                    microPageList.add(componentInfo_shuffling);
                    break;
                case DecorateEntityType.GRAPHIC_NAVIGATION_STR: // 图文导航
                    ComponentInfo componentInfo_navigator = new ComponentInfo();
                    componentInfo_navigator.setType(DecorateEntityType.GRAPHIC_NAVIGATION_STR);
                    List<GraphicNavItem> graphicNavItemList = new ArrayList<>();
                    JSONArray navigatorSubList = (JSONArray) itemObj.get("list");
                    for (Object subItem : navigatorSubList) {
                        JSONObject subItemObj = (JSONObject) subItem;
                        GraphicNavItem graphicNavItem = new GraphicNavItem();
                        graphicNavItem.setNavIcon(subItemObj.getString("img_url"));
                        graphicNavItem.setNavContent(subItemObj.getString("title"));
                        graphicNavItem.setNavResourceType(subItemObj.getString("src_type"));
                        graphicNavItem.setNavResourceId(subItemObj.getString("src_id"));
                        graphicNavItemList.add(graphicNavItem);
                    }
                    componentInfo_navigator.setGraphicNavItemList(graphicNavItemList);
                    microPageList.add(componentInfo_navigator);
                    break;
                case DecorateEntityType.GOODS_STR:
                    initGoods(itemObj);
                    break;
                case DecorateEntityType.FLOW_INFO_STR:
                    initFlowInfo(itemObj);
                    break;
            }
        }
    }

    // 初始化商品
    private void initGoods(JSONObject itemObj) {
        String type_title = itemObj.getString("type_title");
        switch (type_title) {
            case DecorateEntityType.RECENT_UPDATE_STR: // 频道
                // TODO: 初始化频道数据
                JSONArray recentList = (JSONArray) itemObj.get("list");
                for (Object listItem : recentList) {
                    ComponentInfo component_recent = new ComponentInfo();
                    component_recent.setType(DecorateEntityType.RECENT_UPDATE_STR);
                    component_recent.setFormUser(mainActivity.isFormalUser);
                    JSONObject jsonItem = (JSONObject) listItem;

                    String recentTitle = jsonItem.getString("title");
                    mColumnTitle = recentTitle;
                    // 频道组件对应的专栏 id
                    String recentId = jsonItem.getString("src_id");
                    String imgUrl = jsonItem.getString("img_url");
                    int updateCount = jsonItem.getInteger("resource_count") == null ? 0 : jsonItem.getInteger("resource_count");
                    String updateCountStr = "已更新至" + updateCount + "期";
                    component_recent.setTitle(recentTitle);
                    component_recent.setImgUrl(imgUrl);
                    component_recent.setDesc(updateCountStr);
                    // 最近更新需要设置专栏 id
                    component_recent.setColumnId(recentId);
                    if(TextUtils.isEmpty(jsonItem.getString("show_price"))){
                        component_recent.setHasBuy(true);
                    }else{
                        component_recent.setHasBuy(false);
                    }
                    if (recentId != null) {
                        ColumnPresenter columnPresenter = new ColumnPresenter(this);
                        // 请求该专栏下三条数据
                        columnPresenter.requestColumnListByNum(recentId, "0", 3);
                    }
                    // 先把频道组件存起来，等网络请求的回调回来后再操作
                    microPageList.add(component_recent);
                }
                break;
            case DecorateEntityType.KNOWLEDGE_COMMODITY_STR: // 知识商品
                if (itemObj.getInteger("list_style") == 0) { // 列表形式
                    ComponentInfo componentInfo_know_list = new ComponentInfo();
                    componentInfo_know_list.setType(DecorateEntityType.KNOWLEDGE_COMMODITY_STR);
                    componentInfo_know_list.setSubType(DecorateEntityType.KNOWLEDGE_LIST);
                    // 微页面列表形式的知识商品组件都隐藏 title
                    componentInfo_know_list.setHideTitle(true);
                    componentInfo_know_list.setGroupId(itemObj.getString("tag_id"));
                    List<KnowledgeCommodityItem> listItems = new ArrayList<>();
                    JSONArray knowledgeListSubList = (JSONArray) itemObj.get("list");
                    if (knowledgeListSubList == null) {
                        return;
                    }
                    initKnowledgeData(listItems, knowledgeListSubList);
                    componentInfo_know_list.setKnowledgeCommodityItemList(listItems);
                    microPageList.add(componentInfo_know_list);
                } else if (itemObj.getInteger("list_style") == 2) { // （小图）宫格形式
                    ComponentInfo componentInfo_know_group = new ComponentInfo();
                    componentInfo_know_group.setType(DecorateEntityType.KNOWLEDGE_COMMODITY_STR);
                    componentInfo_know_group.setSubType(DecorateEntityType.KNOWLEDGE_GROUP);
                    componentInfo_know_group.setTitle("学会管理自己的财富");
                    componentInfo_know_group.setDesc("查看更多");
                    componentInfo_know_group.setGroupId(itemObj.getString("tag_id"));
                    List<KnowledgeCommodityItem> groupItems = new ArrayList<>();
                    JSONArray knowledgeGroupSubList = (JSONArray) itemObj.get("list");
                    if (knowledgeGroupSubList == null) {
                        return;
                    }
                    initKnowledgeData(groupItems, knowledgeGroupSubList);
                    componentInfo_know_group.setKnowledgeCommodityItemList(groupItems);
                    microPageList.add(componentInfo_know_group);
                }
                break;
        }
    }

    // 从 JSON 对象数组中获取知识商品的数据并初始化到已设置好的对象中
    private void initKnowledgeData(List<KnowledgeCommodityItem> itemList, JSONArray knowledgeListSubList) {
        for (Object subItem : knowledgeListSubList) {
            JSONObject listSubItemObj = (JSONObject) subItem;
            KnowledgeCommodityItem item = new KnowledgeCommodityItem();
            item.setItemTitle(listSubItemObj.getString("title"));
            item.setItemTitleColumn(listSubItemObj.getString("summary"));
            item.setItemImg(listSubItemObj.getString("img_url"));
            String price = "￥" + listSubItemObj.getString("show_price");
            if (price.equals("￥")) { // 表示买了，所以没有价格
                item.setItemPrice(null);
                item.setHasBuy(true);
            } else {
                item.setItemPrice(price);
                item.setHasBuy(false);
            }
            String srcType = listSubItemObj.getString("src_type");
            String srcId = listSubItemObj.getString("src_id");
            // view_count -- 浏览次数 / resource_count -- 更新期数 / purchase_count -- 订阅数
            int viewCount = listSubItemObj.getInteger("view_count") == null ? 0 : listSubItemObj.getInteger("view_count");
            int resourceCount = listSubItemObj.getInteger("resource_count") == null ? 0 : listSubItemObj.getInteger("resource_count");
            item.setSrcType(srcType);
            item.setResourceId(srcId);
            // 专栏或者大专栏订阅量就是 purchaseCount
            if (srcType.equals(DecorateEntityType.COLUMN) || srcType.equals(DecorateEntityType.TOPIC)) {
                viewCount = resourceCount;
            }
            String viewDesc = obtainViewCountDesc(srcType, viewCount);
            item.setItemDesc(viewDesc);
            itemList.add(item);
        }
    }

    // 根据子类型获取浏览字段
    private String obtainViewCountDesc (String srcType, int viewCount) {
        if (viewCount == 0) {
            return "";
        }
        switch (srcType) {
            case DecorateEntityType.IMAGE_TEXT: // 图文
                return viewCount + "次阅读";
            case DecorateEntityType.AUDIO: // 音频
            case DecorateEntityType.VIDEO: // 视频
                return viewCount + "次播放";
            case DecorateEntityType.TOPIC: // 大专栏
            case DecorateEntityType.COLUMN: // 专栏
                return "已更新" + viewCount + "期";
            default:
                return "";
        }
    }

    // 初始化信息流组件
    private void initFlowInfo(JSONObject data) {
        // 信息流 information_list
        JSONArray informationList = (JSONArray) data.get("information_list");
        for (Object listItem : informationList) {
            ComponentInfo flowInfoComponent = new ComponentInfo();
            flowInfoComponent.setType(DecorateEntityType.FLOW_INFO_STR);
            flowInfoComponent.setFormUser(mainActivity.isFormalUser);
            JSONObject flowInfoItem = (JSONObject) listItem;
            // 拿到信息流 title 信息
            JSONArray dateTag = (JSONArray) flowInfoItem.get("date_tag");
            for (int i = 0; i< dateTag.size(); i++) { // 0 -- 今日；1 -- *月*日；2 -- 星期*
                String today = dateTag.getString(0);
                if (!today.equals("")) { // 今日
                    String flowInfoTitle = dateTag.getString(1) + " " + dateTag.getString(2);
                    flowInfoComponent.setTitle(flowInfoTitle);
                    flowInfoComponent.setDesc(today);
                    flowInfoComponent.setJoinedDesc("我正在学");
                    flowInfoComponent.setImgUrl("res:///" + R.mipmap.icon_taday_learning);
                } else { // 非今日，不显示我正在学
                    String flowInfoTitle = dateTag.getString(1);
                    String flowInfoDesc = dateTag.getString(2);
                    flowInfoComponent.setTitle(flowInfoTitle);
                    flowInfoComponent.setDesc(flowInfoDesc);
                    flowInfoComponent.setJoinedDesc("");
                    flowInfoComponent.setImgUrl("");
                }
            }
            // 拿到信息流的 content 信息
            List<FlowInfoItem> dataList = new ArrayList<>();
            JSONArray flowInfoList = (JSONArray) flowInfoItem.get("list");
            for (Object item : flowInfoList) {
                JSONObject flowInfo = (JSONObject) item;
                FlowInfoItem fii = new FlowInfoItem();
                String resourceType = flowInfo.getString("src_type");
                if (resourceType.equals("member")) { continue; } // 会员先不支持
                String resourceId = flowInfo.getString("src_id");
                String title = flowInfo.getString("title");
                String desc = flowInfo.getString("summary");
                String imgUrl = flowInfo.getString("img_url");
                String showPrice = flowInfo.getString("show_price").equals("") ? "" : "￥" + flowInfo.getString("show_price");
                boolean hasBuy = showPrice.equals("");

                fii.setItemType(resourceType);
                fii.setItemId(resourceId);
                fii.setItemTitle(title);
                fii.setItemDesc(desc);
                fii.setItemPrice(showPrice);
                fii.setItemHasBuy(hasBuy);
                fii.setItemImg(imgUrl);

                dataList.add(fii);
            }
            flowInfoComponent.setFlowInfoItemList(dataList);
            microPageList.add(flowInfoComponent);
        }
    }


    // 拿到请求到的数据后进行界面初始化
    public void init() {
        microPageList = new ArrayList<>();
        microPageScroller.setScrollChanged(this);
        toolbarHeight = Dp2Px2SpUtil.dp2px(mContext,160);

        // 微页面 id 存在并且不是首页的微页面 id，默认是课程页面
        if (!microPageId.equals("") && !microPageId.equals(MainActivity.MICRO_PAGE_MAIN)) { // 课程页设置一个顶部背景
            microPageTitleBg.setImageURI("res:///" + R.mipmap.class_bg);
            microPageToolbar.setVisibility(View.VISIBLE);
            // 初始化 toolbar
            int statusBarHeight = StatusBarUtil.getStatusBarHeight(mContext);
            // 沉浸式初始化
            microPageWrap.setPadding(0, statusBarHeight, 0, 0);
            // toolbar 的内容都设置为透明
            microPageToolbar.setBackgroundColor(Color.argb(0,255,255,255));
            microPageToolbarTitle.setVisibility(View.GONE);
            microPageToolbarSearch.setVisibility(View.GONE);
            microPageToolbarSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    JumpDetail.jumpSearch(mContext);
                }
            });
            isMain = false;
        } else {
            microPageTitleBg.setImageURI("");
            microPageToolbar.setVisibility(View.GONE);
            isMain = true;
        }

        // 转场动画 SimpleDraweeView 处理
        getActivity().setExitSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onSharedElementEnd(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
                super.onSharedElementEnd(sharedElementNames, sharedElements, sharedElementSnapshots);
                Log.d(TAG, "onSharedElementEnd: ");
                for (View view : sharedElements) {
                    if (view instanceof SimpleDraweeView) {
                        view.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onSharedElementStart(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
                super.onSharedElementStart(sharedElementNames, sharedElements, sharedElementSnapshots);
                Log.d(TAG, "onSharedElementStart: ");
            }

            @Override
            public void onSharedElementsArrived(List<String> sharedElementNames, List<View> sharedElements, OnSharedElementsReadyListener listener) {
                super.onSharedElementsArrived(sharedElementNames, sharedElements, listener);
                Log.d(TAG, "onSharedElementsArrived: ");
            }
        });
    }

    DecorateRecyclerAdapter microPageAdapter;
    // 初始化首页内容
    private void initMainContent () {
        // 初始化布局管理器
        LinearLayoutManager llm_content = new LinearLayoutManager(mContext);
        llm_content.setOrientation(LinearLayout.VERTICAL);
        microPageContent.setLayoutManager(llm_content);
        SpacesItemDecoration spacesItemDecoration = new SpacesItemDecoration();
        spacesItemDecoration.setMargin(Dp2Px2SpUtil.dp2px(mContext, 20), 0, Dp2Px2SpUtil.dp2px(mContext, 20), 0);
        microPageContent.addItemDecoration(spacesItemDecoration);
        // 初始化适配器
        microPageAdapter = new DecorateRecyclerAdapter(mContext, microPageList);
        microPageContent.setAdapter(microPageAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        // TODO: 设置状态栏颜色
//        if (!isMain) {
//            StatusBarUtil.setStatusBarColor(getActivity(), getResources().getColor(R.color.recent_update_btn_pressed));
//            isMain = true;
//        } else {
//            StatusBarUtil.setStatusBarColor(getActivity(), getResources().getColor(R.color.white));
//            isMain = false;
//        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        if (unbinder != null) {
            unbinder.unbind();
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
    @Subscribe
    public void onEventMainThread(AudioPlayEvent event) {
        switch (event.getState()){
            case AudioPlayEvent.NEXT:
            case AudioPlayEvent.LAST:
            case AudioPlayEvent.PAUSE:
            case AudioPlayEvent.PLAY:
            case AudioPlayEvent.STOP:
                if(microPageAdapter != null){
                    microPageAdapter.notifyDataSetChangedRecentUpdate();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onScrollChanged(int l, int t, int oldl, int oldt) {
        if (!isMain) {
            float alpha = (t / (toolbarHeight * 1.0f)) * 255;
            if(alpha > 255){
                alpha = 255;
            }else if(alpha < 0){
                alpha = 0;
            }
            microPageToolbar.setBackgroundColor(Color.argb((int) alpha,30,89,246));
            if (alpha == 255) {
                microPageToolbarTitle.setVisibility(View.VISIBLE);
                if (hasSearch) {
                    microPageToolbarSearch.setVisibility(View.VISIBLE);
                } else {
                    microPageToolbarSearch.setVisibility(View.GONE);
                }
            } else {
                microPageToolbarTitle.setVisibility(View.GONE);
                microPageToolbarSearch.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onLoadState(int state) {

    }
}