package com.xlh.study.simpleglide;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.xlh.study.simpleglide.fragment.ActivityFragmentManager;
import com.xlh.study.simpleglide.fragment.FragmentActivityFragmentManager;


/**
 * @author: Watler Xu
 * time:2020/3/12
 * description:
 * version:0.0.1
 */
public class WxRequestManager {

    private final String TAG = WxRequestManager.class.getSimpleName();

    private Context requestManagerContext;

    private final String FRAGMENT_ACTIVITY_NAME = "FRAGMENT_ACTIVITY_NAME";
    private final String ACTIVITY_NAME = "ACTIVITY_NAME";
    private final int NEXT_HANDLER_MSG = 995465;

    FragmentActivity fragmentActivity;

    private static WxRequestTargetEngine requestTargetEngine;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            Fragment fragment = fragmentActivity.getSupportFragmentManager().findFragmentByTag(FRAGMENT_ACTIVITY_NAME);
            Log.e(TAG,"Handler: fragment" + fragment);
            return false;
        }
    });

    {
        if(requestTargetEngine == null){
            requestTargetEngine = new WxRequestTargetEngine();
        }
    }


    /**
     * 通过FragmentActivity的生命周期来管理
     * @param fragmentActivity
     */
    public WxRequestManager(FragmentActivity fragmentActivity) {
        this.requestManagerContext = fragmentActivity;
        this.fragmentActivity = fragmentActivity;

        // 获取Fragment
        FragmentManager supportFragmentManager = fragmentActivity.getSupportFragmentManager();
        Fragment fragment = supportFragmentManager.findFragmentByTag(FRAGMENT_ACTIVITY_NAME);
        if(null == fragment){
            // fragment的生命周期与requestTargetEngine关联起来
            fragment = new FragmentActivityFragmentManager(requestTargetEngine);
            // 添加到 supportFragmentManager
            supportFragmentManager.beginTransaction().add(fragment,FRAGMENT_ACTIVITY_NAME).commitAllowingStateLoss();
        }

        mHandler.sendEmptyMessage(NEXT_HANDLER_MSG);

    }

    /**
     * 通过Activity的生命周期来管理
     * @param activity
     */
    public WxRequestManager(Activity activity) {
        this.requestManagerContext = activity;

        android.app.FragmentManager fragmentManager = activity.getFragmentManager();
        android.app.Fragment fragment = fragmentManager.findFragmentByTag(ACTIVITY_NAME);
        if(null == fragment){
            // fragment的生命周期与requestTargetEngine关联起来
            fragment= new ActivityFragmentManager(requestTargetEngine);
            // 添加到管理器
            fragmentManager.beginTransaction().add(fragment,ACTIVITY_NAME).commitAllowingStateLoss();

        }

        mHandler.sendEmptyMessage(NEXT_HANDLER_MSG);

    }

    /**
     * Application,无法管理生命周期
     * @param context
     */
    public WxRequestManager(Context context) {
        this.requestManagerContext = context;
    }

    public WxRequestTargetEngine load(String path){
        mHandler.removeMessages(NEXT_HANDLER_MSG);

        requestTargetEngine.loadValueInitAction(requestManagerContext,path);

        return requestTargetEngine;
    }


}
