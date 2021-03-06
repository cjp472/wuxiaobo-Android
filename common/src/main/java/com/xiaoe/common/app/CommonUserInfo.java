package com.xiaoe.common.app;

import android.text.TextUtils;

import com.xiaoe.common.db.LoginSQLiteCallback;
import com.xiaoe.common.db.SQLiteUtil;
import com.xiaoe.common.entitys.LoginUser;

import java.util.List;

public class CommonUserInfo {

    private static String apiToken;
    private static String phone;
    private static String userId;
    private static String wxNickname;
    private static String wxAvatar;
    private static String shopId;
    // 是否为超级会员
    private static boolean isSuperVip;
    // 超级会员是否可用
    private static boolean isSuperVipAvailable;
    // 超级会员影响范围
    private static int superVipEffective;

    private static CommonUserInfo commonUserInfo = null;
    private static LoginUser loginUser = null;
    private boolean hasUnreadMsg;
    private int unreadMsgCount;
    private static SQLiteUtil loginSQLiteUtil;

    private CommonUserInfo(){
        loginSQLiteUtil = SQLiteUtil.init(XiaoeApplication.getmContext(), new LoginSQLiteCallback());
        if(!loginSQLiteUtil.tabIsExist(LoginSQLiteCallback.TABLE_NAME_USER)){
            loginSQLiteUtil.execSQL(LoginSQLiteCallback.TABLE_SCHEMA_USER);
        }
    }
    public static CommonUserInfo getInstance(){
        if(commonUserInfo == null){
            synchronized (CommonUserInfo.class){
                if(commonUserInfo == null){
                    commonUserInfo = new CommonUserInfo();

                }
            }
        }
        if(loginUser == null){
            setUserInfo();
        }
        return commonUserInfo;
    }

    public void clearUserInfo(){
        commonUserInfo = null;
    }
    private static void setUserInfo(){
        List<LoginUser> list = loginSQLiteUtil.query(LoginSQLiteCallback.TABLE_NAME_USER, "select * from " + LoginSQLiteCallback.TABLE_NAME_USER, null);
        if(list != null && list.size() > 0){
            loginUser = list.get(0);
        }
    }
    public static void setShopId(String shopId) {
        CommonUserInfo.shopId = shopId;
    }

    public static String getShopId() {
        return shopId;
    }

    public static void setApiToken(String apiToken) {
        CommonUserInfo.apiToken = apiToken;
    }

    public static void setPhone(String phone) {
        CommonUserInfo.phone = phone;
    }

    public static void setUserId(String userId) {
        CommonUserInfo.userId = userId;
    }

    public static void setWxNickname(String wxNickname) {
        CommonUserInfo.wxNickname = wxNickname;
    }

    public static void setWxAvatar(String wxAvatar) {
        CommonUserInfo.wxAvatar = wxAvatar;
    }

    public static String getApiToken() {
        return apiToken;
    }

    public String getApiTokenByDB(){
        //双层判断
        if(loginUser == null){
            setUserInfo();
        }
        if(loginUser != null){
            return loginUser.getApi_token();
        }
        return null;
    }

    public static String getPhone() {
        return phone;
    }

    public static String getUserId() {
        return userId;
    }

    public static String getLoginUserIdOrAnonymousUserId(){
        return TextUtils.isEmpty(userId) ? Constants.ANONYMOUS_USER_ID : userId;
    }

    public String getUserIdByDB(){
        //双层判断
        if(loginUser == null){
            setUserInfo();
        }
        if(loginUser != null){
            return loginUser.getUserId();
        }
        return null;
    }

    public static String getWxNickname() {
        return wxNickname;
    }

    public static String getWxAvatar() {
        return wxAvatar;
    }

    public static void setIsSuperVip(boolean isSuperVip) {
        CommonUserInfo.isSuperVip = isSuperVip;
    }

    public static boolean isIsSuperVip() {
        return isSuperVip;
    }

    public static void setIsSuperVipAvailable(boolean isSuperVipAvailable) {
        CommonUserInfo.isSuperVipAvailable = isSuperVipAvailable;
    }

    public static boolean isIsSuperVipAvailable() {
        return isSuperVipAvailable;
    }

    public boolean isHasUnreadMsg() {
        return hasUnreadMsg;
    }

    public void setHasUnreadMsg(boolean hasUnreadMsg) {
        this.hasUnreadMsg = hasUnreadMsg;
    }

    public void clearLoginUserInfo() {
        loginUser = null;
    }

    public int getUnreadMsgCount() {
        return unreadMsgCount;
    }

    public void setUnreadMsgCount(int unreadMsgCount) {
        this.unreadMsgCount = unreadMsgCount;
    }

    public static void setSuperVipEffective(int superVipEffective) {
        CommonUserInfo.superVipEffective = superVipEffective;
    }

    public static int getSuperVipEffective() {

        return superVipEffective;
    }
}
