package com.leautolink.baseproject.utils.rxJava.bean;

/**
 * File description
 * 在主线程中执行的任务
 * Created by @author${shimeng}  on @date14/3/17.
 */

public abstract class UITask<T> {
    
    public abstract void doInUIThread();

    public UITask(T t) {
        setT(t);
    }

    public UITask() {

    }

    private T t;

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }
}