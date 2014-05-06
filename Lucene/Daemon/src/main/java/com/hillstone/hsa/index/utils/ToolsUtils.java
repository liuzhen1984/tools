package com.hillstone.hsa.index.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * User: zliu
 * Date: 14-3-19
 * Time: 下午3:40
 * To change this template use File | Settings | File Templates.
 */
public class ToolsUtils {
    /**
     * 获得当前时间，格式自定义
     *
     * @param format
     * @return
     */
    public static String getCurrentDate(String format) {
        Calendar day = Calendar.getInstance();
        day.add(Calendar.DATE, 0);
        SimpleDateFormat sdf = new SimpleDateFormat(format);// "yyyy-MM-dd"
        String date = sdf.format(day.getTime());
        return date;
    }
}
