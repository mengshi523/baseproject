package com.leautolink.baseproject.base.manager;

import android.app.Activity;
import android.content.Context;

import com.leautolink.baseproject.base.modelInterface.LoginModel;
import com.leautolink.baseproject.base.storage.BaseSharePreference;
import com.leautolink.baseproject.utils.LetvReportUtils;
import com.letv.loginsdk.LoginSdk;
import com.letv.loginsdk.LoginSdkLogout;
import com.letv.loginsdk.bean.LetvBaseBean;
import com.letv.loginsdk.bean.UserBean;
import com.letv.loginsdk.callback.LoginSuccessCallBack;



/**
 * File description
 * 对登录sdk的再封装，并存在本地提供其他模块使用。
 * Created by @author${shimeng}  on @date14/3/8.
 */

public class LoginManager implements LoginModel {
    @Override
    public void init(Context context) {
        boolean isLogin = isLogin(context);
        if(!isLogin) {
            BaseSharePreference.putBoolean(context, "islogin", false);
            BaseSharePreference.putString(context, "headPicUrl", "");
            BaseSharePreference.putString(context, "userName", "");
            BaseSharePreference.putString(context, "nickName", "");
            BaseSharePreference.putString(context, "uid", "");
            BaseSharePreference.putString(context, "ssoTk", "");
            BaseSharePreference.putString(context, "passWord", "");
        }
    }

    @Override
    public void login(final Activity activity, final OnLoginFinishedListener LoginListener) {
        new LoginSdk().login(activity, new LoginSuccessCallBack() {
            @Override
            public void loginSuccessCallBack(LoginSuccessState loginSuccessState,
                    LetvBaseBean bean) {
                if (loginSuccessState == LoginSuccessState.LOGINSUCCESS) {
                    //登录成功
                    UserBean userBean = (UserBean) bean;
                    saveInfoToSp(activity, userBean);
                    LetvReportUtils.reportLoginEvent(userBean.getUid(), "login");
                    LoginListener.onSuccess(userBean);
                } else {
                    LoginListener.onFailer();
                }
            }
        });
    }

    private void saveInfoToSp(Context context, UserBean userBean) {
        BaseSharePreference.putBoolean(context, "islogin", true);
        BaseSharePreference.putString(context, "headPicUrl", userBean.getPicture200x200());
        BaseSharePreference.putString(context, "userName", userBean.getUsername());
        BaseSharePreference.putString(context, "nickName", userBean.getNickname());
        BaseSharePreference.putString(context, "uid", userBean.getUid());
        BaseSharePreference.putString(context, "ssoTk", userBean.getSsoTK());
        BaseSharePreference.putString(context, "passWord", userBean.getPw());
    }

    @Override
    public void logout(Context context) {
        new LoginSdkLogout().logout(context);
        BaseSharePreference.putBoolean(context, "islogin", false);
    }

    @Override
    public boolean isLogin(Context context) {
        return BaseSharePreference.getBoolean(context, "islogin", false);
    }

    @Override
    public String getHeadPicUrl(Context context) {
        return BaseSharePreference.getString(context, "headPicUrl", "");
    }

    @Override
    public String getUserName(Context context) {
        return BaseSharePreference.getString(context, "userName", "");
    }

    @Override
    public String getNickName(Context context) {
        return BaseSharePreference.getString(context, "nickName", "");
    }

    @Override
    public String getUid(Context context) {
        return BaseSharePreference.getString(context, "uid", "");
    }

    @Override
    public String getSsoTk(Context context) {
        return BaseSharePreference.getString(context, "ssoTk", "");
    }

    @Override
    public String getPassWord(Context context) {
        return BaseSharePreference.getString(context, "passWord", "");
    }

    @Override
    public void setUserName(Context context, String value) {
        BaseSharePreference.putString(context, "userName", value);
    }

    @Override
    public void setNickName(Context context, String value) {
        BaseSharePreference.putString(context, "nickName", value);
    }

    @Override
    public void setHeadPicUrl(Context context, String url) {
        BaseSharePreference.putString(context, "headPicUrl", url);
    }

    @Override
    public void setPassWord(Context context, String value) {
        BaseSharePreference.putString(context, "passWord", value);
    }
}
