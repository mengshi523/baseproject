package com.leautolink.baseproject.base.presenter;

import android.content.Context;

import com.leautolink.baseproject.utils.rxBus.RxManager;


/**
 * Created by ${shimeng} on 14/3/7.
 */

public  class BasePresenterImpl<T> implements BasePresenter<T> {
    //private WeakReference actReference;
    public Context context;
    public T mView;
    public RxManager mRxManager = new RxManager();

    @Override
    public void attachView(T view) {
        this.mView = view;
    }

    @Override
    public void detachView() {

        mView = null;
    }

    @Override
    public T getView() {
        return mView;
    }

    public boolean isViewAttached() {
        return mView != null;
    }

//    public void checkViewAttached() {
//        if (!isViewAttached()) throw new MvpViewNotAttachedException();
//    }
//
//
//    public static class MvpViewNotAttachedException extends RuntimeException {
//        public MvpViewNotAttachedException() {
//            super("Please call Presenter.attachView(MvpView) before" +
//                    " requesting data to the Presenter");
//        }
//    }
//
    @Override
    public void onDestroy() {
        mRxManager.clear();
    }
}
