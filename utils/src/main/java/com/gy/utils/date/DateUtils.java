package com.gy.utils.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by ganyu on 2016/11/2.
 *
 */
public class DateUtils {

    /**
     * 日期转换成Java字符串
     *
     * @param date
     * @return str
     */
    public static String DateToStr(Date date) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = format.format(date);
        return str;
    }

    public static String getTimeInDay () {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        String str = format.format(Calendar.getInstance().getTime());
        return str;
    }

    public static String getTimestampString(Date paramDate)
    {
        String str = null;
        long l = paramDate.getTime();
        if (isSameDay(l))
        {
            Calendar localCalendar = GregorianCalendar.getInstance();
            localCalendar.setTime(paramDate);
            int i = localCalendar.get(Calendar.HOUR_OF_DAY);
            if (i > 17)
                str = "晚上 hh:mm";
            else if ((i >= 0) && (i <= 6))
                str = "凌晨 hh:mm";
            else if ((i > 11) && (i <= 17))
                str = "下午 hh:mm";
            else
                str = "上午 hh:mm";
        }
        else if (isYesterday(l))
        {
            str = "昨天 HH:mm";
        }
        else
        {
            str = "M月d日 HH:mm";
        }
        return new SimpleDateFormat(str, Locale.CHINA).format(paramDate);
    }

    private static boolean isSameDay(long paramLong)
    {
        TimeInfo localTimeInfo = getTodayStartAndEndTime();
        return (paramLong > localTimeInfo.getStartTime()) && (paramLong < localTimeInfo.getEndTime());
    }

    private static boolean isYesterday(long paramLong)
    {
        TimeInfo localTimeInfo = getYesterdayStartAndEndTime();
        return (paramLong > localTimeInfo.getStartTime()) && (paramLong < localTimeInfo.getEndTime());
    }

    public static String DateToStr(Date date, String formatStr) {
        if (null != date) {
            SimpleDateFormat format = new SimpleDateFormat(formatStr);
            String str = format.format(date);
            return str;
        } else {
            return "";
        }
    }

    /**
     * 字符串转换成日期
     *
     * @param str
     * @return date
     */
    public static Date StrToDate(String str) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 字符串转换成日期
     *
     * @param str
     * @return date
     */
    public static Date StrToDate(String str, String formatStr) {

        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        Date date = null;
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 计算两个日期型的时间相差多少时间
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return
     */
    public static String[] twoDateDistance(Date startDate, Date endDate) {
        String[] dates = new String[2];
        String date = "";
        String date2 = "";
        if (startDate == null || endDate == null) {
            return null;
        }
        SimpleDateFormat formatStart = new SimpleDateFormat("yyyy-MM-dd");
        startDate = StrToDate(formatStart.format(startDate));
        long timeLong = endDate.getTime() - startDate.getTime();
        if (timeLong < 60 * 60 * 1000 * 24) {
            date = "今天";
            SimpleDateFormat format = new SimpleDateFormat("E MM-dd");
            date2 = format.format(endDate);
        } else if (timeLong >= 60 * 60 * 1000 * 24
                && timeLong < 60 * 60 * 1000 * 24 * 2) {
            date = "明天";
            SimpleDateFormat format = new SimpleDateFormat("E MM-dd");
            date2 = format.format(endDate);
        } else if (timeLong >= 60 * 60 * 1000 * 24 * 2
                && timeLong < 60 * 60 * 1000 * 24 * 3) {
            date = "后天";
            SimpleDateFormat format = new SimpleDateFormat("E MM-dd");
            date2 = format.format(endDate);
        } else {
            SimpleDateFormat format = new SimpleDateFormat("E");
            date = format.format(endDate);
            SimpleDateFormat format2 = new SimpleDateFormat("MM-dd");
            date2 = format2.format(endDate);
        }
        dates[0] = date;
        dates[1] = date2;
        return dates;
    }

    /**
     * 计算两个日期型的时间相差多少时间
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return
     */
    public static Boolean isEffectiveTime(Date startDate, Date endDate, Integer hours) {
        boolean isEffective = false;
        if (startDate == null || endDate == null) {
            return isEffective;
        }
        long timeLong = endDate.getTime() - startDate.getTime();
        if (hours != null && timeLong >= 60 * 60 * 1000 * hours) {
            isEffective = true;
        }
        return isEffective;
    }

    /**
     * 计算两个日期型的时间相差多少时间
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return
     */
    public static Boolean isOverdueTime(Date startDate, Date endDate) {
        boolean isOverdue = false;
        if (startDate == null || endDate == null) {
            return isOverdue;
        }
        long timeLong = endDate.getTime() - startDate.getTime();
        if (timeLong <= 0) {
            isOverdue = true;
        }
        return isOverdue;
    }

    /**
     * 计算两个日期型的时间相差多少时间
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return
     */
    public static String[] twoDateDistance2(Date startDate, Date endDate) {
        String[] dates = new String[3];
        if (startDate == null || endDate == null) {
            return null;
        }
        long timeLong = endDate.getTime() + (1000 * 60 * 60 * 24) - startDate.getTime();
        if (timeLong < 1000 * 60) {
            return null;
        }
        String day = "" + (int) (timeLong / (1000 * 60 * 60 * 24));
        String hours = "" + (int) (timeLong % (1000 * 60 * 60 * 24) / (1000 * 60 * 60));
        String min = "" + (int) (timeLong % (1000 * 60 * 60) / (1000 * 60));
        dates[0] = day;
        dates[1] = hours;
        dates[2] = min;
        return dates;
    }

    public static TimeInfo getTodayStartAndEndTime()
    {
        Calendar localCalendar1 = Calendar.getInstance();
        localCalendar1.set(Calendar.HOUR_OF_DAY, 0);
        localCalendar1.set(Calendar.MINUTE, 0);
        localCalendar1.set(Calendar.SECOND, 0);
        localCalendar1.set(Calendar.MILLISECOND, 0);
        Date localDate1 = localCalendar1.getTime();
        long l1 = localDate1.getTime();
        SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss S");
        Calendar localCalendar2 = Calendar.getInstance();
        localCalendar2.set(Calendar.HOUR_OF_DAY, 23);
        localCalendar2.set(Calendar.MINUTE, 59);
        localCalendar2.set(Calendar.SECOND, 59);
        localCalendar2.set(Calendar.MILLISECOND, 999);
        Date localDate2 = localCalendar2.getTime();
        long l2 = localDate2.getTime();
        TimeInfo localTimeInfo = new TimeInfo();
        localTimeInfo.setStartTime(l1);
        localTimeInfo.setEndTime(l2);
        return localTimeInfo;
    }

    public static TimeInfo getYesterdayStartAndEndTime()
    {
        Calendar localCalendar1 = Calendar.getInstance();
        localCalendar1.add(Calendar.DAY_OF_MONTH, -1);
        localCalendar1.set(Calendar.HOUR_OF_DAY, 0);
        localCalendar1.set(Calendar.MINUTE, 0);
        localCalendar1.set(Calendar.SECOND, 0);
        localCalendar1.set(Calendar.MILLISECOND, 0);
        Date localDate1 = localCalendar1.getTime();
        long l1 = localDate1.getTime();
        Calendar localCalendar2 = Calendar.getInstance();
        localCalendar2.add(Calendar.DAY_OF_MONTH, -1);
        localCalendar2.set(Calendar.HOUR_OF_DAY, 23);
        localCalendar2.set(Calendar.MINUTE, 59);
        localCalendar2.set(Calendar.SECOND, 59);
        localCalendar2.set(Calendar.MILLISECOND, 999);
        Date localDate2 = localCalendar2.getTime();
        long l2 = localDate2.getTime();
        TimeInfo localTimeInfo = new TimeInfo();
        localTimeInfo.setStartTime(l1);
        localTimeInfo.setEndTime(l2);
        return localTimeInfo;
    }

    public static boolean isCloseEnough(long paramLong1, long paramLong2)
    {
        long l = paramLong1 - paramLong2;
        if (l < 0L)
            l = -l;
        return l < 30000L;
    }
}
