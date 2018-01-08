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

    /**
     *  日期专字符串
     * @param date 日期
     * @param formatStr
     *   "yyyy-MM-dd",
     *   "yyyy-MM-dd HH:mm",
     *   "yyyy-MM-dd HH:mmZ",
     *   "yyyy-MM-dd HH:mm:ss.SSSZ",
     *   "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
     * @return 对应格式日期字符串
     */
    public static String DateToStr(Date date, String formatStr) {
        if (null != date) {
            SimpleDateFormat format = new SimpleDateFormat(formatStr, Locale.getDefault());
            return format.format(date);
        } else {
            return "";
        }
    }

    /**
     * 字符串转换成日期
     *   "yyyy-MM-dd",
     *   "yyyy-MM-dd HH:mm",
     *   "yyyy-MM-dd HH:mmZ",
     *   "yyyy-MM-dd HH:mm:ss.SSSZ",
     *   "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
     */
    public static Date StrToDate(String str, String formatStr) {

        SimpleDateFormat format = new SimpleDateFormat(formatStr, Locale.getDefault());
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
        SimpleDateFormat formatStart = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        startDate = StrToDate(formatStart.format(startDate), "yyyy-MM-dd");
        long timeLong = endDate.getTime() - startDate.getTime();
        if (timeLong < 60 * 60 * 1000 * 24) {
            date = "今天";
            SimpleDateFormat format = new SimpleDateFormat("E MM-dd", Locale.getDefault());
            date2 = format.format(endDate);
        } else if (timeLong >= 60 * 60 * 1000 * 24
                && timeLong < 60 * 60 * 1000 * 24 * 2) {
            date = "明天";
            SimpleDateFormat format = new SimpleDateFormat("E MM-dd", Locale.getDefault());
            date2 = format.format(endDate);
        } else if (timeLong >= 60 * 60 * 1000 * 24 * 2
                && timeLong < 60 * 60 * 1000 * 24 * 3) {
            date = "后天";
            SimpleDateFormat format = new SimpleDateFormat("E MM-dd", Locale.getDefault());
            date2 = format.format(endDate);
        } else {
            SimpleDateFormat format = new SimpleDateFormat("E", Locale.getDefault());
            date = format.format(endDate);
            SimpleDateFormat format2 = new SimpleDateFormat("MM-dd", Locale.getDefault());
            date2 = format2.format(endDate);
        }
        dates[0] = date;
        dates[1] = date2;
        return dates;
    }

    /**
     * 计算两个日期型的时间是否相差指定小时输时间
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return .
     */
    public static Boolean isEffectiveTime(Date startDate, Date endDate, Integer hours) {
        boolean isEffective = false;
        if (startDate == null || endDate == null) {
            return false;
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
     * @return .
     */
    public static Boolean isOverdueTime(Date startDate, Date endDate) {
        boolean isOverdue = false;
        if (startDate == null || endDate == null) {
            return false;
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
     * @return .
     */
    public static String[] twoDateDistance2(Date startDate, Date endDate) {
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
        String[] dates = new String[3];
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

    /**
     * 计算当前时间和指定时间（24小时之内）相差的毫秒数
     * @return 毫秒数
     */
    public static long getIntervalToTimeOfDay (int targetHour, int targetMin, int targetSec) {
        Calendar calendar = Calendar.getInstance();
        long currentMillisec = calendar.getTimeInMillis();
        calendar.set(Calendar.HOUR_OF_DAY, targetHour);
        calendar.set(Calendar.MINUTE, targetMin);
        calendar.set(Calendar.SECOND, targetSec);
        long targetMillisec = calendar.getTimeInMillis();
        long interval = targetMillisec - currentMillisec;

        return interval > 0? interval : interval + 24 * 60 * 60 * 1000;
    }

    /**
     * 计算endTime是否在startTime 的 30s 之内 ，例如聊天，时间比较长的时候需要显示日期
     * @param endTime .
     * @param startTime .
     * @return .
     */
    public static boolean isCloseEnough(long endTime, long startTime)
    {
        long l = endTime - startTime;
        if (l < 0L) l = -l;
        return l < 30000L;
    }
}
