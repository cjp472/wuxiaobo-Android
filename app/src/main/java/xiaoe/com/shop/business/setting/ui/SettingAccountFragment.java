package xiaoe.com.shop.business.setting.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.suke.widget.SwitchButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import xiaoe.com.common.entitys.SettingItemInfo;
import xiaoe.com.common.interfaces.OnItemClickWithSettingItemInfoListener;
import xiaoe.com.common.utils.Dp2Px2SpUtil;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseFragment;
import xiaoe.com.shop.business.setting.presenter.LinearDividerDecoration;
import xiaoe.com.shop.business.setting.presenter.SettingRecyclerAdapter;
import xiaoe.com.shop.business.setting.presenter.SettingTimeCount;
import xiaoe.com.shop.common.JumpDetail;
import xiaoe.com.shop.utils.JudgeUtil;
import xiaoe.com.shop.widget.CodeVerifyView;

public class SettingAccountFragment extends BaseFragment implements OnItemClickWithSettingItemInfoListener {

    private static final String TAG = "SettingAccountFragment";

    private Unbinder unbinder;
    private Context mContext;
    private boolean destroyView = false;
    protected View viewWrap;

    private int layoutId = -1;
    private List<SettingItemInfo> itemList;

    private SettingAccountActivity settingAccountActivity;

    RecyclerView accountRecycler; // 账号设置 recycler
    RecyclerView aboutContent; // 关于 recycler

    // 软键盘
    InputMethodManager imm;

    public static SettingAccountFragment newInstance(int layoutId) {
        SettingAccountFragment settingAccountFragment = new SettingAccountFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("layoutId", layoutId);
        settingAccountFragment.setArguments(bundle);
        return settingAccountFragment;
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
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        settingAccountActivity = (SettingAccountActivity) getActivity();
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
            case R.layout.fragment_account:
                initAccountFragment();
                break;
            case R.layout.fragment_message:
                initMessageFragment();
                break;
//            case R.layout.fragment_suggestion:
//                initSuggestionFragment();
//                break;
            case R.layout.fragment_about:
                initAboutFragment();
                break;
            case R.layout.fragment_current_phone:
                initCurrentPhoneFragment();
                break;
            case R.layout.fragment_pwd_obtain_phone_code:
                initPwdObtainPhoneCodeFragment();
                break;
            case R.layout.fragment_pwd_new:
                initPwdNewFragment();
                break;
            case R.layout.fragment_phone_code:
                initPhoneCodeFragment();
                break;
            case R.layout.fragment_change_phone_complete:
                initChangePhoneCompleteFragment();
                break;
        }
    }

    // 账号设置布局
    private void initAccountFragment() {
        // 账号设置页面假数据
        itemList = new ArrayList<>();
        accountRecycler = (RecyclerView) viewWrap.findViewById(R.id.account_content);
        SettingItemInfo changePwd = new SettingItemInfo("修改密码", "", "");
        SettingItemInfo changePhone = new SettingItemInfo("更换手机号", "" ,settingAccountActivity.localPhone);
        String tip = settingAccountActivity.apiToken == null ? "未绑定" : "已绑定";
        SettingItemInfo bindWeChat = new SettingItemInfo("绑定微信", "", tip);
        itemList.add(changePwd);
        itemList.add(changePhone);
        itemList.add(bindWeChat);
        SettingRecyclerAdapter settingRecyclerAdapter = new SettingRecyclerAdapter(getActivity(), itemList);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        LinearDividerDecoration linearDividerDecoration = new LinearDividerDecoration(getActivity(), R.drawable.recycler_divider_line, Dp2Px2SpUtil.dp2px(getActivity(), 20));
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        settingRecyclerAdapter.setOnItemClickWithSettingItemInfoListener(this);
        accountRecycler.setLayoutManager(llm);
        accountRecycler.addItemDecoration(linearDividerDecoration);
        accountRecycler.setAdapter(settingRecyclerAdapter);
    }

    // 推送消息布局
    private void initMessageFragment() {
        SwitchButton messageSwitch = (SwitchButton) viewWrap.findViewById(R.id.message_switch);
        // TODO: 获取推送消息开关状态
//        messageSwitch.setChecked(true);
        messageSwitch.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(getActivity(), "打开消息推送", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "关闭消息推送", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // 建议布局（先留着）
    /*private void initSuggestionFragment() {
        EditText editText = (EditText) viewWrap.findViewById(R.id.suggest_content);
        final TextView numText = (TextView) viewWrap.findViewById(R.id.suggest_num);
        Button btnSubmit = (Button) viewWrap.findViewById(R.id.suggest_submit);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int tempLength = s.length();
                numText.setText(String.format(getResources().getString(R.string.setting_suggestion_num), tempLength));
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingAccountActivity.replaceFragment(SettingAccountActivity.MAIN);
            }
        });
    }*/

    // 关于布局
    private void initAboutFragment() {
        SimpleDraweeView aboutLogo = (SimpleDraweeView) viewWrap.findViewById(R.id.about_logo);
        TextView aboutDesc = (TextView) viewWrap.findViewById(R.id.about_desc);
        aboutDesc.setText("吴晓波 版权所有");
        aboutContent = (RecyclerView) viewWrap.findViewById(R.id.about_content);
        // 关于布局假数据
        aboutLogo.setImageURI("http://pic24.nipic.com/20121008/6298817_164144357169_2.jpg");
        itemList = new ArrayList<>();
        SettingItemInfo connectUs = new SettingItemInfo("联系我们", "", "462436501@gmail.com");
        SettingItemInfo service = new SettingItemInfo("服务协议", "" ,"");
        itemList.add(connectUs);
        itemList.add(service);
        SettingRecyclerAdapter settingRecyclerAdapter = new SettingRecyclerAdapter(getActivity(), itemList);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        LinearDividerDecoration linearDividerDecoration = new LinearDividerDecoration(getActivity(), R.drawable.recycler_divider_line, Dp2Px2SpUtil.dp2px(getActivity(), 20));
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        settingRecyclerAdapter.setOnItemClickWithSettingItemInfoListener(this);
        aboutContent.setLayoutManager(llm);
        aboutContent.addItemDecoration(linearDividerDecoration);
        aboutContent.setAdapter(settingRecyclerAdapter);
    }

    private void initCurrentPhoneFragment() {
        TextView phoneNum = (TextView) viewWrap.findViewById(R.id.phone_num);
        Button phoneSubmit = (Button) viewWrap.findViewById(R.id.phone_submit);
        // 初始化数据
        phoneNum.setText(settingAccountActivity.localPhone);
        phoneSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingAccountActivity.inputNewPhone = false;
                settingAccountActivity.loginPresenter.obtainPhoneCode(settingAccountActivity.localPhone);
            }
        });
    }

    private void initPwdObtainPhoneCodeFragment() {
        Button nowBtn = (Button) viewWrap.findViewById(R.id.pwd_obtain_phone_code);

        nowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取验证码
                settingAccountActivity.loginPresenter.obtainPhoneCode(settingAccountActivity.localPhone);
                // 修改密码和更换手机是互斥操作
                settingAccountActivity.isUpdatePassword = true;
                settingAccountActivity.inputNewPhone = false;
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initPwdNewFragment() {
        final EditText newPwd = (EditText) viewWrap.findViewById(R.id.pwd_new_content);
        Button newBtn = (Button) viewWrap.findViewById(R.id.pwd_new_submit);

        newPwd.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 拿到画在尾部的 drawable
                Drawable drawable = newPwd.getCompoundDrawables()[2];
                // 如果没有不处理
                if (drawable == null) {
                    return false;
                }
                // 如果不是抬起事件，不处理
                if (event.getAction() != MotionEvent.ACTION_UP) {
                    return false;
                }
                if (event.getX() > newPwd.getWidth() - newPwd.getPaddingEnd() - drawable.getIntrinsicWidth()) {
                    if (newPwd.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD + InputType.TYPE_CLASS_TEXT) {
                        // 设置隐藏按钮
                        Drawable right = getActivity().getResources().getDrawable(R.mipmap.icon_hide);
                        right.setBounds(0, 0, right.getMinimumWidth(), drawable.getMinimumHeight());
                        newPwd.setCompoundDrawables(null, null, right, null);
                        newPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    } else {
                        // 设置显示按钮
                        Drawable right = getActivity().getResources().getDrawable(R.mipmap.icon_show);
                        right.setBounds(0, 0, right.getMinimumWidth(), drawable.getMinimumHeight());
                        newPwd.setCompoundDrawables(null, null, right, null);
                        newPwd.setInputType(InputType.TYPE_CLASS_TEXT |InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    }
                    return true;
                }
                return false;
            }
        });

        newBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newPwd.getText().toString().length() < 6) {
                    Toast.makeText(getActivity(), "密码长度不能少于6", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (imm != null && imm.isActive()) {
                    imm.hideSoftInputFromWindow(newPwd.getWindowToken(), 0);
                }
                String newPassword = newPwd.getText().toString();
                settingAccountActivity.loginPresenter.resetPasswordBySms(settingAccountActivity.localPhone, settingAccountActivity.smsCode, newPassword);
                newPwd.setText("");
            }
        });
    }

    protected CodeVerifyView phoneCodeContent;
    protected SettingTimeCount settingTimeCount;
    TextView phoneNumTitle;

    private void initPhoneCodeFragment() {
        settingTimeCount = new SettingTimeCount(getActivity(), 60000, 1000, viewWrap);
        settingTimeCount.start();
        phoneNumTitle = (TextView) viewWrap.findViewById(R.id.phone_num_title);
        phoneCodeContent = (CodeVerifyView) viewWrap.findViewById(R.id.phone_num_content);
        final TextView phoneNumDesc = (TextView) viewWrap.findViewById(R.id.phone_num_desc);
        phoneNumTitle.setText(String.format(getActivity().getResources().getString(R.string.setting_phone_num_title), settingAccountActivity.localPhone));
        phoneCodeContent.setOnCodeFinishListener(new CodeVerifyView.OnCodeFinishListener() {
            @Override
            public void onComplete(String content) {
                // content 为用户输入的验证码
                // 更换手机
                if (settingAccountActivity.inputNewPhone) {
                    settingAccountActivity.newSmsCode = content;
                    // 新手机验证码确认
                    settingAccountActivity.loginPresenter.verifyNewCode(settingAccountActivity.newPhone, content);
                } else {
                    settingAccountActivity.smsCode = content;
                    // 旧手机验证码确认
                    settingAccountActivity.loginPresenter.verifyCode(settingAccountActivity.localPhone, content);
                }
                // 修改密码
                if (settingAccountActivity.isUpdatePassword) {
                    settingAccountActivity.smsCode = content;
                    settingAccountActivity.loginPresenter.verifyCode(settingAccountActivity.localPhone, content);
                }
            }
        });
        phoneNumDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingAccountActivity.loginPresenter.obtainPhoneCode(phoneNumTitle.getText().toString());
                settingTimeCount.start();
            }
        });
    }

    TextView completeTitle;
    LinearLayout completeContentWrap;
    EditText completeContent;
    TextView completeError;
    Button completeSubmit;

    // 初始化修改手机完成页面
    private void initChangePhoneCompleteFragment() {
        completeTitle = (TextView) viewWrap.findViewById(R.id.setting_complete_title);
        completeContentWrap = (LinearLayout) viewWrap.findViewById(R.id.login_input_num_wrap);
        completeContent = (EditText) viewWrap.findViewById(R.id.login_input_num_content);
        completeError = (TextView) viewWrap.findViewById(R.id.login_error_tip);
        completeSubmit = (Button) viewWrap.findViewById(R.id.login_submit_btn);

        completeTitle.setVisibility(View.GONE);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, Dp2Px2SpUtil.dp2px(getActivity(), 40), 0, 0);
        completeContentWrap.setLayoutParams(layoutParams);
        completeContent.setInputType(InputType.TYPE_CLASS_PHONE);

        completeContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String head = s.toString();
                if (head.length() == 11 && completeError.getVisibility() == View.GONE) {
                    completeSubmit.setAlpha(1);
                    completeSubmit.setEnabled(true);
                } else {
                    JudgeUtil.showErrorViewIfNeed(getActivity(), head, completeError, completeSubmit);
                    completeSubmit.setAlpha(0.6f);
                    completeSubmit.setEnabled(false);
                    completeContent.setCursorVisible(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        completeSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completeContent.setCursorVisible(false);
                settingAccountActivity.inputNewPhone = true;
                settingAccountActivity.isUpdatePassword = false;
                settingAccountActivity.newPhone = completeContent.getText().toString();
                settingAccountActivity.loginPresenter.obtainPhoneCode(settingAccountActivity.newPhone);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        destroyView = true;
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    public void onItemClick(View view, SettingItemInfo itemInfo) {
        // 账号设置的点击事件
        if (accountRecycler == view.getParent()) {
            switch (itemList.indexOf(itemInfo)) {
                case 0:
                    // 修改密码
                    settingAccountActivity.replaceFragment(SettingAccountActivity.PWD_PHONE_CODE);
                    break;
                case 1:
                    // 更换手机号
                    settingAccountActivity.replaceFragment(SettingAccountActivity.CURRENT_PHONE);
                    break;
                case 2:
                    // 绑定微信
                    String tip = itemList.get(2).getItemContent();
                    if (tip.equals("未绑定")) {
                        JumpDetail.jumpLogin(getActivity());
                    }
                    break;
            }
        } else if (aboutContent == view.getParent()) { // 关于的点击事件
            switch (itemList.indexOf(itemInfo)) {
                case 0:
                    // 联系我们
                    Toast.makeText(getActivity(), "联系我们", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    // 服务协议
                    Toast.makeText(getActivity(), "服务协议", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    /**
     * 如果软键盘弹出，就关闭软键盘
     */
    private void toggleSoftKeyboard() {
        if (imm != null && imm.isActive()) {
            View view = getActivity().getCurrentFocus();
            if (view != null) {
                IBinder iBinder = view.getWindowToken();
                imm.hideSoftInputFromWindow(iBinder, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) { // 界面隐藏的时候，做清空操作
            toggleSoftKeyboard();
            if (phoneCodeContent != null) {
                phoneCodeContent.clearAllEditText();
            }
            if (settingTimeCount != null) {
                settingTimeCount.cancel();
            }
        }
    }
}