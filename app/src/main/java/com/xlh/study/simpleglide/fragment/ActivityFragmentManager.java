package com.xlh.study.simpleglide.fragment;

import android.app.Fragment;

/**
 * @author: Watler Xu
 * time:2020/3/13
 * description: Activity生命周期关联管理
 * version:0.0.1
 */
public class ActivityFragmentManager extends Fragment {

    private LifecycleCallback lifecycleCallback;

    public ActivityFragmentManager() {

    }

    public ActivityFragmentManager(LifecycleCallback lifecycleCallback) {
        this.lifecycleCallback = lifecycleCallback;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (lifecycleCallback != null) {
            lifecycleCallback.wxGlideInitAction();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (lifecycleCallback != null) {
            lifecycleCallback.wxGlideStopAction();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (lifecycleCallback != null) {
            lifecycleCallback.wxGildeRecycleAction();
        }
    }
}
