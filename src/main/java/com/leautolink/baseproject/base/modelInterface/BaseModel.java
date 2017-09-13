package com.leautolink.baseproject.base.modelInterface;

import android.content.Context;

/**
 * Created by ${shimeng} on 14/3/2.
 *  * Class Note:
 * 1 完成presenter的实现。这里面主要是Model层和View层的交互和操作。
 * 2  presenter里面还有个FinishedListener，
 * 其在Presenter层实现，给Model层回调，更改View层的状态，
 * 确保 Model层不直接操作View层。如果没有这一接口在LoginPresenterImpl实现的话，
 * LoginPresenterImpl只 有View和Model的引用那么Model怎么把结果告诉View呢？
 */

public interface BaseModel {
    void init(Context context);
}
