package com.leautolink.baseproject.base.manager;

import com.letv.loginsdk.bean.UserBean;

/**
 * File description
 * Created by @author${shimeng}  on @date14/3/8.
 */

public interface OnLoginFinishedListener {
    void onSuccess(UserBean userBean);

    void onFailer();
}
