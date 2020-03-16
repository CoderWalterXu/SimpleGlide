package com.xlh.study.simpleglide.cache;

import com.xlh.study.simpleglide.resource.Value;

/**
 * @author: Watler Xu
 * time:2020/3/13
 * description: 内存缓存中，元素被移除的接口回调
 * version:0.0.1
 */
public interface MemoryCacheCallback {

    public void entryRemoveMemoryCache(String key, Value oldValue);

}
