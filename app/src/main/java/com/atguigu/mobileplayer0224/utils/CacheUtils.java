package com.atguigu.mobileplayer0224.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.atguigu.mobileplayer0224.service.MusicPlayService;

/**
 * 作者：尚硅谷-杨光福 on 2016/11/23 14:22
 * 微信：yangguangfu520
 * QQ号：541433511
 * 作用：缓存工具类
 */
public class CacheUtils {
    /**
     * 保持文本数据
     * @param mContext 上下文
     * @param key
     * @param value
     */
    public static void putString(Context mContext, String key, String value) {
        SharedPreferences sp = mContext.getSharedPreferences("atguigu",Context.MODE_PRIVATE);
        sp.edit().putString(key,value).commit();
    }

    /**
     * 得到文本数据
     * @param mContext
     * @param key
     * @return
     */
    public static String getString(Context mContext, String key) {
        SharedPreferences sp = mContext.getSharedPreferences("atguigu",Context.MODE_PRIVATE);
        return sp.getString(key,"");
    }

    /**
     * 得到播放模式
     * @param context
     * @param key
     * @return
     */
    public static int getPlaymdoe(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences("atguigu",Context.MODE_PRIVATE);
        return sp.getInt(key, MusicPlayService.REPEAT_NORMAL);
    }

    /**
     * 保持播放模式
     * @param context
     * @param key
     * @param value
     */
    public static void putPlaymode(Context context, String key, int value) {
        SharedPreferences sp = context.getSharedPreferences("atguigu",Context.MODE_PRIVATE);
        sp.edit().putInt(key,value).commit();
    }
}
