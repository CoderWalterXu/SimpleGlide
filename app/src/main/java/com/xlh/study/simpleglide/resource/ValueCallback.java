package com.xlh.study.simpleglide.resource;

/**
 * @author: Watler Xu
 * time:2020/3/13
 * description:监听Value不再使用的回调接口
 * version:0.0.1
 */
public interface ValueCallback {

    /**
     * 监听Value不再使用
     * @param key
     * @param value
     */
    public void valueNonUseListener(String key,Value value);

}
