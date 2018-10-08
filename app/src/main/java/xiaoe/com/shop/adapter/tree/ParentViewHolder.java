package xiaoe.com.shop.adapter.tree;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import xiaoe.com.common.entitys.ItemData;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseViewHolder;
import xiaoe.com.shop.interfaces.ItemDataClickListener;
import xiaoe.com.shop.widget.RecycleViewDivider;

/**
 * @Author Zheng Haibo
 * @PersonalWebsite http://www.mobctrl.net
 * @Description
 */
public class ParentViewHolder extends BaseViewHolder {
	private String TAG = "ParentViewHolder";
	private Context mContext;
	private TextView text;
	private RecyclerView childRecyclerView;
	private TreeChildRecyclerAdapter treeChildRecyclerAdapter;
	private final View divisionLine;
	private final LinearLayout btnExpandDown;
    private final LinearLayout btnPlayAll;
    private final LinearLayout btnExpandTop;

	public ParentViewHolder(Context context, View itemView) {
		super(itemView);
		mContext = context;
		text = (TextView) itemView.findViewById(R.id.text);
		//列表控件
		childRecyclerView = (RecyclerView) itemView.findViewById(R.id.tree_child_recycler_view);
		LinearLayoutManager layoutManager = new LinearLayoutManager(context);
		layoutManager.setAutoMeasureEnabled(true);
		childRecyclerView.setLayoutManager(layoutManager);
		//item分割线
		childRecyclerView.addItemDecoration(new RecycleViewDivider(context, LinearLayoutManager.HORIZONTAL, R.drawable.imaginary_line));
		childRecyclerView.setNestedScrollingEnabled(false);
		treeChildRecyclerAdapter = new TreeChildRecyclerAdapter(context);
		childRecyclerView.setAdapter(treeChildRecyclerAdapter);
		//分割线
		divisionLine = itemView.findViewById(R.id.division_line);
		//展开按钮
		btnExpandDown = (LinearLayout) itemView.findViewById(R.id.expand_down_btn);
		//收起按钮
        btnExpandTop = (LinearLayout) itemView.findViewById(R.id.expand_top_btn);
		//播放全部按钮
        btnPlayAll = (LinearLayout) itemView.findViewById(R.id.play_all_btn);
	}

	public void bindView(final ItemData itemData, final int position,
						 final ItemDataClickListener imageClickListener) {
		text.setText(itemData.getText());
		if (itemData.isExpand()) {
			divisionLine.setVisibility(View.VISIBLE);
			childRecyclerView.setVisibility(View.VISIBLE);
			setExpandState(View.GONE);
			setPlayButtonState(View.VISIBLE);
		}else{
			divisionLine.setVisibility(View.GONE);
			childRecyclerView.setVisibility(View.GONE);
			setExpandState(View.VISIBLE);
			setPlayButtonState(View.GONE);
		}
		btnPlayAll.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(mContext, "正在播放全部", Toast.LENGTH_SHORT).show();
			}
		});
		treeChildRecyclerAdapter.setData(itemData.getChildren());
		//展开点击事件
		btnExpandDown.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (imageClickListener != null) {
                    imageClickListener.onExpandChildren(itemData);
                    itemData.setExpand(true);
                    divisionLine.setVisibility(View.VISIBLE);
                    childRecyclerView.setVisibility(View.VISIBLE);
                    setExpandState(View.GONE);
                    setPlayButtonState(View.VISIBLE);
				}
			}
		});
        //收起点击事件
        btnExpandTop.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (imageClickListener != null) {
                    imageClickListener.onHideChildren(itemData);
                    itemData.setExpand(false);
                    divisionLine.setVisibility(View.GONE);
                    childRecyclerView.setVisibility(View.GONE);
                    setExpandState(View.VISIBLE);
                    setPlayButtonState(View.GONE);
                }
            }
        });
	}
	private void setExpandState(int visibility){
		btnExpandDown.setVisibility(visibility);
		btnExpandTop.setVisibility(visibility == View.VISIBLE ? View.GONE : View.VISIBLE);
	}
	private void setPlayButtonState(int visibility){
        btnPlayAll.setVisibility(visibility);
    }
}