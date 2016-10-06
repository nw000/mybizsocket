package com.dx168.bizsocket.core;

/**
 * Created by tong on 16/10/5.
 */
public enum CacheStrategy {
    //永久使用
    persistence,
    //过期时间
    relative_millis,
    //使用的次数
    counter,
    //先使用再到服务器去获取更新
    use_and_refresh,
    //发现指定的命令号结果前一直有效
    use_util_conflict
}
