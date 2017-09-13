package com.leautolink.baseproject.application;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.letv.loginsdk.LetvLoginSdkManager;


/**
 * File description
 * 该BaseApplication做一些所有app都会用到的基础初始化或者配置。
 * 之后其他应用的application应该都继承此BaseApplication。
 *
 * Created by
 *
 * @author ${shimeng} on @date${14/3/7}.
 */

public class BaseApplication extends MultiDexApplication {
    /*****************************集团登录SDK*********************************/
    /**
     * 平台标示,测试使用,发布时要换成自己的plat
     */
    private String platName = "leauto";
    /**
     * QQ_APP_ID 从QQ开发平台上申请到的APP ID
     */
    private static String QQ_APP_ID = null;//"1104986726";
    /**
     * QQ_APP_KEY 从QQ开发平台上申请到的APP KEY
     */
    private static String QQ_APP_KEY = null;//"dz2RMwajQQ9E4L8V";

    /**
     * 从新浪开发平台上申请到的App Key
     */
    private static String SINA_APP_KEY = null;//"3655642165";
    /**
     * 从新浪开发平台上申请到的App 密码
     */
    private static String SINA_APP_SECRET = null;//"b0b21c661b5c73fca2449ecd8824cfd0";
    /**
     * 从新浪开发平台上申请到的App 对应应用的回调页
     */
    private static String REDIRECT_URL = null;//"http://sso.letv.com/oauth/appsinacallbackdata";// 应用的回调页
    /**
     * 从微信开发平台上申请到的APP ID
     */
    private static String WX_APP_ID = null;//"wx1e151638198cbf2d";
    /**
     * 从微信开发平台上申请到的AppSecret
     */
    private static String WX_APP_SECRET = null;//"d4624c36b6795d1d99dcf0547af5443d";

    /**
     * 从乐视平台注册的超级ID和app secret
     */
    private static String SUPER_APP_ID = null;//"4b8cb88cdf84e069e0bd1da2";
    /**
     * 从乐视平台注册的超级ID的app secret
     */
    private static String SUPER_APP_SECRET = null;//"95dd5b0344e6c012ed5340ee";
    /**
     * 从乐视平台注册的超级ID返回的token
     */
    private static String SUPER_APP_SIGNTOKEN = null;//"jX5pM5MpaMOI9c2GNAwZZN1qmnaI2y0W";
    /**
     * 国际化需要用到的Google服务器使用的id
     */
    private static String GOOGLE_SERVER_ID = null;
    //"40664878722-3qmrg4gclkfuhede1lga4e1672ik6ad2.apps.googleusercontent.com";

    @Override
    public void onCreate() {
        super.onCreate();
        //setLoginParam("1105409216","AK8An2dkUsQhRZEs","wxe96e7888e16b06bb","b0b21c661b5c73fca2449ecd8824cfd0","1934408605","http://ecolink.leautolink.com/");
        //initThirdLogin();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    /**
     * 提供给使用的项目设置qq、微信、新浪微博申请的相应参数
     *
     * @param qq_id         项目申请的qq的ID
     * @param qq_key        项目申请的qq的key
     * @param weixin_id     项目申请的weixin的id
     * @param weixin_secret 项目申请的weixin的密码
     * @param sina_key      项目申请的新浪微博的key
     * @param sina_url      项目申请的新浪微博的回调地址
     */
    public void setLoginParam(String qq_id, String qq_key, String weixin_id, String weixin_secret,
            String sina_key, String sina_url) {
        QQ_APP_ID = qq_id;
        QQ_APP_KEY = qq_key;
        SINA_APP_KEY = sina_key;
        REDIRECT_URL = sina_url;
        WX_APP_ID = weixin_id;
        WX_APP_SECRET = weixin_secret;
    }

    /**
     * 提供给使用的项目设置注册乐视账号申请的相应参数
     *
     * @param superId_appID        超级id
     * @param superId_appsecret    超级id的密码
     * @param superId_appSignToken 超级id的token
     * @param google_server_Id     gooogle服务器所需要的ID
     */
    public void setLoginSuperIdParam(String superId_appID, String superId_appsecret,
            String superId_appSignToken, String google_server_Id) {
        SUPER_APP_ID = superId_appID;
        SUPER_APP_SECRET = superId_appsecret;
        SUPER_APP_SIGNTOKEN = superId_appSignToken;
        GOOGLE_SERVER_ID = google_server_Id;
    }

    /**
     * 提供给使用的项目调用三方登录初始化
     */
    public void initThirdLogin() {
        if (QQ_APP_ID != null && QQ_APP_KEY != null && SINA_APP_KEY != null && REDIRECT_URL != null
                && WX_APP_ID != null && WX_APP_SECRET != null) {
            LetvLoginSdkManager.initSDK(this, platName, true, true, true, true, true, true);

            new LetvLoginSdkManager().initThirdLoginSwitch(false, true, true, true, false, false,
                    false);
            // 需要传入第三方登录需要的一些参数（从QQ开发平台上申请到的APP ID和APP KEY，从新浪开发平台上申请到的App
            // Key和应用的回调页，从微信开发平台上申请到的APP ID和AppSecret）
            if (SUPER_APP_ID != null && SUPER_APP_SECRET != null
                    && SUPER_APP_SIGNTOKEN != null && GOOGLE_SERVER_ID != null) {

                new LetvLoginSdkManager().initThirdLogin(QQ_APP_ID, QQ_APP_KEY, SINA_APP_KEY,
                        REDIRECT_URL,
                        WX_APP_ID, WX_APP_SECRET, SUPER_APP_ID, SUPER_APP_SECRET,
                        SUPER_APP_SIGNTOKEN,
                        GOOGLE_SERVER_ID);
            } else {
                new LetvLoginSdkManager().initThirdLogin(QQ_APP_ID, QQ_APP_KEY, SINA_APP_KEY,
                        REDIRECT_URL,
                        WX_APP_ID, WX_APP_SECRET, "4b8cb88cdf84e069e0bd1da2",
                        "95dd5b0344e6c012ed5340ee",
                        "jX5pM5MpaMOI9c2GNAwZZN1qmnaI2y0W",
                        "40664878722-3qmrg4gclkfuhede1lga4e1672ik6ad2.apps.googleusercontent.com");
            }
            // 注册乐视账户成功时自动登录后是否显示个人信息界面（true:显示 false：不显示）
            new LetvLoginSdkManager().showPersonInfo(true);
        }
    }
}
