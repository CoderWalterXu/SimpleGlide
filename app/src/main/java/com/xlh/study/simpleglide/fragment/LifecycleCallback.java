package com.xlh.study.simpleglide.fragment;

/**
 * @author: Watler Xu
 * time:2020/3/13
 * description:
 * version:0.0.1
 */
public interface LifecycleCallback {

    /**
     * 生命周期初始化
     */
    public void wxGlideInitAction();

    /**
     * 生命周期停止
     */
    public void wxGlideStopAction();

    /**
     * 生命周期销毁，释放资源
     */
    public void wxGildeRecycleAction();

}
