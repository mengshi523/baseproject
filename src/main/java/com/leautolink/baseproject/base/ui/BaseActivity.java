package com.leautolink.baseproject.base.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;

import com.leautolink.baseproject.base.presenter.BasePresenterImpl;
import com.leautolink.baseproject.base.reveiver.NetStateReceiver;
import com.leautolink.baseproject.utils.JumpUtil;
import com.leautolink.baseproject.utils.NetChangeObserver;
import com.leautolink.baseproject.utils.NetworkUtil;

import butterknife.ButterKnife;

/**
 * Created by ${shimeng} on 14/3/2.
 */

public abstract class BaseActivity<T extends BasePresenterImpl> extends AppCompatActivity implements
        JumpUtil.JumpInterface {

    /**
     * Log tag
     */
    protected static String TAG_LOG = null;
    /**
     * 屏幕参数
     */
    protected int mScreenWidth = 0;
    protected int mScreenHeight = 0;
    protected float mScreenDensity = 0.0f;
    /**
     * 上下文
     */
    protected Context mContext = null;
    /**
     * 联网状态
     */
    protected NetChangeObserver mNetChangeObserver = null;

    public T mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        TAG_LOG = this.getClass().getSimpleName();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        mScreenDensity = displayMetrics.density;
        mScreenHeight = displayMetrics.heightPixels;
        mScreenWidth = displayMetrics.widthPixels;
        if (getContentViewLayoutID() != 0) {
            setContentView(getContentViewLayoutID());
        } else {
            throw new IllegalArgumentException(
                    "You must return a right contentView layout resource Id");
        }
        mPresenter = loadPresenter();
        if (this instanceof BaseView) mPresenter.attachView(this);
        initView();
        if(mPresenter!=null) {
            mPresenter.attachView(this);
        }
        initData();
        initListener();

    }

    @Override
    protected void onDestroy() {
        NetStateReceiver.removeRegisterObserver(mNetChangeObserver);
        if(mPresenter!=null) {
            mPresenter.detachView();
        }
        super.onDestroy();
    }


    @Override
    public void setContentView(int layoutID) {
        super.setContentView(layoutID);
        ButterKnife.bind(this);
    }


    /**
     * 界面需要初始化的数据
     */

    public void initData() {
        mNetChangeObserver = new NetChangeObserver() {
            @Override
            public void onNetConnected(NetworkUtil.NetType type) {
                super.onNetConnected(type);
                onNetworkConnected(type);
            }

            @Override
            public void onNetDisConnect() {
                super.onNetDisConnect();
                onNetworkDisConnected();
            }
        };

        NetStateReceiver.registerObserver(mNetChangeObserver);
    }

    /**
     * network connected
     */
    public void onNetworkConnected(NetworkUtil.NetType type) {

    }

    /**
     * network disconnected
     */
    public void onNetworkDisConnected() {

    }

    protected abstract T loadPresenter();

    protected abstract void initListener();

    protected abstract void initView();


    /**
     * bind layout resource file
     *
     * @return id of layout resource
     */
    protected abstract int getContentViewLayoutID();


}
