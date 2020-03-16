package com.xlh.study.simpleglide.cache;

import android.graphics.Bitmap;
import android.util.Log;

import com.xlh.study.simpleglide.Utils;
import com.xlh.study.simpleglide.resource.Value;
import com.xlh.study.simpleglide.resource.ValueCallback;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: Watler Xu
 * time:2020/3/13
 * description: 活动缓存——真正被使用的资源
 * version:0.0.1
 */
public class ActiveCache {

    private final static String TAG = ActiveCache.class.getSimpleName();

    private Map<String, CustomValueWeakReference> mapList = new HashMap<>();
    private Map<String, Bitmap> mapValueList = new HashMap<>();

    private ReferenceQueue<Value> queue;
    // 标识释放关闭线程
    private boolean isCloseThread;
    private Thread thread;
    // 区分手动移除 和 被动移除
    private boolean isShutdownRemove;
    // 资源监听回调
    private ValueCallback valueCallback;

    public ActiveCache(ValueCallback valueCallback) {
        this.valueCallback = valueCallback;
    }

    /**
     * 添加至活动缓存
     *
     * @param key
     * @param value
     */
    public void put(String key, Value value) {
        Utils.checkNotEmpty(key);

        // 设置Value的监听
        value.setValueCallback(valueCallback);

        // 添加到容器中
        mapList.put(key, new CustomValueWeakReference(value, getQueue(), key));
        mapValueList.put(key, value.getBitmap());
    }

    /**
     * 通过key获取Value
     *
     * @param key
     * @return
     */
    public Value getValue(String key) {
        CustomValueWeakReference customValueWeakReference = mapList.get(key);
        if (null != customValueWeakReference) {
            Value value = customValueWeakReference.getValue();
            value.setBitmap(mapValueList.get(key));
            value.setKey(key);

            Log.e(TAG, "ActiveCache getValue: Inputkey:" + key + " --- value:" + value.getBitmap() + "对应 key:" + value.getKey());
            return value;
        }
        return null;
    }

    /**
     * 通过key手动移除Value
     *
     * @param key
     * @return
     */
    public Value removeValue(String key) {
        isShutdownRemove = true;
        WeakReference<Value> valueWeakReference = mapList.remove(key);
        // 还原 目的是为了 让 GC自动移除 继续工作
        isShutdownRemove = false;
        if (null != valueWeakReference) {
            return valueWeakReference.get();
        }
        return null;
    }

    public void closeThread() {
        try {
            thread.interrupt();
            isCloseThread = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        mapList.clear();
        System.gc();
    }


    /**
     * 监听弱引用是否被回收
     */
    public static final class CustomValueWeakReference extends WeakReference<Value> {

        private String key;
        private Value value;

        public CustomValueWeakReference(Value value, ReferenceQueue<? super Value> q, String key) {
            super(value, q);
            this.key = key;
            this.value = value;
            Log.e(TAG, "构造 put: Inputkey:" + key + " --- value:" + this.value.getBitmap() + "对应 key:" + this.value.getKey());
        }

        public Value getValue() {
            return this.value;
        }
    }

    private ReferenceQueue<Value> getQueue() {
        if (queue == null) {
            queue = new ReferenceQueue<>();

            //
            thread = new Thread() {
                @Override
                public void run() {
                    super.run();

                    while (!isCloseThread) {
                        try {
                            if (!isShutdownRemove) {
                                Reference<? extends Value> removeReference = queue.remove();
                                CustomValueWeakReference customWeakReference = (CustomValueWeakReference) removeReference;

                                if (mapList != null && !mapList.isEmpty() && !mapValueList.isEmpty()) {
                                    mapList.remove(customWeakReference.key);
                                    mapValueList.remove(customWeakReference.key);
                                }
                            }

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            thread.start();
        }
        return queue;
    }


}
