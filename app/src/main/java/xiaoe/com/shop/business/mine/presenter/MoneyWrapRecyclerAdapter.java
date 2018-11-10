package xiaoe.com.shop.business.mine.presenter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.List;

import xiaoe.com.common.entitys.MineMoneyItemInfo;
import xiaoe.com.common.interfaces.OnItemClickWithMoneyItemListener;
import xiaoe.com.common.utils.Dp2Px2SpUtil;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseViewHolder;

public class MoneyWrapRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private static final String TAG = "MoneyWrapRecyclerAdapte";

    private Context mContext;
    private List<MineMoneyItemInfo> mItemList;

    private OnItemClickWithMoneyItemListener onItemClickWithMoneyItemListener;

    public void setOnItemClickWithMoneyItemListener(OnItemClickWithMoneyItemListener listener) {
        this.onItemClickWithMoneyItemListener = listener;
    }

    public MoneyWrapRecyclerAdapter(Activity activity, List<MineMoneyItemInfo> itemList) {
        this.mContext = activity;
        this.mItemList = itemList;
    }

    public MoneyWrapRecyclerAdapter(Context context, List<MineMoneyItemInfo> itemList) {
        this.mContext = context;
        this.mItemList = itemList;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.money_item, null);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int left = Dp2Px2SpUtil.dp2px(parent.getContext(), 25);
        int top = Dp2Px2SpUtil.dp2px(parent.getContext(), 15);
        int right = Dp2Px2SpUtil.dp2px(parent.getContext(), 25);
        int bottom = Dp2Px2SpUtil.dp2px(parent.getContext(), 12);
        layoutParams.setMargins(left, top, right, bottom);
        view.setLayoutParams(layoutParams);
        return new MoneyItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int currentPos = holder.getAdapterPosition();
        final MineMoneyItemInfo currentItem = mItemList.get(currentPos);
        MoneyItemViewHolder viewHolder = (MoneyItemViewHolder) holder;
        viewHolder.money_item_title.setText(currentItem.getItemTitle());
        viewHolder.money_item_desc.setText(currentItem.getItemDesc());
        viewHolder.money_item_wrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickWithMoneyItemListener != null) {
                    onItemClickWithMoneyItemListener.onMineMoneyItemInfoClickListener(v, currentItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItemList == null ? 0 : mItemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
}
