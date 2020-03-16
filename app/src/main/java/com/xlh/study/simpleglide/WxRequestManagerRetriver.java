package com.xlh.study.simpleglide;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;


/**
 * @author: Watler Xu
 * time:2020/3/12
 * description:
 * version:0.0.1
 */
public class WxRequestManagerRetriver {

    public WxRequestManager get(FragmentActivity fragmentActivity) {
        return new WxRequestManager(fragmentActivity);
    }

    public WxRequestManager get(Activity activity){
        return new WxRequestManager(activity);
    }

    public WxRequestManager get(Context context){
        return new WxRequestManager(context);
    }

}
