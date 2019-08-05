package com.pl23k.restaurant.utils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期时间的工具类
 * Created by Lucius on 2017/6/9.
 */
public class DateUtil {
    /**
     * 取当前整型时间
     *
     * @return
     */
    public static long getLongTime() {
        long time = System.currentTimeMillis();
        return time;
    }

    /**
     * 取Java整理时间
     *
     * @param time
     * @return
     */
    public static long toJavaTime(long time) {
        long t = 0;
        try {
            int count = String.valueOf(time).length();
            if (10 == count) {
                t = time * 1000;
            }else{
                t = time;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }
    /**
     * 格式化时间Date yyyy-MM-dd HH:mm:ss
     *
     * @param date
     * @return
     */
    public final static String formatDateTime(Date date) {
        if (null == date) return "";
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//格式化时间输出
        return df.format(date);
    }

    /**
     * 格式化时间Date，yyyy-MM-dd
     * @param date
     * @return
     */
    public final static String formatDate(Date date){
        if (null == date) return "";
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//格式化时间输出
        return df.format(date);
    }

    /**
     * 把字符串日期转换为日期类
     * @param dateStr
     * @return
     */
    public final static Date formatStringDate(String dateStr){
        Date date;
        try {
            if(null == dateStr || "".equals(dateStr)) return new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//格式化时间输出
            date = df.parse(dateStr);
        }catch (Exception e){
            e.printStackTrace();
//            ExceptionLogUtil.logWriteFile("DateUtil","formatStringDate",e);
            date = new Date();
        }
        return date;
    }

    /**
     * 把字符串时间转换为日期类
     * @param timeStr
     * @return
     */
    public final static Date formatStringTime(String timeStr){
        Date date;
        try {
            if(null == timeStr || "".equals(timeStr)) return new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//格式化时间输出
            date = df.parse(timeStr);
        }catch (Exception e){
            e.printStackTrace();
//            ExceptionLogUtil.logWriteFile("DateUtil","formatStringDate",e);
            date = new Date();
        }
        return date;
    }

    /**
     * 格式化时间Timestamp
     *
     * @param date
     * @return
     */
    public final static String formatDate(Timestamp date) {
        if (null == date) return "";
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//格式化时间输出
        return df.format(date);
    }

    /**
     * 格式化时间Data
     * formatDate(new Date(),"yyyy-MM-dd HH:mm:ss")
     *
     * @param date
     * @param format
     * @return
     */
    public final static String formatDate(Date date, String format) {
        if (null == date) return "";
        SimpleDateFormat df = new SimpleDateFormat(format);//格式化时间输出
        return df.format(date);
    }

    /**
     * 格式化时间Timestamp
     * formatDate(new Timestamp(),"yyyy-MM-dd HH:mm:ss")
     *
     * @param date
     * @param format
     * @return
     */
    public final static String formatDate(Timestamp date, String format) {
        if (null == date) return "";
        SimpleDateFormat df = new SimpleDateFormat(format);//格式化时间输出
        return df.format(date);
    }

    /**
     * 时间格式转化Timestamp
     *
     * @param dateStr
     * @return
     */
    public final static String changeDateFormat(String dateStr) {
        if (null == dateStr || "".equals(dateStr)) return "";
        Timestamp tt = Timestamp.valueOf(dateStr);

        return formatDate(tt, "dd/MM/yyyy");
    }

    /**
     * 根据起始日期和间隔时间计算结束日期
     *
     * @param sDate 开始时间
     * @param days  间隔时间
     * @return 结束时间
     */
    public static Date calculateEndDate(Date sDate, int days) {
        //将开始时间赋给日历实例
        Calendar sCalendar = Calendar.getInstance();
        sCalendar.setTime(sDate);
        //计算结束时间
        sCalendar.add(Calendar.DATE, days);
        //返回Date类型结束时间
        return sCalendar.getTime();
    }

    /**
//     * 计算两个日期的时间间隔
//     *
//     * @param sDate 开始时间
//     * @param eDate 结束时间
//     * @param type  间隔类型("Y/y"--年  "M/m"--月  "D/d"--日)
//     * @return interval时间间隔
//     */
//    public static int calInterval(Date sDate, Date eDate, String type) {
//        //时间间隔，初始为0
//        int interval = 0;
//
//        /*比较两个日期的大小，如果开始日期更大，则交换两个日期*/
//        //标志两个日期是否交换过
//        boolean reversed = false;
//        if (compareDate(sDate, eDate) > 0) {
//            Date dTest = sDate;
//            sDate = eDate;
//            eDate = dTest;
//            //修改交换标志
//            reversed = true;
//        }
//
//        /*将两个日期赋给日历实例，并获取年、月、日相关字段值*/
//        Calendar sCalendar = Calendar.getInstance();
//        sCalendar.setTime(sDate);
//        int sYears = sCalendar.get(Calendar.YEAR);
//        int sMonths = sCalendar.get(Calendar.MONTH);
//        int sDays = sCalendar.get(Calendar.DAY_OF_YEAR);
//
//        Calendar eCalendar = Calendar.getInstance();
//        eCalendar.setTime(eDate);
//        int eYears = eCalendar.get(Calendar.YEAR);
//        int eMonths = eCalendar.get(Calendar.MONTH);
//        int eDays = eCalendar.get(Calendar.DAY_OF_YEAR);
//
//        //年
//        if (StrUtil.cTrim(type).equals("Y") || StrUtil.cTrim(type).equals("y")) {
//            interval = eYears - sYears;
//            if (eMonths < sMonths) {
//                --interval;
//            }
//        }
//        //月
//        else if (StrUtil.cTrim(type).equals("M") || StrUtil.cTrim(type).equals("m")) {
//            interval = 12 * (eYears - sYears);
//            interval += (eMonths - sMonths);
//        }
//        //日
//        else if (StrUtil.cTrim(type).equals("D") || StrUtil.cTrim(type).equals("d")) {
//            interval = 365 * (eYears - sYears);
//            interval += (eDays - sDays);
//            //除去闰年天数
//            while (sYears < eYears) {
//                if (isLeapYear(sYears)) {
//                    --interval;
//                }
//                ++sYears;
//            }
//        }
//        //如果开始日期更大，则返回负值
//        if (reversed) {
//            interval = -interval;
//        }
//        //返回计算结果
//        return interval;
//    }

    /**
     * 输出日历相关字段（当前日期）
     *
     * @param cNow 当前时间
     * @return null
     * <p/>
     * 各个字段值都可以用get(field)得到，也可以用set(field, value)函数修改
     */
    public static void printFields(Calendar cNow) {
        //先用Date类型输出验证
        SimpleDateFormat df = (SimpleDateFormat) DateFormat.getInstance();
        df.applyPattern("yyyy-MM-dd  HH:mm:ss");
        System.out.println("标准日期:" + df.format(new Date()));
        //逐个输出相关字段值
        System.out.print("年份:" + cNow.get(Calendar.YEAR) + "\t");
        //月份有问题(这里的月份开始计数为0)
        System.out.print("月份:" + cNow.get(Calendar.MONTH) + "\t");
        System.out.print("日期:" + cNow.get(Calendar.DATE) + "\t");
        System.out.print("小时:" + cNow.get(Calendar.HOUR) + "\t");
        System.out.print("分钟:" + cNow.get(Calendar.MINUTE) + "\t");
        System.out.println("秒钟:" + cNow.get(Calendar.SECOND));
        //本年的第几天,在计算时间间隔的时候有用
        System.out.println("一年中的天数:" + cNow.get(Calendar.DAY_OF_YEAR));
        System.out.println("一年中的周数:" + cNow.get(Calendar.WEEK_OF_YEAR));
        //即本月的第几周
        System.out.println("一月中的周数:" + cNow.get(Calendar.WEEK_OF_MONTH));
        //即一周中的第几天(这里是以周日为第一天的)
        System.out.println("一周中的天数:" + cNow.get(Calendar.DAY_OF_WEEK));
    }

    /**
     * 判定某个年份是否是闰年
     *
     * @param year 待判定的年份
     * @return 判定结果
     */
    public static boolean isLeapYear(int year) {
        return (year % 400 == 0 || (year % 4 == 0 && year % 100 != 0));
    }

    /**
     * 比较两个Date类型的日期大小
     *
     * @param sDate 开始时间
     * @param eDate 结束时间
     * @return result返回结果(0--相同 1--前者大 2--后者大)
     */
    public static int compareDate(Date sDate, Date eDate) {
        int result = 0;
        //将开始时间赋给日历实例
        Calendar sC = Calendar.getInstance();
        sC.setTime(sDate);
        //将结束时间赋给日历实例
        Calendar eC = Calendar.getInstance();
        eC.setTime(eDate);
        //比较
        result = sC.compareTo(eC);
        //返回结果
        return result;
    }

    /**
     * 获得指定日期当天开始时间
     * @param dateStr
     * @return
     */
    public static String getTimeStart(String dateStr){
        String timeStartStr = "";
        try {
            Date date = formatStringDate(dateStr);
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.set(Calendar.HOUR,0);
            c.set(Calendar.MINUTE,0);
            c.set(Calendar.SECOND,0);
            timeStartStr = formatDateTime(c.getTime());
        }catch (Exception e){
            e.printStackTrace();
//            ExceptionLogUtil.logWriteFile("DateUtil","getTimeStart",e);
        }
        return timeStartStr;
    }

    /**
     * 获得指定日期当天结束时间
     * @param dateStr
     * @return
     */
    public static String getTimeEnd(String dateStr){
        String timeEndStr = "";
        try {
            Date date = formatStringDate(dateStr);
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.set(Calendar.HOUR,23);
            c.set(Calendar.MINUTE,59);
            c.set(Calendar.SECOND,59);
            timeEndStr = formatDateTime(c.getTime());
        }catch (Exception e){
            e.printStackTrace();
//            ExceptionLogUtil.logWriteFile("DateUtil","getTimeEnd",e);
        }
        return timeEndStr;
    }

    /**
     * 得到一天的起点，即00点00分00秒
     * @return
     */
    public static String getNowTimeStart(){
        String nowTimeStartStr = "";
        try {
            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR,0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND,0);
            Date date = c.getTime();
            nowTimeStartStr = formatDateTime(date);
        }catch (Exception e){
            e.printStackTrace();
        }
        return nowTimeStartStr;
    }

    /**
     * 得到一天的起点，即00点00分00秒
     * @return
     */
    public static String getNowTimeEnd(){
        String nowTimeStartStr = "";
        try {
            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR,23);
            c.set(Calendar.MINUTE,59);
            c.set(Calendar.SECOND,59);
            Date date = c.getTime();
            nowTimeStartStr = formatDateTime(date);
        }catch (Exception e){
            e.printStackTrace();
        }
        return nowTimeStartStr;
    }

    /**
     * 把完整时间格式的时间转换成不含年份和秒的时间字符串
     * @param dateTimeStr 完整时间格式的时间字符串
     * @return 不含年份和秒的时间字符串
     */
    public static String getTimeNotYearAndSecond(String dateTimeStr){
        String timeNotYearAndSecondStr = "";
        try {
            Date date = new Date(dateTimeStr);
            SimpleDateFormat df = new SimpleDateFormat("MM-dd HH:mm");//格式化时间输出
            timeNotYearAndSecondStr = df.format(date);
        }catch (Exception e){
            e.printStackTrace();
        }
        return timeNotYearAndSecondStr;
    }

    /**
     * 把完整时间格式的时间转换成不含秒的时间字符串
     * @param dateTimeStr 完整时间格式的时间字符串
     * @return  不含秒的时间字符串
     */
    public static String getTimeNotSecond(String dateTimeStr){
        String timeNotSecondStr = "";
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateTimeStr);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");//格式化时间输出
            timeNotSecondStr = df.format(date);
        }catch (Exception e){
            e.printStackTrace();
//            ExceptionLogUtil.logWriteFile("DateUtil","getTimeNotSecond",e);
            timeNotSecondStr = "";
        }
        return timeNotSecondStr;
    }

    /**
     * 把不带秒的时间格式的时间转换成不含时分秒的日期字符串
     * @param dateTimeStr 完整时间格式的时间字符串
     * @return 不含时分秒的日期字符串
     */
    public static String getDateByNoSecondTimeStr(String dateTimeStr){
        String dateStr = "";
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(dateTimeStr);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//格式化时间输出
            dateStr = df.format(date);
        }catch (Exception e){
            e.printStackTrace();
//            ExceptionLogUtil.logWriteFile("DateUtil","getDateByNoSecondTimeStr",e);
            dateStr = "";
        }
        return dateStr;
    }

    /**
     * 把完整时间格式的时间转换成不含时分秒的日期字符串
     * @param dateTimeStr 完整时间格式的时间字符串
     * @return 不含时分秒的日期字符串
     */
    public static String getDateByTimeStr(String dateTimeStr){
        String dateStr = "";
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateTimeStr);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//格式化时间输出
            dateStr = df.format(date);
        }catch (Exception e){
            e.printStackTrace();
//            ExceptionLogUtil.logWriteFile("DateUtil","getDateByTimeStr",e);
            dateStr = "";
        }
        return dateStr;
    }

    /**
     * 获得当前时间
     * @return
     */
    public  static String getNowTime(){
        String nowTimeStr="";
        try {
            Date date= new Date();
            nowTimeStr=formatDateTime(date);
        }catch (Exception e){
            e.printStackTrace();
        }
        return nowTimeStr;
    }

    /**
     * 获得当前日期
     * @return
     */
    public static String getNowDate(){
        String nowDateStr="";
        try {
            Date date= new Date();
            nowDateStr= formatDate(date,"yyyyMMdd");
        }catch (Exception e){
            e.printStackTrace();
        }
        return nowDateStr;
    }

    /**
     * 获得时分秒的格式化HHmmss
     * @return
     */
    public static String getNowSeconds(){
        String nowSecondsStr="";
        try {
            Date date=new Date();
            nowSecondsStr= formatDate(date,"HHmmss");
        }catch (Exception e){
            e.printStackTrace();
        }
        return nowSecondsStr;
    }

    /**
     * 在当前时间的基础上加上分钟
     * @param nowTimeStr
     * @return
     */
    public static String addMinute(String nowTimeStr,int minutes){
        String resultTime = "";
        try {
            Date nowTime = formatStringTime(nowTimeStr);
            nowTime.setMinutes(nowTime.getMinutes() + minutes);
            resultTime = formatDateTime(nowTime);
        }catch (Exception e){
            e.printStackTrace();
//            ExceptionLogUtil.logWriteFile("DateUtil","addMinute",e);
            resultTime = nowTimeStr;
        }
        return resultTime;
    }

    /**
     * 在当前不带秒的时间字符串表示的时间上加上指定分钟
     * @param nowTimeStr
     * @param minutes
     * @return
     */
    public static String addMinuteNotSecond(String nowTimeStr,int minutes){
        String resultTime = "";
        try {
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date nowTime = sf.parse("nowTimeStr");
            nowTime.setMinutes(nowTime.getMinutes() + minutes);
            resultTime = sf.format(nowTime);
        }catch (Exception e){
            e.printStackTrace();
//            ExceptionLogUtil.logWriteFile("DateUtil","addMinute",e);
            resultTime = nowTimeStr;
        }
        return resultTime;
    }

    /**
     * 在指定的时间上加月
     * @param nowDate
     * @param months
     * @return
     */
    public static String addMonth(String nowDate,int months){
        String resultDate = "";
        try {
            Date nowTime = formatStringTime(nowDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(nowTime);
            calendar.add(Calendar.MONTH,months);
            resultDate = formatDate(calendar.getTime());
        }catch (Exception e){
            e.printStackTrace();
//            ExceptionLogUtil.logWriteFile("DateUtil","addMinute",e);
            resultDate = nowDate;
        }
        return resultDate;
    }

    /**
     * 在指定的时间上加日
     * @param nowDate
     * @param day
     * @return
     */
    public static String addDay(String nowDate,int day){
        String resultDate = "";
        try {
            Date nowTime = formatStringTime(nowDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(nowTime);
            calendar.add(Calendar.DATE,day);
            resultDate = formatDate(calendar.getTime());
        }catch (Exception e){
            e.printStackTrace();
            resultDate = nowDate;
        }
        return resultDate;
    }

    /**
     * 在指定的时间上加日
     * @param nowDate
     * @param day
     * @return
     */
    public static Date addDayToDate(Date nowDate,int day){
        Date resultDate = null;
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(nowDate);
            calendar.add(Calendar.DATE,day);
            resultDate = calendar.getTime();
        }catch (Exception e){
            e.printStackTrace();
            resultDate = nowDate;
        }
        return resultDate;
    }

    public static void main(String[] args) {
        String dateStr = "2017-07-13";
        System.out.println("dateStart---->" + getTimeStart(dateStr));
        System.out.println("dateEnd----->" + getTimeEnd(dateStr));

        Date yesterday = calculateEndDate(formatStringDate("2017-07-01"),-1);

        System.out.println("dateStart---->" + getTimeStart(formatDate(yesterday)));
        System.out.println("dateEnd----->" + getTimeEnd(formatDate(yesterday)));
    }

    /**
     * 把字符串时间转换为日期类
     * @param timeStr
     * @return
     */
    public final static Date formatString(String timeStr,String format){
        Date date;
        try {
            if(null == timeStr || "".equals(timeStr)) return new Date();
            SimpleDateFormat df = new SimpleDateFormat(format);//格式化时间输出
            date = df.parse(timeStr);
        }catch (Exception e){
            e.printStackTrace();
            date = new Date(0);
        }
        return date;
    }

}
