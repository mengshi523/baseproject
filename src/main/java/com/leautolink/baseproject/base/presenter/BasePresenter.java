package com.leautolink.baseproject.base.presenter;

/**
 * Created by ${shimeng} on 14/3/2.
 */

public interface BasePresenter <T>{
    /**
     * @param view 绑定
     */
    void attachView(T view);

    /**
     * 防止内存的泄漏,清楚presenter与activity之间的绑定
     */
    void detachView();

    /**
     *
     * @return 获取View
     */
    T getView();
    /**
     *
     * @return 销毁
     */
    void onDestroy();
}
