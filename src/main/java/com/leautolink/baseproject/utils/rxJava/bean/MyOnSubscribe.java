package com.leautolink.baseproject.utils.rxJava.bean;

import rx.Observable;

/**
 * File description
 * 订阅抽象类
 * Created by @author${shimeng}  on @date14/3/17.
 */
public abstract class MyOnSubscribe<C> implements Observable.OnSubscribe<C> {
    private C c;

    public MyOnSubscribe(C c) {
        setT(c);
    }

    public C getT() {
        return c;
    }

    public void setT(C c) {
        this.c = c;
    }


}