package xiaoe.com.shop.base;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.List;

import xiaoe.com.common.app.Global;
import xiaoe.com.common.entitys.LoginUser;
import xiaoe.com.common.entitys.LoginUserInfo;
import xiaoe.com.common.utils.SQLiteUtil;
import xiaoe.com.network.network_interface.INetworkResponse;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.shop.R;
import xiaoe.com.shop.business.login.presenter.LoginSQLiteCallback;


/**
 * Created by Administrator on 2017/7/17.
 * <br>des：fragment 基类
 */

public class BaseFragment extends Fragment implements INetworkResponse {
    private static PopupWindow popupWindow;
    private TextView mToastText;
    protected boolean isFragmentDestroy = false;

    // Fragment 懒加载字段
    private boolean isFragmentVisible;
    private boolean isReuseView;
    private boolean isFirstVisible;
    private View rootView;
    private Handler mHandler = new BfHandler(this);

    static class BfHandler extends Handler {

        WeakReference<BaseFragment> wrf;

        BfHandler (BaseFragment baseFragment) {
            this.wrf = new WeakReference<>(baseFragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (popupWindow != null) {
                popupWindow.dismiss();
            }
        }
    }

    // 登录的信息
    List<LoginUser> userList;
    List<LoginUserInfo> userInfoList;

    // 用户登录信息
    LoginUser user = null;
    // 用户详细信息
    LoginUserInfo userInfo = null;

    // 这个方法除了 Fragment 的可见状态发生变化时会被回调外，创建 Fragment 实例时候也会调用
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        // 若在生命周期之外调用就 return
        if (rootView == null) {
            return;
        }
        if (isFirstVisible && isVisibleToUser) {
            onFragmentFirstVisible();
            isFirstVisible = false;
        }
        if (isVisibleToUser) {
            onFragmentVisibleChange(true);
        }
        if (isFragmentVisible) {
            isFragmentVisible = false;
            onFragmentVisibleChange(false);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 初始化数据库
        SQLiteUtil.init(getActivity().getApplicationContext(), new LoginSQLiteCallback());
        // 如果表不存在，就去创建
        if (!SQLiteUtil.tabIsExist(LoginSQLiteCallback.TABLE_NAME_USER)) {
            SQLiteUtil.execSQL(LoginSQLiteCallback.TABLE_SCHEMA_USER);
        }
        if (!SQLiteUtil.tabIsExist(LoginSQLiteCallback.TABLE_NAME_USER_INFO)) {
            SQLiteUtil.execSQL(LoginSQLiteCallback.TABLE_SCHEMA_USER_INFO);
        }

        userList = SQLiteUtil.query(LoginSQLiteCallback.TABLE_NAME_USER, "select * from " + LoginSQLiteCallback.TABLE_NAME_USER, null);
        userInfoList = SQLiteUtil.query(LoginSQLiteCallback.TABLE_NAME_USER_INFO, "select * from " + LoginSQLiteCallback.TABLE_NAME_USER_INFO, null);
        if (userList.size() == 1) {
            user = userList.get(0);
        }
        if (userInfoList.size() == 1) {
            userInfo = userInfoList.get(0);
        }

        initVariable();
    }

    // 获取登录 api_token，为空表示没有登录态
    protected String getLoginApiToke() {
        if (user != null) {
            return user.getApi_token();
        } else {
            return "";
        }
    }

    // 获取登录手机，为空表示没有登录态
    protected String getLoginPhone() {
        if (user != null && userInfo != null) {
            return userInfo.getPhone();
        } else {
            return "";
        }
    }

    // 获取微信昵称，为空表示没有登录态
    protected String getWxNickname() {
        if (user != null && userInfo != null) {
            return userInfo.getWxNickname();
        } else {
            return "";
        }
    }

    // 获取微信头像，为空表示没有登录态
    protected String getWxAvatar() {
        if (user != null && userInfo != null) {
            return userInfo.getWxAvatar();
        } else {
            return "";
        }
    }

    // 获取登录账号的店铺 id，为空表示没有登录态
    protected String getShopId() {
        if (user != null && userInfo != null) {
            return userInfo.getShopId();
        } else {
            return "";
        }
    }

    // 获取登录用户登录信息集合
    protected List<LoginUser> getLoginUserList() {
        return userList;
    }

    // 获取登录用户详细信息集合
    protected List<LoginUserInfo> getLoginUserInfoList() {
        return userInfoList;
    }

    // 获取登录用户
    protected LoginUser getLoginUser() {
        return user;
    }

    // 获取登录用户信息
    protected LoginUserInfo getLoginUserInfo() {
        return userInfo;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // 懒加载的初始化
        if (rootView == null) {
            rootView = view;
            if (getUserVisibleHint()) {
                if (isFirstVisible) {
                    onFragmentFirstVisible();
                    isFirstVisible = false;
                }
                onFragmentVisibleChange(true);
                isFragmentVisible = true;
            }
        }
        super.onViewCreated(isReuseView ? rootView : view, savedInstanceState);
        // toast 的初始化
        popupWindow = new PopupWindow(getContext());
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        View pView = LayoutInflater.from(getContext()).inflate(R.layout.layout_custom_toast, null,false);
        mToastText = (TextView) pView.findViewById(R.id.id_toast_text);
        popupWindow.setContentView(pView);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.setOutsideTouchable(false);
        popupWindow.setFocusable(true);
        isFragmentDestroy = false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResponse(final IRequest iRequest, final boolean success, final Object entity) {
        if(!success && entity instanceof String){
            if(((String)entity).equals("user login time out")){
                return;
            }
        }
        if(!isFragmentDestroy){
            Global.g().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onMainThreadResponse(iRequest,success,entity);
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isFragmentDestroy = true;
        if(popupWindow!= null){
            popupWindow.dismiss();
            popupWindow = null;
        }
        if(mHandler != null){
            mHandler.removeCallbacksAndMessages(null);
        }
        mHandler = null;
        initVariable();
    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {

    }

    public void toastCustom(String msg){
        mHandler.removeMessages(0);
        mToastText.setText(msg);
        popupWindow.showAtLocation(getActivity().getWindow().getDecorView(), Gravity.CENTER, 0, 0);
        mHandler.sendEmptyMessageDelayed(0,1500);
    }

    /**
     * 在弹出对话框时，显示popupWindow，则popupWindow会在对话框下面（被对话框盖住）
     * 从方法是让popupWindow显示在对话上面
     * view 是对话框中的view
     */
    public void toastCustomDialog(View view,String msg){
        mHandler.removeMessages(0);
        mToastText.setText(msg);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        mHandler.sendEmptyMessageDelayed(0,1500);
    }

    // fragment 首次可见时回调，可进行网络加载
    protected void onFragmentFirstVisible () {

    }

    // 去除 setUserVisibleHint 多余的回调，只有当 fragment 可见状态发生变化才回调
    // 可以进行 ui 的操作
    protected void onFragmentVisibleChange (boolean isVisible) {

    }

    protected boolean isFragmentVisible () {
        return isFragmentVisible;
    }

    // 设置是否使用 view 的复用，默认开启，view 的复用可以防止重复创建 view
    protected void reuseView (boolean isReuse) {
        isReuseView = isReuse;
    }

    private void initVariable () {
        isFirstVisible = true;
        isFragmentVisible = false;
        rootView = null;
        isReuseView = true;
    }
}
