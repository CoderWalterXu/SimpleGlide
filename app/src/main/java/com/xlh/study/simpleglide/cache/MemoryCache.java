package com.xlh.study.simpleglide.cache;

import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.LruCache;

import com.xlh.study.simpleglide.resource.Value;

/**
 * @author: Watler Xu
 * time:2020/3/13
 * description: 内存缓存--LRU算法
 * version:0.0.1
 */
public class MemoryCache extends LruCache<String, Value> {

    private boolean isShutdownRemove;

    private MemoryCacheCallback memoryCacheCallback;

    public MemoryCache(int maxSize) {
        super(maxSize);
    }

    public void setMemoryCacheCallback(MemoryCacheCallback memoryCacheCallback) {
        this.memoryCacheCallback = memoryCacheCallback;
    }

    public Value shutdownRemove(String key) {
        isShutdownRemove = true;
        Value value = remove(key);
        isShutdownRemove = false;  // !isShutdownRemove == 被动的
        return value;
    }

    @Override
    protected int sizeOf(String key, Value value) {
        Bitmap bitmap = value.getBitmap(); // 8

        // 最开始的时候
        // int result = bitmap.getRowBytes() * bitmap.getHeight();

        // API 12  3.0
        // result = bitmap.getByteCount(); // 在bitmap内存复用上有区别 （所属的）

        // API 19 4.4
        // result = bitmap.getAllocationByteCount(); // 在bitmap内存复用上有区别 （整个的）

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return bitmap.getAllocationByteCount();
        }

        return bitmap.getByteCount();
    }


    @Override
    protected void entryRemoved(boolean evicted, @NonNull String key, @NonNull Value oldValue, @Nullable Value newValue) {
        super.entryRemoved(evicted, key, oldValue, newValue);

        if (memoryCacheCallback != null && !isShutdownRemove) { // !isShutdownRemove == 被动的
            memoryCacheCallback.entryRemoveMemoryCache(key, oldValue);
        }

    }

}
