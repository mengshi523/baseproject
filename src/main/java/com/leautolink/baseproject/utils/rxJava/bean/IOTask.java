package com.leautolink.baseproject.utils.rxJava.bean;

/**
 * File description
 * 在IO线程中执行的任务
 * Created by @author${shimeng}  on @date14/3/17.
 */

public abstract class IOTask<T> {
    private T t;

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }


    public IOTask(T t) {
        setT(t);
    }


    public abstract void doInIOThread();
}
