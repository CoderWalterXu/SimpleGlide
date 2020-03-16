package com.xlh.study.simpleglide.pool;

import android.graphics.Bitmap;

/**
 * @author: Watler Xu
 * time:2020/3/13
 * description: Bitmap内存复用池
 * version:0.0.1
 */
public interface BitmapPool {

    /**
     * 将bitmap加入到Bitmap内存复用池
     * @param bitmap
     */
    void putBitmap(Bitmap bitmap);

    /**
     * 从Bitmap内存复用池取出bitmap
     * @param width
     * @param height
     * @param config
     * @return
     */
    Bitmap getBitmap(int width,int height,Bitmap.Config config);


}
