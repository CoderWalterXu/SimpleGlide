package com.xlh.study.simpleglide;

/**
 * @author: Watler Xu
 * time:2020/3/12
 * description:
 * version:0.0.1
 */
public class WxGildeBuilder {

    /**
     * 创建WxGlide
     * @return
     */
    public WxGlide build(){
        WxRequestManagerRetriver rmt = new WxRequestManagerRetriver();
        WxGlide wxGlide = new WxGlide(rmt);
        return wxGlide;
    }

}
