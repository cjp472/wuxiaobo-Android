package xiaoe.com.shop.business.earning.ui;

import android.content.ClipData;
import android.content.ClipData.Item;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import xiaoe.com.common.app.CommonUserInfo;
import xiaoe.com.common.utils.Dp2Px2SpUtil;
import xiaoe.com.network.NetworkCodes;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.network.requests.SettingPseronMsgRequest;
import xiaoe.com.network.requests.WithDrawalRequest;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.XiaoeActivity;
import xiaoe.com.shop.business.earning.presenter.EarningPresenter;
import xiaoe.com.shop.common.JumpDetail;
import xiaoe.com.shop.interfaces.OnConfirmListener;
import xiaoe.com.shop.utils.OSUtils;
import xiaoe.com.shop.utils.StatusBarUtil;

// 提现页面
public class WithdrawalActivity extends XiaoeActivity {

    private static final String TAG = "WithdrawalActivity";

    @BindView(R.id.wr_wrap)
    LinearLayout wrWrap;

    @BindView(R.id.title_back)
    ImageView wrBack;
    @BindView(R.id.title_content)
    TextView wrTitle;
    @BindView(R.id.title_end)
    TextView wrDesc;

    @BindView(R.id.wr_page_money)
    EditText wrInput;
    @BindView(R.id.wr_page_error_tip)
    TextView wrErrorTip;
    @BindView(R.id.wr_page_balance)
    TextView wrBalance; // 余额
    @BindView(R.id.wr_page_all_take)
    TextView wrAll;
    @BindView(R.id.wr_page_now)
    TextView wrNow;
    @BindView(R.id.wr_page_limit_phone)
    TextView wrLimitPhone;

    Unbinder unbinder;

//    float balance; // 余额
    String balance; // 余额（字符串）
    double balanceNum; // 余额（数字）
    Intent intent;

    EarningPresenter earningPresenter;
    String realName;
    double resultPrice;
    // 软键盘
    InputMethodManager imm;
    AlertDialog dialog;
    // 剪贴板
    ClipboardManager clipboardManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setStatusBar();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdrawal);

        unbinder = ButterKnife.bind(this);

        wrWrap.setPadding(0, StatusBarUtil.getStatusBarHeight(this), 0, 0);
        intent = getIntent();
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        balance = intent.getStringExtra("allMoney");

        if (!"".equals(balance)) {
            try {
                balanceNum = Double.parseDouble(balance);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else {
            balanceNum = 0;
        }

        earningPresenter = new EarningPresenter(this);
        initData();
        initListener();
    }

    private void initData() {
        wrTitle.setText("提现");
        wrDesc.setVisibility(View.GONE);
        wrBalance.setText(String.format(getResources().getString(R.string.withdrawal_left), balance));
        String localPhone = CommonUserInfo.getPhone();
        char[] phoneArr = localPhone.toCharArray();
        for (int i = 0; i < phoneArr.length; i++) {
            if (i >= 3 && i <=8) {
                phoneArr[i] = '*';
            }
        }
        String limitPhone = String.valueOf(phoneArr);
        wrLimitPhone.setText(limitPhone);
    }

    private void initListener() {
        wrWrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSoftKeyboard();
            }
        });
        wrBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        wrAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wrInput.setText(String.valueOf(balance));
                wrInput.setSelection(wrInput.getText().toString().length());
            }
        });
        wrInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                wrErrorTip.setVisibility(View.GONE);
                // 限制输入两位小数
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        s = s.toString().subSequence(0,
                                s.toString().indexOf(".") + 3);
                        wrInput.setText(s);
                        wrInput.setSelection(s.length());
                    }
                }

                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    wrInput.setText(s);
                    wrInput.setSelection(2);
                }

                if (s.toString().startsWith("0") && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        wrInput.setText(s.subSequence(0, 1));
                        wrInput.setSelection(1);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        wrNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("".equals(wrInput.getText().toString())) {
                    return;
                }
                double price = Double.parseDouble(wrInput.getText().toString());
                // 点击时隐藏
                wrErrorTip.setVisibility(View.GONE);
                if (price <= balanceNum) {
                    if (price > 20000) { // 大于 20000 分批提现

                        getDialog().setMessageVisibility(View.GONE);
                        getDialog().setTitle(getString(R.string.withdrawal_limit_content));
                        getDialog().getTitleView().setPadding(Dp2Px2SpUtil.dp2px(WithdrawalActivity.this,46),
                                0,Dp2Px2SpUtil.dp2px(WithdrawalActivity.this,46),0);
                        getDialog().setConfirmText("知道了");
                        getDialog().setConfirmTextColor(getResources().getColor(R.color.recent_list_color));
                        getDialog().setHideCancelButton(true);
                        getDialog().showDialog(1);

                    } else if (price > 0 && price < 10) { // 至少 10 块钱才能提现
                        wrErrorTip.setText(R.string.withdrawal_less_tip);
                        wrErrorTip.setVisibility(View.VISIBLE);
                    } else {
                        resultPrice = price * 100;
                        earningPresenter.requestWithdrawal(resultPrice, OSUtils.getIP(WithdrawalActivity.this));
                    }
                } else {
                    wrErrorTip.setText(R.string.withdrawal_error_tip);
                    wrErrorTip.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        super.onMainThreadResponse(iRequest, success, entity);
        JSONObject result = (JSONObject) entity;
        if (result == null) {
            return;
        }
        if (success) {
            if (iRequest instanceof WithDrawalRequest) {
                int code = result.getInteger("code");
                if (code == NetworkCodes.CODE_SUCCEED) {
                    Log.d(TAG, "onMainThreadResponse: 提现提交成功...");
                    JumpDetail.jumpWrResult(WithdrawalActivity.this, String.valueOf(resultPrice / 100));
                } else if (code == NetworkCodes.CODE_OPEN_ID_ERROR) {

                    Log.d(TAG, "onMainThreadResponse: msg --- " + result.getString("msg"));
                    JSONObject data = (JSONObject) result.get("data");

                    if (data != null) {
                        String url = data.getString("h5_url");
                        ClipData clipData = ClipData.newPlainText(null, url);
                        getDialog().setTitle(getString(R.string.withdrawal_grant_title));
                        getDialog().getTitleView().setTextSize(20);
                        getDialog().getTitleView().setTextColor(getResources().getColor(R.color.main_title_color));
                        getDialog().getTitleView().setGravity(Gravity.START);
                        getDialog().getTitleView().setPadding(Dp2Px2SpUtil.dp2px(this, 20), 0, 0, 0);
                        getDialog().setHintMessage(getString(R.string.withdrawal_grant_content), url);
                        getDialog().setConfirmListener((view, tag) -> {
                            try {
                                Intent intent = new Intent(Intent.ACTION_MAIN);
                                ComponentName componentName = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
                                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.setComponent(componentName);
                                clipboardManager.setPrimaryClip(clipData);
                                startActivity(intent);
                            } catch (Exception e) {
                                Toast("未安装微信呦");
                            }
                        });
                        getDialog().showDialog(0);
                    } else {
                        Log.d(TAG, "onMainThreadResponse: 接口返回有问题，data 为空了");
                    }

                } else {
                    Log.d(TAG, "onMainThreadResponse: 提现失败...");
                    String msg = result.getString("msg");
                    if (!TextUtils.isEmpty(msg)) {
                        Toast(msg);
                    }
                }
            }
        } else {
            Log.d(TAG, "onMainThreadResponse: request fail...");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    /**
     * 如果软键盘弹出，就关闭软键盘
     */
    private void toggleSoftKeyboard() {
        if (imm != null && imm.isActive()) {
            View view = getCurrentFocus();
            if (view != null) {
                IBinder iBinder = view.getWindowToken();
                imm.hideSoftInputFromWindow(iBinder, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
}
