package com.zls.jenkinsplugin.util;

import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.PrimitiveIterator;

/**
 * String工具类
 *
 * @author zhangliansheng
 * @date 2019/11/3
 */
public class StringUtil extends StringUtils {
    public static boolean isBlank(String arg) {
        return isEmpty(arg) || "".equals(arg.trim());
    }

    public static boolean isNotBlank(String arg) {
        return !isBlank(arg);
    }

    /**
     * 将字符串转换成map
     * 字符串格式: Overall coverage: class: 7, method: 2, line: 2, branch: 1, instruction: 5
     *
     * @param
     * @return
     * @author zhangliansheng
     * @date 2019/11/4
     */
    public static Map<String, String> formatStrToMap(String txt, String startStr) {
        txt = txt.substring(txt.indexOf(startStr));
        try {
            return doFormatStr2Map(txt);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public static Map<String, String> formatStrToMap(String txt, int startIndex) {
        txt = txt.substring(startIndex);
        try {
            return doFormatStr2Map(txt);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    private static Map<String, String> doFormatStr2Map(String txt) throws Exception {
        txt = txt.trim();
        Map ret = new HashMap();
        String[] kvs = txt.split(",");
        for (int i = 0; i < kvs.length; i++) {
            String ky = kvs[i];
            String key = ky.trim().split(": ")[0].trim();
            String value = ky.trim().split(": ")[1].trim();
            ret.put(key, value);
        }
        return ret;
    }
}
