package com.leautolink.baseproject.base.ui.widget;

import android.content.Context;
import android.view.View;

/**
 * File description
 * Created by @author${shimeng}  on @date14/3/8.
 */

public interface IVaryViewHelper {

    View getCurrentLayout();

    void restoreView();

    void showLayout(View view);

    View inflate(int layoutId);

    Context getContext();

    View getView();

}
