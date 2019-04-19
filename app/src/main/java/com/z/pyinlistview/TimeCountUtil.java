package com.z.pyinlistview;

import android.util.Log;

/**
 * 计算代码耗时工具类
 */
class TimeCountUtil {
    private long start;

    void startCalculate(){
        start = System.nanoTime();
    }

    void endCalculate(){
        long end = System.nanoTime();
        Log.e("代码执行耗时",""+(end-start)/(1000*1000) +"ms");
    }
}
