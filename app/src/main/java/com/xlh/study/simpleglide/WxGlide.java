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
public class WxGlide {

    WxRequestManagerRetriver retriver;

    public WxGlide(WxRequestManagerRetriver retriver) {
        this.retriver = retriver;
    }

    public static WxRequestManager with(FragmentActivity fragmentActivity){
        return getWxRetriever(fragmentActivity).get(fragmentActivity);
    }

    public static WxRequestManager with(Activity activity){
        return getWxRetriever(activity).get(activity);
    }

    public static WxRequestManager with(Context context){
        return getWxRetriever(context).get(context);
    }


    public static WxGlide get(Context context){
        return new WxGildeBuilder().build();
    }

    public WxRequestManagerRetriver getWxRetriever(){
        return retriver;
    }

    public static WxRequestManagerRetriver getWxRetriever(Context context){
        return WxGlide.get(context).getWxRetriever();
    }


}
