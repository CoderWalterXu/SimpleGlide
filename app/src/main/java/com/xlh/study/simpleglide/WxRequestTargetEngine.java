package com.xlh.study.simpleglide;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.xlh.study.simpleglide.cache.ActiveCache;
import com.xlh.study.simpleglide.cache.MemoryCache;
import com.xlh.study.simpleglide.cache.MemoryCacheCallback;
import com.xlh.study.simpleglide.cache.disk.DiskLruCacheImpl;
import com.xlh.study.simpleglide.fragment.LifecycleCallback;
import com.xlh.study.simpleglide.loaddata.LoadDataManager;
import com.xlh.study.simpleglide.loaddata.ResponseListener;
import com.xlh.study.simpleglide.pool.LruBitmapPool;
import com.xlh.study.simpleglide.resource.Key;
import com.xlh.study.simpleglide.resource.Value;
import com.xlh.study.simpleglide.resource.ValueCallback;

/**
 * @author: Watler Xu
 * time:2020/3/13
 * description: 加载图片资源
 * version:0.0.1
 */
public class WxRequestTargetEngine implements LifecycleCallback, ValueCallback, MemoryCacheCallback, ResponseListener {

    private final String TAG = WxRequestTargetEngine.class.getSimpleName();

    // 活动缓存
    private ActiveCache activeCache;
    // 内存缓存
    private MemoryCache memoryCache;
    // 磁盘缓存
    private DiskLruCacheImpl diskLruCache;
    // 复用池
    private LruBitmapPool lruBitmapPool;
    private final int MEMORY_MAX_SIZE = 1024 * 1024 * 60;

    private String path;
    private Context context;
    private String key;
    private ImageView imageView;

    public WxRequestTargetEngine() {
        if (activeCache == null) {
            activeCache = new ActiveCache(this); // 回调告诉外界，Value资源不再使用了 设置监听
        }
        if (memoryCache == null) {
            memoryCache = new MemoryCache(MEMORY_MAX_SIZE); //LRU最少使用的元素会被移除 设置监听
            memoryCache.setMemoryCacheCallback(this);
        }
        // 初始化磁盘缓存
        diskLruCache = new DiskLruCacheImpl();

        if (lruBitmapPool == null) {
            lruBitmapPool = new LruBitmapPool(MEMORY_MAX_SIZE);
        }
    }


    public void loadValueInitAction(Context context, String path) {
        this.context = context;
        this.path = path;
        key = new Key(path).getKey();
    }

    public void into(ImageView imageView) {
        this.imageView = imageView;

        Utils.checkNotEmpty(imageView);
        Utils.assertMainThread();

        Value value = cacheAction();
        if (null != value) {
            // 使用完成了 减一
            value.nonUseAction();
            imageView.setImageBitmap(value.getBitmap());
        }

    }

    /**
     * 加载资源: 活动缓存 --> 内存缓存 --> 磁盘缓存 --> 网络资源
     * 加载资源成功后: 资源保存到缓存中
     *
     * @return
     */
    private Value cacheAction() {

        // 活动缓存
        Value value = activeCache.getValue(key);
        if (null != value) {
            Log.e(TAG, "cacheAction: 本次加载是在(活动缓存)中获取的资源>>>");

            // 返回 代表 使用了一次 Value
            value.useAction(); // 使用了一次 加一
            return value;
        }

        // 内存缓存
        value = memoryCache.get(key);
        if (null != value) {
            // 将内存缓存元素移动到活动缓存
            activeCache.put(key, value);
            // 移除内存缓存
            memoryCache.shutdownRemove(key);

            Log.d(TAG, "cacheAction: 本次加载是在(内存缓存)中获取的资源>>>");

            // 使用了一次 加一
            value.useAction();
            return value;
        }

        // 磁盘缓存
        value = diskLruCache.get(key, lruBitmapPool);
        if (null != value) {
            // 将磁盘缓存元素移动到活动缓存
            activeCache.put(key, value);

            Log.e(TAG, "cacheAction: 本次加载是在(磁盘缓存)中获取的资源>>>");

            // 使用了一次 加一
            value.useAction();
            return value;
        }

        // 网络资源
        value = new LoadDataManager().loadResource(context, path, this);
        if (null != value) {
            return value;
        }


        return null;
    }

    private void saveCache(String key, Value value) {
        Log.e(TAG, "saveCache() 加载外部资源成功后，保存到缓存中 key:" + key + " value:" + value);
        value.setKey(key);

        if (diskLruCache != null) {
            // 保存到磁盘缓存中
            diskLruCache.put(key, value);
        }
    }


    @Override
    public void entryRemoveMemoryCache(String key, Value oldValue) {
        // 添加到Bitmap复用池
        lruBitmapPool.putBitmap(oldValue.getBitmap());
    }

    @Override
    public void wxGlideInitAction() {
        Log.e(TAG, "wxGlideInitAction WxGlide生命周期——初始化");
    }

    @Override
    public void wxGlideStopAction() {
        Log.e(TAG, "wxGlideInitAction WxGlide生命周期——停止");
    }

    @Override
    public void wxGildeRecycleAction() {
        Log.e(TAG, "wxGlideInitAction WxGlide生命周期——销毁");

        // 把活动缓存给释放掉
        if (activeCache != null) {
            activeCache.closeThread();
        }
    }

    @Override
    public void responseSuccess(Value value) {
        if (null != value) {
            saveCache(key, value);

            Log.e(TAG, "responseSuccess: value.getmBitmap().isMutable() " + value.getBitmap().isMutable());

            imageView.setImageBitmap(value.getBitmap());
        }
    }

    @Override
    public void responseException(Exception e) {
        Log.e(TAG, "responseException: 加载外部资源失败 e:" + e.getMessage());
    }

    @Override
    public void valueNonUseListener(String key, Value value) {
        // 把活动缓存操作的Value资源 加入到 内存缓存
        if (key != null && value != null) {
            Log.e(TAG, "valueNonUseListener -- value.getmBitmap().isMutable():" + value.getBitmap().isMutable());
            memoryCache.put(key, value);
        }
    }
}
