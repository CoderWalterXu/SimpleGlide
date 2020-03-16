package com.xlh.study.simpleglide.resource;

import com.xlh.study.simpleglide.Utils;

/**
 * @author: Watler Xu
 * time:2020/3/13
 * description: 资源对应的唯一key
 * version:0.0.1
 */
public class Key {

    private String key;

    public Key(String key){
        this.key = Utils.getSHA256StrJava(key);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
