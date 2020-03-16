package com.xlh.study.simpleglide.pool;

import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.util.LruCache;

import java.util.TreeMap;

/**
 * @author: Watler Xu
 * time:2020/3/13
 * description:
 * version:0.0.1
 */
public class LruBitmapPool extends LruCache<Integer, Bitmap> implements BitmapPool {

    private final String TAG = LruBitmapPool.class.getSimpleName();

    // 有序的map集合,可以按照key的插入顺序或者实现内部的comparator方法来自定义排序
    private TreeMap<Integer, String> treeMap = new TreeMap<>();

    public LruBitmapPool(int maxSize) {
        super(maxSize);
    }

    @Override
    public void putBitmap(Bitmap bitmap) {
        if (!bitmap.isMutable()) {
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            }
            Log.e(TAG, "putBitmap: 复用的条件1 Bitmap.ismutable 是false，条件不满足，不能复用 添加..." + bitmap);
            return;
        }

        int bitmapSize = getBitmapSize(bitmap);
        if (bitmapSize > maxSize()) {
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            }
            Log.e(TAG, "putBitmap: 复用的条件2 Bitmap.Size大于LruMaxSize，条件不满足，不能复用 添加..." + bitmap);
            return;
        }

        // 添加到Lru Cache中
        put(bitmapSize, bitmap);

        treeMap.put(bitmapSize, null);
        Log.e(TAG, "putBitmap() 添加到复用池了....");

    }

    @Override
    public Bitmap getBitmap(int width, int height, Bitmap.Config config) {
        // 获取Bitmap内存大小，只管 ARGB_8888  RGB_565
        int getSize = width * height * (config == Bitmap.Config.ARGB_8888 ? 4 : 2);
        Integer key = treeMap.ceilingKey(getSize);
        if (key == null) {
            return null;// 如果找不到 保存的key，就直接返回null，无法复用
        }
        if (key <= (getSize * 2)) {
            Bitmap resultBitmap = remove(key);
            Log.e(TAG, "getBitmap() 从缓存池中获取" + resultBitmap);
        }
        return null;
    }

    @Override
    protected int sizeOf(Integer key, Bitmap value) {
        return getBitmapSize(value);
    }

    @Override
    protected void entryRemoved(boolean evicted, Integer key, Bitmap oldValue, Bitmap newValue) {
        treeMap.remove(key);
    }

    /**
     * 获得Bitmap的大小
     *
     * @param bitmap
     * @return
     */
    private int getBitmapSize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return bitmap.getAllocationByteCount();
        } else {
            return bitmap.getByteCount();
        }
    }
}
