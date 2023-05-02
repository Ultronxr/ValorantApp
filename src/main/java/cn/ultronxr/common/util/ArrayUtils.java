package cn.ultronxr.common.util;

import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;

/**
 * @author Ultronxr
 * @date 2023/05/02 16:55:23
 * @description
 */
public class ArrayUtils {

    /**
     * 以“不带首尾括号、各值之间以英文逗号+空格分隔”的方式，把 List 转成 String<br/>
     * 例：[a,b,c] ==> "a, b, c"
     * @param list
     * @return
     */
    public static String toString(List<String> list) {
        if(CollectionUtils.isEmpty(list)) {
            return "";
        }
        StringBuilder res = new StringBuilder();
        for(int i = 0; i < list.size(); i++) {
            res.append(list.get(i));
            if(i < list.size()-1) {
                res.append(", ");
            }
        }
        return res.toString();
    }

    /**
     * 以“不带首尾括号、各值之间以英文逗号+空格分隔”的方式，把 String[]数组 转成 String<br/>
     * 例：{a,b,c} ==> "a, b, c"
     * @param array
     * @return
     */
    public static String toString(String[] array) {
        if(null == array || array.length == 0) {
            return "";
        }
        StringBuilder res = new StringBuilder();
        for(int i = 0; i < array.length; i++) {
            res.append(array[i]);
            if(i < array.length-1) {
                res.append(", ");
            }
        }
        return res.toString();
    }

}
