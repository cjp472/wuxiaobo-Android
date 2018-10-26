package xiaoe.com.shop.business.mine_learning.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import xiaoe.com.common.app.Global;
import xiaoe.com.common.entitys.ComponentInfo;
import xiaoe.com.common.entitys.DecorateEntityType;
import xiaoe.com.common.entitys.KnowledgeCommodityItem;
import xiaoe.com.common.utils.Dp2Px2SpUtil;
import xiaoe.com.network.NetworkCodes;
import xiaoe.com.network.requests.CollectionListRequest;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.shop.R;
import xiaoe.com.shop.adapter.decorate.DecorateRecyclerAdapter;
import xiaoe.com.shop.adapter.decorate.knowledge_commodity.KnowledgeListAdapter;
import xiaoe.com.shop.base.XiaoeActivity;
import xiaoe.com.shop.business.mine_learning.presenter.MineLearningPresenter;
import xiaoe.com.shop.business.search.presenter.SpacesItemDecoration;
import xiaoe.com.shop.utils.CollectionUtils;
import xiaoe.com.shop.utils.StatusBarUtil;
import xiaoe.com.shop.widget.StatusPagerView;

public class MineLearningActivity extends XiaoeActivity {

    private static final String TAG = "MineLearningActivity";

    private String pageTitle;

    @BindView(R.id.learning_back)
    ImageView learningBack;
    @BindView(R.id.learning_title)
    TextView learningTitle;
    @BindView(R.id.learning_list)
    RecyclerView learningList;
    @BindView(R.id.learning_loading)
    StatusPagerView learningLoading;

    // 我正在学请求
    MineLearningPresenter mineLearningPresenter;
    // 收藏请求
    CollectionUtils collectionUtils;

    List<ComponentInfo> pageList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_learning);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        pageTitle = intent.getStringExtra("pageTitle");
        //状态栏颜色字体(白底黑字)修改 Android6.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setStatusBarColor(getWindow(), Color.parseColor(Global.g().getGlobalColor()), View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            Global.g().setGlobalColor("#000000");
            StatusBarUtil.setStatusBarColor(getWindow(), Color.parseColor(Global.g().getGlobalColor()), View.SYSTEM_UI_FLAG_VISIBLE);
        }

        initPageData();
        initListener();
    }

    // 初始化页面数据
    private void initPageData() {
        pageList = new ArrayList<>();
        switch (pageTitle) {
            case "我的收藏":
                learningTitle.setText(pageTitle);
                collectionUtils = new CollectionUtils(this);
                collectionUtils.requestCollectionList(1, 10);
                learningLoading.setVisibility(View.VISIBLE);
                learningLoading.setLoadingState(View.VISIBLE);
                break;
            case "我正在学":
                learningTitle.setText(pageTitle);
//            MineLearningPresenter mineCollectionPresenter = new MineLearningPresenter(this, "我正在学的接口");
//            mineCollectionPresenter.requestSearchResult();
                // 显示假数据
                initTempData();
                break;
            default:
                // 其他情况处理（没传 title）
                Log.d(TAG, "initPageData: 没传 title");
                learningLoading.setHintStateVisibility(View.VISIBLE);
                learningLoading.setLoadingState(View.GONE);
                learningLoading.setStateImage(R.mipmap.collection_none);
                learningLoading.setStateText("暂无收藏内容，快去首页逛逛吧");
                break;
        }
    }

    // 我正在学临时数据
    private void initTempData() {
        // 我的收藏页面使用的是只是商品的列表组件，所以可以直接用列表组件
        List<KnowledgeCommodityItem> listItems = new ArrayList<>();
        // 有价格没买
        KnowledgeCommodityItem listItem_1 = new KnowledgeCommodityItem();
        listItem_1.setItemTitle("我的财富计划新中产必修理财课程");
        listItem_1.setItemImg("http://img05.tooopen.com/images/20141020/sy_73154627197.jpg");
        listItem_1.setItemPrice("￥999.99");
        listItem_1.setHasBuy(false);
        listItem_1.setItemDesc("已更新至135期");
        // 有价格买了
        KnowledgeCommodityItem listItem_2 = new KnowledgeCommodityItem();
        listItem_2.setItemTitle("我的财富计划新中产必修理财课程杀杀杀");
        listItem_2.setItemImg("http://img.zcool.cn/community/01f39a59a7affba801211d25185cd3.jpg@1280w_1l_2o_100sh.jpg");
        listItem_2.setItemPrice("￥85.55");
        listItem_2.setHasBuy(true);
        listItem_2.setItemDesc("已更新至120期");
        // 没有价格，免费的
        KnowledgeCommodityItem listItem_3 = new KnowledgeCommodityItem();
        listItem_3.setItemTitle("我的财富计划新中产必修理财课程哒哒哒");
        listItem_3.setItemImg("http://img.zcool.cn/community/01951d55dd8f336ac7251df845a2ae.jpg");
        listItem_3.setItemDesc("已更新至101期");
        listItems.add(listItem_1);
        listItems.add(listItem_2);
        listItems.add(listItem_3);
        listItems.add(listItem_1);
        listItems.add(listItem_2);
        listItems.add(listItem_3);

        List<ComponentInfo> componentInfos = new ArrayList<>();
        ComponentInfo componentInfo = new ComponentInfo();
        componentInfo.setType(DecorateEntityType.KNOWLEDGE_COMMODITY_STR);
        componentInfo.setSubType(DecorateEntityType.KNOWLEDGE_LIST);
        componentInfo.setHideTitle(true);
        componentInfo.setKnowledgeCommodityItemList(listItems);
        componentInfos.add(componentInfo);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        learningList.setLayoutManager(layoutManager);
        DecorateRecyclerAdapter decorateRecyclerAdapter = new DecorateRecyclerAdapter(this, componentInfos);
        learningList.setAdapter(decorateRecyclerAdapter);
    }

    private void initListener() {
        learningBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        super.onMainThreadResponse(iRequest, success, entity);
        JSONObject result = (JSONObject) entity;
        if (success) {
            if (iRequest instanceof CollectionListRequest) {
                int code = result.getInteger("code");
                JSONObject data = (JSONObject) result.get("data");
                if (code == 0) {
                    initCollectionPageData(data);
                } else if (code == NetworkCodes.CODE_COLLECT_LIST_FAILED) {
                    Log.d(TAG, "onMainThreadResponse: 获取收藏列表失败");
                }
            }
        } else {
            Log.d(TAG, "onMainThreadResponse: 请求失败");
            onBackPressed();
        }
    }

    // 初始化收藏页数据
    private void initCollectionPageData(JSONObject data) {
        JSONArray goodsList = (JSONArray) data.get("goods_list");
        List<KnowledgeCommodityItem> itemList = new ArrayList<>();
        if (goodsList == null) {
            // 收藏列表为空，显示为空的页面
            learningLoading.setHintStateVisibility(View.VISIBLE);
            learningLoading.setLoadingState(View.GONE);
            learningLoading.setStateImage(R.mipmap.collection_none);
            learningLoading.setStateText("暂无收藏内容，快去首页逛逛吧");
            return;
        }
        ComponentInfo knowledgeList = new ComponentInfo();
        knowledgeList.setTitle("");
        knowledgeList.setType(DecorateEntityType.KNOWLEDGE_COMMODITY_STR);
        knowledgeList.setSubType(DecorateEntityType.KNOWLEDGE_LIST);
        // 隐藏 title
        knowledgeList.setHideTitle(true);
        // 因为这个接口拿到的 resourceType 是 int 类型，转成字符串存起来
        for (Object goodItem : goodsList) {
            JSONObject goodJsonItem = (JSONObject) ((JSONObject) goodItem).get("info");
            KnowledgeCommodityItem item = new KnowledgeCommodityItem();
            String resourceId = goodJsonItem.getString("goods_id");
            String resourceType = convertInt2Str(goodJsonItem.getInteger("goods_type"));
            String title = goodJsonItem.getString("title");
            String imgUrl = goodJsonItem.getString("img_url");
            // 专栏、会员、大专栏的简介
            String desc = goodJsonItem.getString("org_summary");
            int price = goodJsonItem.getInteger("price");
            // 收藏的都存了价格，没有存是否买
            String priceStr;
            if (price > 0) {
                priceStr = "￥" + price;
            } else {
                priceStr = "";
            }
            // 收藏没有保存描述和人数
            item.setItemImg(imgUrl);
            item.setItemTitle(title);
            item.setResourceId(resourceId);
            item.setSrcType(resourceType);
            item.setItemTitleColumn(desc);
            item.setItemPrice(priceStr);
            itemList.add(item);
        }
        knowledgeList.setKnowledgeCommodityItemList(itemList);
        pageList.add(knowledgeList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        learningList.setLayoutManager(layoutManager);
        DecorateRecyclerAdapter decorateRecyclerAdapter = new DecorateRecyclerAdapter(this, pageList);
        learningList.setAdapter(decorateRecyclerAdapter);
        SpacesItemDecoration spacesItemDecoration = new SpacesItemDecoration();
        spacesItemDecoration.setMargin(
                Dp2Px2SpUtil.dp2px(this, 20),
                Dp2Px2SpUtil.dp2px(this, 0),
                Dp2Px2SpUtil.dp2px(this, 20),
                Dp2Px2SpUtil.dp2px(this, 0));
        learningList.addItemDecoration(spacesItemDecoration);
        learningLoading.setVisibility(View.GONE);
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
