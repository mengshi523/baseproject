package com.leautolink.baseproject.base.manager;
import rx.Subscriber;

/**
 * Created by ${shimeng} on 14/3/2.
 */

public abstract class BaseSubscriber<T> extends Subscriber<T> {

    @Override
    public void onError(Throwable e) {

        onError(e);
    }

    /**
     * @param e 错误的一个回调
     */
    protected abstract void onError(Exception e);

}
