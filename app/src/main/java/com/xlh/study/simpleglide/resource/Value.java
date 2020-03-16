package com.xlh.study.simpleglide.resource;

import android.graphics.Bitmap;
import android.util.Log;

import com.xlh.study.simpleglide.Utils;

/**
 * @author: Watler Xu
 * time:2020/3/13
 * description: 对Bitmap的封装
 * version:0.0.1
 */
public class Value {

    private final String TAG = Value.class.getSimpleName();

    private Value() {

    }

    private static volatile Value value;

    public static Value getInstance() {
        if (null == value) {
            synchronized (Value.class) {
                if (null == value) {
                    value = new Value();
                }
            }
        }
        return value;
    }

    private Bitmap bitmap;

    // 使用计数
    private int count;

    // 资源监听
    private ValueCallback valueCallback;

    private String key;


    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        Log.e(TAG, "setBitmap -- bitmap.isMutable:" + bitmap.isMutable());
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ValueCallback getValueCallback() {
        return valueCallback;
    }

    public void setValueCallback(ValueCallback valueCallback) {
        this.valueCallback = valueCallback;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    /**
     * 使用就加一
     */
    public void useAction() {
        Utils.checkNotEmpty(bitmap);

        if (bitmap.isRecycled()) {
            Log.e(TAG, "useAction() bitmap isRecycled");
            return;
        }

        count++;

        Log.e(TAG, "useAction() use +1,count: " + count);

    }

    /**
     * 不再使用就减一
     */
    public void nonUseAction() {
        count--;
        if (count <= 0 && valueCallback != null) {
            Log.e(TAG, "nonUseAction() -- bitmap.isMutable:" + bitmap.isMutable());
            // 设置资源监听回调，告诉外界不再使用了
            valueCallback.valueNonUseListener(key, value);
        }
        Log.e(TAG, "nonUseAction() use -1,count: " + count);
    }

    /**
     * 释放Bitmap
     */
    public void recycleBitmap() {
        if (count > 0) {
            Log.e(TAG, "recycleBitmap() 使用计数大于0，证明还在使用中，不能去释放...");
        }

        if (bitmap.isRecycled()) {
            Log.e(TAG, "recycleBitmap() bitmap isRecycled()");
            return;
        }

        bitmap.isRecycled();

        value = null;

        // 通知gc回收
        System.gc();

    }


}
