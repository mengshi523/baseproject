package com.leautolink.baseproject.base.modelInterface;

import android.app.Activity;
import android.content.Context;

import com.leautolink.baseproject.base.manager.OnLoginFinishedListener;


/**
 * File description
 * Created by @author${shimeng}  on @date14/3/17.
 */

public interface LoginModel extends BaseModel {
    void login(Activity activity, OnLoginFinishedListener listener);
    void logout(Context context);
    boolean isLogin(Context context);
    String getHeadPicUrl(Context context);
    String getUserName(Context context);
    String getNickName(Context context);
    String getUid(Context context);
    String getSsoTk(Context context);
    String getPassWord(Context context);
    void setUserName(Context context, String value);
    void setNickName(Context context, String value);
    void setHeadPicUrl(Context context, String url);
    void setPassWord(Context context, String value);
}
