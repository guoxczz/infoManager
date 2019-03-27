package com.guoxc.info.utils;


import com.guoxc.info.exception.GeneralException;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 时间工具类
 */
public final class DateUtil {
    private final static Logger logger = LoggerFactory.getLogger(DateUtil.class);

    private final static String dateFormat = "yyyy-MM-dd";

    /**
     * Private Constructor
     **/
    private DateUtil() {
    }

    public static long getDistanceTimes(String str1, String str2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date one;
        Date two;
        long day = 0;
        long hour = 0;
        long min = 0;
        // long sec = 0;
        try {
            one = df.parse(str1);
            two = df.parse(str2);
            long time1 = one.getTime();
            long time2 = two.getTime();
            long diff;
            if (time1 < time2) {
                diff = time2 - time1;
            } else {
                diff = time1 - time2;
            }
            day = diff / (24 * 60 * 60 * 1000);
            hour = diff / (60 * 60 * 1000) - day * 24;
            min = diff / (60 * 1000) - day * 24 * 60 - hour * 60;
            // sec = (diff/1000-day*24*60*60-hour*60*60-min*60);
        } catch (ParseException e) {
            logger.error("!!!!!!!!!!", "", e);
        }
        // long[] times = {day, hour, min, sec};
        return min;
    }
    /**
     * 获取当前时间
     *
     * @return Timestamp对象
     */
    public static Timestamp getCurrontTime() {
        Timestamp sqlTimestamp = new Timestamp(new Date().getTime());
        return sqlTimestamp;
    }

    /**
     * 将Date类型转换成String类型
     *
     * @param date Date对象
     * @return 形如:"yyyy-MM-dd HH:mm:ss"
     */
    public static String date2String(Date date) {
        return date2String(date, DATE_PATTERN.YYYY_MM_DD_HH_MM_SS);
    }
    /**
     * 将String类型转换成Date类型
     * @param date date Date对象
     * @param pattern String格式类型
     * @return
     */
    public static Date string2Date(String date, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        try {
            return format.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 将Date按格式转化成String
     *
     * @param date    Date对象
     * @param pattern 日期类型
     * @return String
     */
    public static String date2String(Date date, String pattern) {
        if (date == null || pattern == null) {
            return null;
        }
        return new SimpleDateFormat(pattern).format(date);
    }

    /**
     * 将String类型转换成Date类型
     *
     * @param date Date对象
     * @return
     */
    public static Date string2Date(String date) {
        SimpleDateFormat format = new SimpleDateFormat(DATE_PATTERN.YYYY_MM_DD_HH_MM_SS);
        try {
            return format.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 获取某日期N天后的日期
     *
     * @param datestr
     * @param day
     * @return
     */
    public static Date getBeforeAfterDate(String datestr, int day) {
        SimpleDateFormat df = new SimpleDateFormat(DATE_PATTERN.YYYY_MM_DD_HH_MM_SS);
        java.sql.Date olddate = null;
        try {
            df.setLenient(false);
            olddate = new java.sql.Date(df.parse(datestr).getTime());
        } catch (ParseException e) {
            throw new RuntimeException("日期转换错误");
        }
        Calendar cal = new GregorianCalendar();
        cal.setTime(olddate);

        int Year = cal.get(Calendar.YEAR);
        int Month = cal.get(Calendar.MONTH);
        int Day = cal.get(Calendar.DAY_OF_MONTH);

        int NewDay = Day + day;

        cal.set(Calendar.YEAR, Year);
        cal.set(Calendar.MONTH, Month);
        cal.set(Calendar.DAY_OF_MONTH, NewDay);

        return new Date(cal.getTimeInMillis());
    }

    /**
     * 计算两个日期差的天数
     *
     * @param fDate
     * @param oDate
     * @return
     */
    public static int daysBetween(Date fDate, Date oDate) {
        Calendar cNow = Calendar.getInstance();
        Calendar cReturnDate = Calendar.getInstance();
        cNow.setTime(oDate);
        cReturnDate.setTime(fDate);
        cNow.set(Calendar.HOUR_OF_DAY, 0);
        cNow.set(Calendar.MINUTE, 0);
        cNow.set(Calendar.SECOND, 0);
        cReturnDate.set(Calendar.HOUR_OF_DAY, 0);
        cReturnDate.set(Calendar.MINUTE, 0);
        cReturnDate.set(Calendar.SECOND, 0);
        long todayMs = cNow.getTimeInMillis();
        long returnMs = cReturnDate.getTimeInMillis();
        long intervalMs = todayMs - returnMs;
        return (int) (intervalMs / (1000 * 86400));
    }

    /**
     * @return
     * @Description: 获取当前日期的前一天
     * @ReturnType String
     * @author: liyl
     * @Created 2015年11月13日 下午5:11:14
     */
    public static Date currentBeforeDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        return calendar.getTime();
    }

    /**
     * @return
     * @Description: 获取当前日期的后一天
     * @ReturnType Date
     * @author: liyl
     * @Created 2015年11月13日 下午5:14:54
     */
    public static Date currentNextDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    /**
     * 获取时间的星期
     *
     * @param dt
     * @return
     */
    public static String getWeekOfDate(Date dt) {
        String[] weekDays = {"SUND_FLAG", "MOND_FLAG", "TUES_FLAG", "WEDN_FLAG", "THUR_FLAG", "FRID_FLAG", "SATU_FLAG"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) {
            w = 0;
        }
        return weekDays[w];
    }

    /**
     * 时间比大小
     *
     * @param DATE1
     * @param DATE2
     * @param pattern
     * @return
     */
    public static int compareDate(String DATE1, String DATE2, String pattern) {

        DateFormat df = new SimpleDateFormat(pattern);
        try {
            Date dt1 = df.parse(DATE1);
            Date dt2 = df.parse(DATE2);
            if (dt1.getTime() > dt2.getTime()) {
                System.out.println("dt1 在dt2前");
                return 1;
            } else if (dt1.getTime() < dt2.getTime()) {
                System.out.println("dt1在dt2后");
                return -1;
            } else {
                return 0;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    /**
     * 在一个时间上加上或减去分钟
     *
     * @param date long
     * @param i    int
     * @return Date
     */
    public static Date addOrMinusMinutes(Date date, int i) {
        Date rtn = null;
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(GregorianCalendar.MINUTE, i);
        rtn = cal.getTime();
        return rtn;
    }

    public static void main(String[] args) throws Exception {
        System.out.println(DateUtil.format(DateUtil.getDateByString(new Date() + ""), "yyyyMM"));
        // System.out.println(DateUtil.date2String(getBeforeAfterDate(DateUtil.date2String(new Date()), 90)));
    }

    /**
     * 日期格式
     **/
    public interface DATE_PATTERN {
        String HHMMSS = "HHmmss";
        String HH_MM_SS = "HH:mm:ss";
        String HH_MM = "HH:mm";
        String YYYY = "yyyy";
        String YYYYMMDD = "yyyyMMdd";
        String YYYYMM = "yyyyMM";
        String YYYY_MM_DD = "yyyy-MM-dd";
        String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
        String YYYYMMDDHHMMSSSSS = "yyyyMMddHHmmssSSS";
        String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    }


    /**
     * 按照指定格式返回格式好的当前日期
     *
     * @param dateFormat 默认yyyy-MM-dd
     * @return
     */
    public static String getCurrentDateString(String dateFormat) {
        return DateUtil.format(new Date(), dateFormat);
    }

    /**
     * 说明 将日期格式化字符串，为null的返回空字符串
     *
     * @param date
     * @return
     */
    public static String format(Date date) {
        if (null == date) {
            return "";
        }
        SimpleDateFormat sf = new SimpleDateFormat(dateFormat);
        return sf.format(date);
    }

    /**
     * 说明 将日期格式化字符串，为null的返回空字符串
     *
     * @param date       日期
     * @param dateFormat 格式化字符串，比如：yyyy-MM-dd
     * @return
     */
    public static String format(Date date, String dateFormat) {
        if (null == dateFormat || "".equals(dateFormat)) {
            return DateUtil.format(date);
        }
        if (null == date) {
            return "";
        }
        SimpleDateFormat sf = new SimpleDateFormat(dateFormat);
        return sf.format(date);
    }

    /**
     * @param source 要进行解析的源字符串
     * @return
     * @说明 将指定的字符串格解析成日期类型，格式默认为：yyyy-MM-dd
     */
    public static Date parase(String source) {
        SimpleDateFormat sf = new SimpleDateFormat(dateFormat);
        try {
            return sf.parse(source);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
        }
        return null;
    }

    /**
     * @param source     要进行解析的源字符串
     * @param dateFormat 要解析的日期格式。
     * @return
     * @说明 将指定的字符串格解析成日期类型 例：如果日期source=20131210,则dateFormat应为：yyyyMMdd,两个应对应
     */
    public static Date parase(String source, String dateFormat) {
        SimpleDateFormat sf = new SimpleDateFormat(dateFormat);
        try {
            return sf.parse(source);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
        }
        return null;
    }

    /**
     * @param date
     * @param days
     * @说明 对指定的日期增加或减少指定的天数
     */
    public static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        cal.add(Calendar.DAY_OF_MONTH, days);

        return cal.getTime();
    }

    /**
     * @param date
     * @param days
     * @说明 对指定的日期增加或减少指定的天数
     */
    public static Calendar addDays(Calendar date, int days) {

        date.add(Calendar.DAY_OF_MONTH, days);

        return date;
    }

    /**
     * @param date
     * @param months
     * @return
     * @说明 对指定的日期增加或减少指定的月数
     */
    public static Date addMonths(Date date, int months) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        cal.add(Calendar.MONTH, months);

        return cal.getTime();
    }

    /**
     * @param date
     * @param months
     * @return
     * @说明 对指定的日期增加或减少指定的月数
     */
    public static Calendar addMonths(Calendar date, int months) {
        date.add(Calendar.MONTH, months);

        return date;
    }

    /**
     * @param date
     * @param hours
     * @return
     * @说明 对指定的日期增加或减少指定的小时数
     */
    public static Date addHours(Date date, int hours) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        cal.add(Calendar.HOUR_OF_DAY, hours);

        return cal.getTime();
    }

    /**
     * @param date
     * @param hours
     * @return
     * @说明 对指定的日期增加或减少指定的小时数
     */
    public static Calendar addHours(Calendar date, int hours) {
        date.add(Calendar.HOUR_OF_DAY, hours);
        return date;
    }

    /**
     * @return
     * @说明 以字符串形式返回当前时间的毫秒数
     */
    public static String getTimeMillions() {
        Calendar cal = Calendar.getInstance();
        long lt = cal.getTimeInMillis();

        return String.valueOf(lt);
    }

    /**
     * 获取当前月的第一天
     *
     * @return 当前月的第一天
     */
    public static String getMonthFirstDay() {
        SimpleDateFormat df = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        Date theDate = calendar.getTime();

        GregorianCalendar gcLast = (GregorianCalendar) Calendar.getInstance();
        gcLast.setTime(theDate);
        gcLast.set(Calendar.DAY_OF_MONTH, 1);
        String day_first = df.format(gcLast.getTime());
        StringBuffer str = new StringBuffer().append(day_first);
        return str.toString();

    }

    /**
     * 获取当前月的最后一天
     *
     * @return 当前月的最后一天
     */
    public static String getMonthLastDay() {

        Calendar calendar = Calendar.getInstance();
        // 最后一天
        int maxday = calendar.getActualMaximum(Calendar.DATE);
        calendar.set(Calendar.DATE, maxday);

        SimpleDateFormat df = new SimpleDateFormat(dateFormat);
        Date theDate = calendar.getTime();
        String s = df.format(theDate);
        StringBuffer str = new StringBuffer().append(s);
        return str.toString();

    }

    /**
     * 获取当前月的第一天，精确到时分秒
     *
     * @return 当前月的第一天，精确到时分秒
     */
    public static Date getFirstDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date date = calendar.getTime();
        return date;

    }

    /**
     * 获得往数据库字段类型为Date型时，插入的时间
     *
     * @param date       默认为当前日期，如果为空时 方法会自动new Date()
     * @param dateFormat 默认为yyyy-MM-dd
     * @return
     */
    public static java.sql.Date paraseSqlDate(String date, String dateFormat) {
        try {
            if (date == null || date.length() == 0) {
                return new java.sql.Date(new Date().getTime());
            } else {
                if (dateFormat == null) {
                    dateFormat = DateUtil.dateFormat;
                }
                SimpleDateFormat sf = new SimpleDateFormat(dateFormat);
                Date d = sf.parse(date);
                return new java.sql.Date(d.getTime());
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return null;
    }

    /**
     * 将日期按照特定格式转换成字符串
     *
     * @param date
     * @param pattern
     * @return
     */
    public static String formatString(Date date, String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(date);
    }

    /**
     * 将日期字符串转换为日期
     *
     * @param strDate
     * @param mask
     * @return
     * @throws ParseException
     */
    public static Timestamp convertStringToTimestamp(String strDate, String mask) throws ParseException {
        SimpleDateFormat df;
        Date date = null;
        df = new SimpleDateFormat(mask);
        try {
            date = df.parse(strDate);
            return new Timestamp(date.getTime());
        } catch (ParseException pe) {
            throw new ParseException(pe.getMessage(), pe.getErrorOffset());
        }
    }

    /**
     * 月份相加 add by yuanjq
     *
     * @param timest1
     * @param month
     * @return
     */
    public static Timestamp DateAddMonth(Timestamp timest1, int month) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(timest1);
        cal.add(Calendar.MONTH, month);
        return new Timestamp(cal.getTimeInMillis());
    }

    /**
     * 对输入的日期进行格式化, 如果输入的日期是null则返回空串.
     * FrameWork使用
     *
     * @param dtDate      java.sql.Timestamp 需要进行格式化的日期字符串
     * @param strFormatTo String 要转换的日期格式
     * @return String 经过格式化后的字符串
     */
    public static String getFormattedDate(Timestamp dtDate,
                                          String strFormatTo) {
        if (dtDate == null) {
            return "";
        }
        if (dtDate.equals(new java.sql.Timestamp(0))) {
            return "";
        }
        String newStrFormateTo = strFormatTo;
        newStrFormateTo = newStrFormateTo.replace('/', '-');
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
        if (Integer.parseInt(formatter.format(dtDate)) < 1900) {
            return "";
        } else {
            formatter = new SimpleDateFormat(newStrFormateTo);
            return formatter.format(dtDate);
        }
    }

    public static String getCurrentDateYMR() {
        SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMDD");
        return sdf.format(System.currentTimeMillis());
    }

    public static Date getDate(String strDate, String strFormat) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat(strFormat);
        Date date = format.parse(strDate);

        return date;
    }

    public static Timestamp getTimestamp(String strDate, String strFormat) throws ParseException {
        Date date = getDate(strDate, strFormat);
        Timestamp timestamp = new Timestamp(date.getTime());

        return timestamp;
    }

    public static String getStringDate(Date date, String strFormat) throws ParseException {
        if (date == null) {
            return "";
        }
        SimpleDateFormat format = new SimpleDateFormat(strFormat);
        String strDate = format.format(date);

        return strDate;
    }

    public static String getStringTimestamp(Timestamp timestamp, String strFormat) throws ParseException {
        if (timestamp == null) {
            return "";
        }
        String strTimestamp = getStringDate(timestamp, strFormat);
        return strTimestamp;
    }


    public static Timestamp addMonth(Timestamp timestamp, int months) throws ParseException {
        GregorianCalendar grc = new GregorianCalendar();
        grc.setTime(timestamp);

        grc.add(GregorianCalendar.MONTH, months);

        return new Timestamp(grc.getTime().getTime());
    }

    public static Timestamp addYear(Timestamp timestamp, int years) throws ParseException {
        GregorianCalendar grc = new GregorianCalendar();
        grc.setTime(timestamp);

        grc.add(GregorianCalendar.YEAR, years);

        return new Timestamp(grc.getTime().getTime());
    }

    public static Timestamp addDay(Timestamp timestamp, int days) throws ParseException {
        GregorianCalendar grc = new GregorianCalendar();
        grc.setTime(timestamp);

        grc.add(GregorianCalendar.DAY_OF_MONTH, days);

        return new Timestamp(grc.getTime().getTime());
    }

    public static Timestamp addHour(Timestamp timestamp, int hours) throws ParseException {
        GregorianCalendar grc = new GregorianCalendar();
        grc.setTime(timestamp);

        grc.add(GregorianCalendar.HOUR_OF_DAY, hours);

        return new Timestamp(grc.getTime().getTime());
    }

    public static Timestamp addMinute(Timestamp timestamp, int minutes) throws ParseException {
        GregorianCalendar grc = new GregorianCalendar();
        grc.setTime(timestamp);

        grc.add(GregorianCalendar.MINUTE, minutes);

        return new Timestamp(grc.getTime().getTime());
    }

    public static Timestamp addSecond(Timestamp timestamp, int seconds) throws ParseException {
        GregorianCalendar grc = new GregorianCalendar();
        grc.setTime(timestamp);

        grc.add(GregorianCalendar.SECOND, seconds);

        return new Timestamp(grc.getTime().getTime());
    }

    public static String getTime(String time, String strFormat) throws ParseException {
        Timestamp endLogDateFormated = getTimestamp(time, strFormat);
        String sTime = getStringTimestamp(endLogDateFormated, "yyyyMMdd");
        return sTime;
    }

    public static String getCurrentDateYMRHMS() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(System.currentTimeMillis());
    }
    public static String getCurrentDateYYYYMM(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        return sdf.format(System.currentTimeMillis());
    }
    public static String getTimeNew(String time, String strFormat) throws ParseException {
        Timestamp endLogDateFormated = getTimestamp(time, strFormat);
        String sTime = getStringTimestamp(endLogDateFormated, "yyyy-MM-dd HH:mm:ss");
        return sTime;
    }

    /**
     * 根据传入的日期字符串转换成相应的日期对象，
     * 如果字符串为空或不符合日期格式，则返回当前时间。
     * FrameWork使用
     *
     * @param strDate String 日期字符串
     * @return java.sql.Timestamp 日期对象
     */
    public static java.sql.Timestamp getDateByString(String strDate) throws GeneralException {
        if (strDate.trim().equals("")) {
            return getCurrentDate();
        }
        try {
            strDate = getFormattedDate(strDate, "yyyy-MM-dd HH:mm:ss") +
                    ".000000000";
            return java.sql.Timestamp.valueOf(strDate);
        } catch (Exception ex) {
            logger.error("转换时间异常", ex);
            return getCurrentDate();
        }
    }

    //获取当前数据库时间
    public static Timestamp getCurrentDate() throws GeneralException {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss" + ".0");
            return Timestamp.valueOf(formatter.format(new Date()));
        } catch (Exception e) {
            throw new GeneralException(e.getMessage());
        }
    }

    /**
     * 对输入的日期字符串进行格式化,
     * 如果输入的是0000/00/00 00:00:00则返回空串.
     * FrameWork使用
     *
     * @param strDate     String 需要进行格式化的日期字符串
     * @param strFormatTo String 要转换的日期格式
     * @return String 经过格式化后的字符串
     */
    public static String getFormattedDate(String strDate, String strFormatTo) {
        if (strDate == null || strDate.trim().equals("")) {
            return "";
        }
        strDate = strDate.replace('/', '-');
        strFormatTo = strFormatTo.replace('/', '-');
        if (strDate.equals("0000-00-00 00:00:00") ||
                strDate.equals("1800-01-01 00:00:00")) {
            return "";
        }
        String formatStr = strFormatTo; //"yyyyMMdd";
        if (strDate.trim().equals("")) { //(strDate == null) ||
            return "";
        }
        switch (strDate.trim().length()) {
            case 6:
                if (strDate.substring(0, 1).equals("0")) {
                    formatStr = "yyMMdd";
                } else {
                    formatStr = "yyyyMM";
                }
                break;
            case 8:
                formatStr = "yyyyMMdd";
                break;
            case 10:
                if (strDate.indexOf("-") == -1) {
                    formatStr = "yyyy/MM/dd";
                } else {
                    formatStr = "yyyy-MM-dd";
                }
                break;
            case 11:
                if (strDate.getBytes().length == 14) {
                    formatStr = "yyyy年MM月dd日";
                } else {
                    return "";
                }
                break;
            case 14:
                formatStr = "yyyyMMddHHmmss";
                break;
            case 19:
                if (strDate.indexOf("-") == -1) {
                    formatStr = "yyyy/MM/dd HH:mm:ss";
                } else {
                    formatStr = "yyyy-MM-dd HH:mm:ss";
                }
                break;
            case 21:
                if (strDate.indexOf("-") == -1) {
                    formatStr = "yyyy/MM/dd HH:mm:ss.S";
                } else {
                    formatStr = "yyyy-MM-dd HH:mm:ss.S";
                }
                break;
            default:
                return strDate.trim();
        }
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(formatStr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(formatter.parse(strDate));
            formatter = new SimpleDateFormat(strFormatTo);
            return formatter.format(calendar.getTime());
        } catch (Exception e) {
            //Common.printLog("转换日期字符串格式时出错;" + e.getMessage());
            return "";
        }
    }

    public static String dealWechatDate(String date) {
        String result;
        if (date == null || "".equals(date)) {
            result = date;
        } else if (isInteger(date)) {
            result = date.substring(0, 4).concat("-");
            if (date.length() == 6) {
                result = result.concat("0").concat(date.substring(4, 5)).concat("-0").concat(date.substring(5, 6));
            } else if (date.length() == 8) {
                result = result.concat(date.substring(4, 6)).concat("-").concat(date.substring(6, 8));
            } else {
                result = date;
            }
        } else if (date.indexOf(".") > -1) {
            result = date.replace(".", "-");
        } else if (date.indexOf("年") > -1) {
            result = date.replace("年", "-").replace("月", "-").replace("日", "");
        } else if (date.indexOf("-") > -1) {
            result = date.replace("年", "-").replace("月", "-").replace("日", "");
        } else {
            result = date;
        }
        return result;
    }

    /*
     * 判断是否为整数
     * @param str 传入的字符串
     * @return 是整数返回true,否则返回false
     */

    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    public static long betweenSecond(Date start, Date end) {
        return (end.getTime() - start.getTime()) / 1000;
    }

    /**
     * 出生日期处理
     * @Title: processBirthday
     * @param birthday
     * @param formatStr
     * @return
     */


    public static String processBirthday(String birthday, String formatStr) {
        String dateStr = birthday;
        try {
            if (!StringUtil.isNull(dateStr)) {
                dateStr = dateStr.replace("年", "").replace("月", "").replace("日", "").replace("-", "").replace(" ", "")
                        .trim();
                if (dateStr.length() == 6) {
                    //201654-->20160504
                    dateStr = dateStr.substring(0, 4) + "0" + dateStr.substring(4, 5) + "0" + dateStr.substring(5);
                }
                dateStr = DateUtil.getStringTimestamp(DateUtil.getTimestamp(dateStr, "yyyyMMdd"), formatStr);
            }
        } catch (Exception e) {
            logger.error("processCustInfo birthday=" + dateStr, e);
        }
        return dateStr;
    }

    /**
     * 计算oldTime和当前时间时间差
     * @Title: calculateTime
     * @param oldTime
     * @return
     * @throws ParseException
     */
    public static boolean calculateTime(String oldTime) throws ParseException {
        logger.info("calculateTime oldTime ===="+oldTime);
        boolean rtn = false;
        try{
            Calendar calendar = Calendar.getInstance();
            Calendar oldCalendar = Calendar.getInstance();
            SimpleDateFormat dFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            String nowTime = getCurrDate("yyyyMMddHHmmss");
            logger.info("calculateTime nowTime="+nowTime);
            Date nowDate = dFormat.parse(nowTime);
            Date oldDate = dFormat.parse(oldTime);
            calendar.setTime(nowDate);
            oldCalendar.setTime(oldDate);
            rtn = (calendar.getTime().getTime()-oldCalendar.getTime().getTime())/1000 > 300;
        }catch(Exception e){
            logger.error("calculateTime error", e);
            return false;
        }
        logger.info("rtn boolean = "+rtn);
        return rtn;
    }
    /**
     * @param patternStr String
     * @return String
     */
    public static String getCurrDate(String patternStr) {
        SimpleDateFormat formatter = new SimpleDateFormat(patternStr);
        Date date = new Date();
        return formatter.format(date);
    }
}