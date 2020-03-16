package com.xlh.study.simpleglide.loaddata;

import android.content.Context;

import com.xlh.study.simpleglide.resource.Value;

/**
 * @author: Watler Xu
 * time:2020/3/13
 * description:
 * version:0.0.1
 */
public interface ILoadData {

    public Value loadResource(Context context, String path, ResponseListener responseListener);

}
