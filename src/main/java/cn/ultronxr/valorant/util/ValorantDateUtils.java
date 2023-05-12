package cn.ultronxr.valorant.util;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Date;

/**
 * @author Ultronxr
 * @date 2023/05/12 10:47:32
 * @description 有关 Valorant 商店 的 日期时间工具类（都与今天8点、明天8点相关）
 */
public class ValorantDateUtils {

    /**
     * 检查日期是否合法，合法的条件：<br/>
     *      先把 date ==> dateTime<br/>
     *      今天8点 < dateTime < 明天8点
     * @param date 前端传入的查询日期（yyyy-MM-dd）
     * @return 是否合法
     */
    public static boolean isDateValid(String date) {
        DateTime now = DateUtil.date();
        DateTime dateTime = DateUtil.parse(date)
                .setField(DateField.HOUR_OF_DAY, now.getField(DateField.HOUR_OF_DAY))
                .setField(DateField.MINUTE, now.getField(DateField.MINUTE))
                .setField(DateField.SECOND, now.getField(DateField.SECOND))
                .setField(DateField.MILLISECOND, now.getField(DateField.MILLISECOND));
        DateTime today8AM = DateUtil.date()
                .setField(DateField.HOUR_OF_DAY, 8)
                .setField(DateField.MINUTE, 0)
                .setField(DateField.SECOND, 0)
                .setField(DateField.MILLISECOND, 0);
        DateTime tomorrow8AM = DateUtil.date(
                DateUtils.addDays(DateUtil.date(today8AM).toJdkDate(), 1)
        );
        boolean valid = DateUtil.compare(today8AM, dateTime) < 0
                && DateUtil.compare(dateTime, tomorrow8AM) < 0;
        //log.info("dateTime={}, today8AM={}, tomorrow8AM={}", dateTime.toString(), today8AM.toString(), tomorrow8AM.toString());
        //log.info("valid={}", valid);
        return valid;
    }

    /**
     * 检查当前时间是否过了今天8点
     * @return true - 当前时间 >= 今天8点<br/>
     *          false - 当前时间 < 今天8点
     */
    public static boolean isNowAfterToday8AM() {
        DateTime now = DateUtil.date();
        DateTime today8AM = DateUtil.date()
                .setField(DateField.HOUR_OF_DAY, 8)
                .setField(DateField.MINUTE, 0)
                .setField(DateField.SECOND, 0)
                .setField(DateField.MILLISECOND, 0);
        return DateUtil.compare(now, today8AM) >= 0;
    }

    /**
     * 给 {@code String} 日期增/减天数
     * @param date   日期（yyyy-MM-dd）
     * @param amount 增/减的天数
     * @return 结果日期（yyyy-MM-dd）
     */
    public static String addDays(String date, int amount) {
        Date dateObj = DateUtil.parseDate(date);
        dateObj = DateUtils.addDays(dateObj, amount);
        return DateUtil.date(dateObj).toDateStr();
    }

    /**
     * 获取指定日期的当天8点
     * @param dateStr 指定日期（yyyy-MM-dd）
     * @return 当天8点的日期时间结果（yyyy-MM-dd 08:00:00）
     */
    public static DateTime get8AM(String dateStr) {
        DateTime dateTime = DateTime.of(dateStr, "yyyy-MM-dd");
        dateTime.setField(DateField.HOUR_OF_DAY, 8)
                .setField(DateField.MINUTE, 0)
                .setField(DateField.SECOND, 0)
                .setField(DateField.MILLISECOND, 0);
        return dateTime;
    }

    /**
     * 获取指定日期的两个8点时刻<br/>
     *     时刻 = [指定日期8点, 后一天8点]
     * @param dateStr    指定日期（yyyy-MM-dd）
     * @param autoAdjust 是否开启自动调整<br/>
     *                若开启自动调整会根据当前的时间进行前后一天移动：<br/>
     *                  1. 当前时间 < 今天8点：时刻（前移一天） = [前一天8点, 指定日期8点]<br/>
     *                  2. 当前时间 >= 今天8点：时刻 = [指定日期8点, 后一天8点]
     * @return 两个8点时刻，且 下标0的时刻 < 下标1的时刻
     */
    public static Date[] getDateTimeBaseOn8AM(String dateStr, boolean autoAdjust) {
        Date[] result = new Date[2];
        Date thatDay8AM = get8AM(dateStr);
        if(autoAdjust && !isNowAfterToday8AM()) {
            // 开启自动调整，且 当前时间 < 今天8点 ，那么把结果前移一天
            result[0] = get8AM(addDays(dateStr, -1));
            result[1] = thatDay8AM;
            return result;
        }
        result[0] = thatDay8AM;
        result[1] = get8AM(addDays(dateStr, 1));
        return result;
    }

}
