package com.xlh.study.simpleglide.loaddata;

import com.xlh.study.simpleglide.resource.Value;

/**
 * @author: Watler Xu
 * time:2020/3/13
 * description: 加载外部资源，成功失败的回调接口
 * version:0.0.1
 */
public interface ResponseListener {

    public void responseSuccess(Value value);

    public void responseException(Exception e);

}
