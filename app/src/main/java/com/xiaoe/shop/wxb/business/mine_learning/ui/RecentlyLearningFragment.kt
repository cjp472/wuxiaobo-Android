package com.xiaoe.shop.wxb.business.mine_learning.ui

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.facebook.drawee.view.SimpleDraweeView
import com.google.gson.Gson
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import com.xiaoe.common.entitys.GoodsListItem
import com.xiaoe.common.entitys.ItemType.ITEM_TYPE_AUDIO
import com.xiaoe.common.entitys.ItemType.ITEM_TYPE_DEFAULT
import com.xiaoe.common.entitys.RecentlyLearning
import com.xiaoe.common.entitys.ResourceType
import com.xiaoe.common.utils.Dp2Px2SpUtil
import com.xiaoe.network.requests.IRequest
import com.xiaoe.network.requests.RecentlyLearningRequest
import com.xiaoe.shop.wxb.R
import com.xiaoe.shop.wxb.R.id.progress
import com.xiaoe.shop.wxb.base.BaseFragment
import com.xiaoe.shop.wxb.business.mine_learning.presenter.MyBoughtPresenter
import com.xiaoe.shop.wxb.business.search.presenter.SpacesItemDecoration
import com.xiaoe.shop.wxb.utils.LogUtils
import com.xiaoe.shop.wxb.utils.SetImageUriUtil
import com.xiaoe.shop.wxb.utils.UploadLearnProgressManager
import com.xiaoe.shop.wxb.utils.jumpKnowledgeDetail
import com.xiaoe.shop.wxb.widget.StatusPagerView
import kotlinx.android.synthetic.main.fragment_recently_learning.*
import java.lang.Exception

/**
 * Date: 2018/12/26 10:14
 * Author: hans yang
 * Description:
 */
class RecentlyLearningFragment : BaseFragment(), OnRefreshListener, OnLoadMoreListener {
    private val mTag = "RecentlyLearning"
    var pageIndex = 1
    val pageSize = 10

    private val mAdapter: MyAdapter by lazy {
        MyAdapter(activity)
    }

    private val mStatusPagerView: StatusPagerView by lazy {
        StatusPagerView(activity)
    }

    private val mBoughtPresenter : MyBoughtPresenter by lazy {
        MyBoughtPresenter(this)
    }

    private val mSpacesItemDecoration: SpacesItemDecoration by lazy {
        SpacesItemDecoration()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_recently_learning, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
    }

    // 初始化页面数据
    private fun initView() {
        mSpacesItemDecoration.setMargin(0, Dp2Px2SpUtil.dp2px(activity, 16f),
                0, 0)
        var linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL,
                false)

        with(recentlyLearningList) {
            addItemDecoration(mSpacesItemDecoration)
            layoutManager = linearLayoutManager
            adapter = mAdapter
        }
        mStatusPagerView.setLoadingState(View.VISIBLE)
        mAdapter.emptyView = mStatusPagerView

        mBoughtPresenter.requestLearningData(pageIndex, pageSize)
    }

    private fun initListener() {
        with(learningRefresh) {
            setOnRefreshListener(this@RecentlyLearningFragment)
            setOnLoadMoreListener(this@RecentlyLearningFragment)
            setEnableLoadMoreWhenContentNotFull(false)
        }
        mAdapter.setOnItemClickListener { adapter, _, position ->
            val data = adapter.getItem(position) as GoodsListItem
            jumpKnowledgeDetail(activity, data.resourceType, data.resourceId, data.info?.imgUrl)
        }
    }

    override fun onMainThreadResponse(iRequest: IRequest?, success: Boolean, entity: Any?) {
        super.onMainThreadResponse(iRequest, success, entity)
        if (isFragmentDestroy) return

        handleData(success, entity, iRequest)
    }

    private fun handleData(success: Boolean, entity: Any?, iRequest: IRequest?) {
        if (success && entity != null) {
            if (iRequest is RecentlyLearningRequest) {
                mStatusPagerView.setPagerState(StatusPagerView.FAIL,
                        getString(R.string.no_learning_content), R.mipmap.collection_none)
                try {//没有数据的时候data返回数组类型，但是有数据又返回object类型
                    val obj = entity as JSONObject
                    val code = obj.getInteger("code") as Int
                    val dataArray = obj.getJSONArray("data") as JSONArray
                    if (0 == code && (dataArray == null || 0 == dataArray.size)){
                        return
                    }else  loadData(entity)
                }catch (e : Exception){
                    e.printStackTrace()
                    loadData(entity)
                }
            }
        } else {
            doRequestFail()
        }
    }

    private fun loadData(entity: Any?) {
        try {
            val result = Gson().fromJson<RecentlyLearning>(entity!!.toString(),
                    RecentlyLearning::class.java)
            val data = result.data!!.goodsList
            data!!
                    .filter { 4 == it.resourceType }
                    .forEach {
                        data!!.remove(it)
                        LogUtils.d("filter 去掉直播的数据")
                    }

            learningRefresh.finishRefresh()
            when {
                0 == result.code && 1 == pageIndex -> {
                    mAdapter.setNewData(data)
                    learningRefresh.setEnableLoadMore(data!!.size!! >= pageSize)
                }
                0 == result.code -> {
                    mAdapter.addData(data!!)
                    if (data!!.size!! >= pageSize) {
                        learningRefresh.finishLoadMore()
                    } else {
                        learningRefresh.finishLoadMoreWithNoMoreData()
                        learningRefresh.setEnableLoadMore(false)
                    }
                }
                3042 == result.code -> {
                    Toast.makeText(activity, result!!.msg, Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            doRequestFail()
        }
    }

    private fun doRequestFail() {
        learningRefresh.finishRefresh()
        learningRefresh.finishLoadMore()
        mStatusPagerView.setPagerState(StatusPagerView.FAIL, StatusPagerView.FAIL_CONTENT,
                R.mipmap.error_page)
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        pageIndex = 1
        mBoughtPresenter.requestLearningData(pageIndex, pageSize)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        mBoughtPresenter.requestLearningData(++pageIndex, pageSize)
    }

    class MyAdapter(val context: Context) : BaseMultiItemQuickAdapter<GoodsListItem,
            BaseViewHolder>(null) {

        init {
            addItemType(ITEM_TYPE_DEFAULT, R.layout.recently_learning_list_item)
            addItemType(ITEM_TYPE_AUDIO, R.layout.audio_learning_list_item)
        }

        override fun convert(helper: BaseViewHolder?, item: GoodsListItem?) {
            helper?.run {
                item?.apply {
                    when (itemViewType) {
                        ITEM_TYPE_DEFAULT -> {
                            setGone(R.id.learningProgress,false)
                            setText(R.id.itemTitle, info?.title)
//                            getView<SimpleDraweeView>(R.id.itemIcon).setImageURI(info?.imgUrl)
                            val itemIcon = getView<SimpleDraweeView>(R.id.itemIcon)
                            SetImageUriUtil.setImgURI(itemIcon, info?.imgUrl,
                                    Dp2Px2SpUtil.dp2px(mContext, 144f),
                                    Dp2Px2SpUtil.dp2px(mContext, 108f))

                            var descString = ""
                            when (resourceType) {
                                ResourceType.TYPE_TEXT ->{// 图文
                                    val learnProgress = UploadLearnProgressManager
                                            .parseSingleItemLearnProgress(orgLearnProgress)
                                    learnProgress?.apply {
                                        descString = if (process >= 100)  context.getString(R.string.learned_finish)
                                                     else ""
                                    }
                                }
                                ResourceType.TYPE_AUDIO,ResourceType.TYPE_VIDEO ->{// 音频，视频
                                    val learnProgress = UploadLearnProgressManager
                                            .parseSingleItemLearnProgress(orgLearnProgress)
                                    learnProgress?.apply {
                                        val time = playTime - playTime * process / 100
                                        descString = when{
                                            0 == time && playTime > 0 -> context.getString(R.string.learned_finish)
                                            time > 0 && playTime > 0 -> String.format("剩余%d分%d秒",time/60,time % 60)
                                            else -> ""
                                        }
                                    }
                                }
//                                ResourceType.TYPE_VIDEO ->{ // 视频
//
//                                }
                                ResourceType.TYPE_MEMBER ->{// 会员
                                    descString =context.getString(R.string.membership_due)
                                    if (1 != info.isExpire){
                                        descString = updateLearnProgress(helper!!, item!!, descString)
                                    }
                                    setText(R.id.itemContent,String.format(context
                                            .getString(R.string.stages_text), info.periodicalCount))
                                }
                                ResourceType.TYPE_COLUMN,
                                ResourceType.TYPE_BIG_COLUMN ->{// 专栏 、大专栏
                                    descString = context.getString(R.string.learned_finish)
                                    if (1 != isFinish){
                                        descString = updateLearnProgress(helper!!, item!!, descString)
                                    }
                                }
//                                ResourceType.TYPE_BIG_COLUMN ->{
//                                } // 大专栏
                            }
                            setText(R.id.itemDesc,descString)
                        }
                        ITEM_TYPE_AUDIO -> {
                            SetImageUriUtil.setImgURI(helper!!.getView(R.id.itemIcoBbg),
                                    "res:///" + R.mipmap.audio_list_bg, Dp2Px2SpUtil.dp2px(mContext,
                                    160f), Dp2Px2SpUtil.dp2px(mContext, 120f))
                            val url = if (TextUtils.isEmpty(info?.imgUrl)) "res:///" + R.mipmap.detail_disk
                            else info?.imgUrl
                            val imageWidthDp = 84f
                            val itemIcon = helper!!.getView<SimpleDraweeView>(R.id.itemIcon)
                            if (url.contains("res:///") || !SetImageUriUtil.isGif(url)) {// 本地图片
                                SetImageUriUtil.setImgURI(itemIcon, url, Dp2Px2SpUtil.dp2px(mContext,
                                        imageWidthDp), Dp2Px2SpUtil.dp2px(mContext, imageWidthDp))
                            } else {// 网络图片
                                SetImageUriUtil.setRoundAsCircle(itemIcon, Uri.parse(url))
                            }
                            setText(R.id.itemTitle, info?.title)

                            var descString = ""
                            val learnProgress = UploadLearnProgressManager
                                    .parseSingleItemLearnProgress(orgLearnProgress)
                            learnProgress?.run {
                                val time = playTime - playTime * process / 100
                                descString = when{
                                    0 == time && playTime > 0 -> context.getString(R.string.learned_finish)
                                    time > 0 && playTime > 0 -> String.format("剩余%d分%d秒",time/60,time % 60)
                                    else -> ""
                                }
                            }
                            setText(R.id.itemDesc,descString)
                        }
                    }
                }
            }
        }

        private fun updateLearnProgress(baseViewHolder: BaseViewHolder, goodsListItem: GoodsListItem, descString: String): String {
            var descString1 = descString
            baseViewHolder.setGone(R.id.learningProgress, true)
            val progress = UploadLearnProgressManager.
                    parseUploadHistory(goodsListItem.orgLearnProgress).size * 100 / goodsListItem.info?.periodicalCount
            baseViewHolder.setProgress(R.id.learningProgress, progress)
            descString1 = String.format(context.getString(R.string.learned_sessinos_and_total),
                    UploadLearnProgressManager.parseUploadHistory(goodsListItem.orgLearnProgress).size
                    , goodsListItem.info?.periodicalCount)
            return descString1
        }
    }
}