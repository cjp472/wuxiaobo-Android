package xiaoe.com.shop.wxapi;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import xiaoe.com.common.app.Constants;


/**
 * WXEntryActivity 是微信固定的Activiy、 不要改名字、并且放到你对应的项目报名下面、
 * 例如： ....(package报名).wxapi.WXEntryActivity
 * 不然无法回调、切记...
 * Wx  回调接口 IWXAPIEventHandler
 * <p/>
 * 关于WXEntryActivity layout。 我们没给页面、而是把Activity  主题 android:theme="@android:style/Theme.Translucent" 透明、
 * <p/>
 * User: MoMo - Nen
 * Date: 2015-10-24
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private final String TAG = this.getClass().getSimpleName();

    private IWXAPI mApi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApi = WXAPIFactory.createWXAPI(this, Constants.getWXAppId(), true);
        mApi.handleIntent(this.getIntent(), this);
    }

    //微信发送的请求将回调到onReq方法
    @Override
    public void onReq(BaseReq baseReq) {
        Log.d(TAG, "onReq: ....");
    }

    //发送到微信请求的响应结果

    @Override
    public void onResp(BaseResp resp) {
        String code = "";
        String msg = "";
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                //发送成功
                SendAuth.Resp sendResp = (SendAuth.Resp) resp;
                code = sendResp.code;
                msg = "发送成功";
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                //发送取消
                msg = "发送取消";
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                //发送被拒绝
                msg = "发送被拒绝";
                break;
            default:
                //发送返回
                break;
        }
        if(resp.getType() == ConstantsAPI.COMMAND_SENDAUTH){
//            WXEntry wxEntry = WXEntry.getInstance();
//            wxEntry.setCode(code);
//            wxEntry.setMsg(msg);
//            wxEntry.setLogin(true);
//            Intent intent = WXLoginActivity.wxLoginActivity.getIntent();
//            intent.putExtra("code", code);
//            intent.putExtra("msg", msg);
//            intent.putExtra("isLogin", true);
            finish();
        }

    }


}
