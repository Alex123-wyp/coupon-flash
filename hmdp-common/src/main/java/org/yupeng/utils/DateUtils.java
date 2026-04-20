package org.yupeng.utils;

import lombok.Data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

/**
 * @program: High-Concurrency Voucher Seckill Platform (HMDP Plus). Email: wyupeng072@gmail.com
 * @description: Date utilities
 * @author: yupeng
 **/
public class DateUtils {
 
    /** Number of days in a week */
    public static final int WEEK_DAYS = 7;
    /** Number of months in a year */
    public static final int YEAR_MONTHS = 12;
    /** Number of hours in a day */
    public static final int DAY_HOURS = 24;
    /** Minutes in an hour */
    public static final int HOUR_MINUTES = 60;
    /** Minutes in a day (24 * 60) */
    public static final int DAY_MINUTES = 1440;
    /** Number of seconds in a minute */
    public static final int MINUTE_SECONDS = 60;
    /** Number of seconds in an hour (60 * 60) */
    public static final int HOUR_SECONDS = 3600;
    /** Seconds in a day (24 * 60 * 60) */
    public static final int DAY_SECONDS = 86400;
    /** Number of milliseconds in one second */
    public static final long SECOND_MILLISECONDS = 1000L;
    /** Number of milliseconds in one minute (60 * 1000) */
    public static final long MINUTE_MILLISECONDS = 60000L;
    /** Number of milliseconds in one hour (60 * 60 * 1000) */
    public static final long HOUR_MILLISECONDS = 3600000L;
    /** Number of milliseconds in a day (24 * 60 * 60 * 1000) */
    public static final long DAY_MILLISECONDS = 86400000L;
    /** Monday */
    public static final int WEEK_1_MONDAY = 1;
    /** Tuesday */
    public static final int WEEK_2_TUESDAY = 2;
    /** Wednesday */
    public static final int WEEK_3_WEDNESDAY = 3;
    /** Thursday */
    public static final int WEEK_4_THURSDAY = 4;
    /** Friday */
    public static final int WEEK_5_FRIDAY = 5;
    /** Saturday */
    public static final int WEEK_6_SATURDAY = 6;
    /** Sunday */
    public static final int WEEK_7_SUNDAY = 7;
    /** January */
    public static final int MONTH_1_JANUARY = 1;
    /** February */
    public static final int MONTH_2_FEBRUARY = 2;
    /** March */
    public static final int MONTH_3_MARCH = 3;
    /** April */
    public static final int MONTH_4_APRIL= 4;
    /** May */
    public static final int MONTH_5_MAY = 5;
    /** June */
    public static final int MONTH_6_JUNE = 6;
    /** July */
    public static final int MONTH_7_JULY = 7;
    /** August */
    public static final int MONTH_8_AUGUST = 8;
    /** September */
    public static final int MONTH_9_SEPTEMBER = 9;
    /** October */
    public static final int MONTH_10_OCTOBER = 10;
    /** November */
    public static final int MONTH_11_NOVEMBER = 11;
    /** December */
    public static final int MONTH_12_DECEMBER= 12;
    /** Show to date */
    public static final String FORMAT_DATE = "yyyy-MM-dd";
    /** Display to hour */
    public static final String FORMAT_HOUR = "yyyy-MM-dd HH";
    /** Display to minute */
    public static final String FORMAT_MINUTE = "yyyy-MM-dd HH:mm";
    /** Display to seconds */
    public static final String FORMAT_SECOND = "yyyy-MM-dd HH:mm:ss";
    /** Display to milliseconds */
    public static final String FORMAT_MILLISECOND = "yyyy-MM-dd HH:mm:ss:SSS";
    /** Display to date (numeric format) */
    public static final String FORMAT_NO_DATE = "yyyyMMdd";
    /** Display to hour (numeric format) */
    public static final String FORMAT_NO_HOUR = "yyyyMMddHH";
    /** Display to minutes (numeric format) */
    public static final String FORMAT_NO_MINUTE = "yyyyMMddHHmm";
    /** Display to seconds (numeric format) */
    public static final String FORMAT_NO_SECOND = "yyyyMMddHHmmss";
    /** Display to milliseconds (numeric format) */
    public static final String FORMAT_NO_MILLISECOND = "yyyyMMddHHmmssSSS";
    
    public static final String FORMAT_UTC = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    
    /**
     * Get the current time in Beijing
     * */
    public static Date now(){
        return parseDateTime(getFormatedDateString(8,FORMAT_SECOND));
    }
    
    /**
     * Get the current time in Beijing
     * */
    public static Date now(String format){
        return parseDateTime(getFormatedDateString(8,format),format);
    }
    
    public static String nowStr(){
        return getFormatedDateString(8, FORMAT_SECOND);
    }
    
    public static String nowStr(String pattern){
        return getFormatedDateString(8, pattern);
    }
 
    /**
     * Get the specified time Formatter
     *
     * @param formatStyle time format
     * @return time formatter
     */
    private static SimpleDateFormat getSimpleDateFormat(String formatStyle) {
        return new SimpleDateFormat(formatStyle);
    }
 
    /**
     * Convert Date format time to specified format time
     *
     * @param date Date format time
     * @param formatStyle converts the specified format (such as: yyyy-MM-dd HH:mm:ss)
     * @return conversion format time
     */
    public static String format(Date date, String formatStyle) {
        if (Objects.isNull(date)) {
            return "";
        }
        return getSimpleDateFormat(formatStyle).format(date);
    }
 
    /**
     * Convert Date format time to yyyy-MM-dd format time
     *
     * @param date Date format time
     * @return yyyy-MM-dd format time (such as: 2022-06-17)
     */
    public static String formatDate(Date date) {
        return format(date, FORMAT_DATE);
    }
 
    /**
     * Convert Date format time to yyyy-MM-dd HH:mm:ss format time
     *
     * @param date Date format time
     * @return yyyy-MM-dd HH:mm:ss format time (such as: 2022-06-17 16:06:17)
     */
    public static String formatDateTime(Date date) {
        return format(date, FORMAT_SECOND);
    }
 
    /**
     * Convert Date format time to yyyy-MM-dd HH:mm:ss:SSS format time
     *
     * @param date Date format time
     * @return yyyy-MM-dd HH:mm:ss:SSS format time (such as: 2022-06-17 16:06:17:325)
     */
    public static String formatDateTimeStamp(Date date) {
        return format(date, FORMAT_MILLISECOND);
    }
    
    /**
     * Convert Date format time to yyyy-MM-dd HH:mm:ss format time
     *
     * @param date Date format time
     * @return yyyy-MM-dd HH:mm:ss format time (such as: 2022-06-17 16:06:17)
     */
    public static String formatUtcTime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_UTC);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        return sdf.format(date);
    }
    
    /**
     * Convert yyyy-MM-dd format time to Date format time
     * 
     * @param dateString yyyy-MM-dd format time (such as: 2022-06-17)
     * @return Date format time
     */
    public static Date parseDate(String dateString) {
        return parse(dateString, FORMAT_DATE);
    }
 
    /**
     * Convert yyyy-MM-dd HH:mm:ss format time to Date format time
     * 
     * @param dateTimeStr yyyy-MM-dd HH:mm:ss format time (such as: 2022-06-17 16:06:17)
     * @return Date format time
     */
    public static Date parseDateTime(String dateTimeStr) {
        return parse(dateTimeStr, FORMAT_SECOND);
    }
    
    /**
     * Convert time in format format to time in Date format
     *
     * @param dateTimeStr yyyy-MM-dd HH:mm:ss format time (such as: 2022-06-17 16:06:17)
     * @param format format
     * @return Date format time
     */
    public static Date parseDateTime(String dateTimeStr,String format) {
        return parse(dateTimeStr, format);
    }
 
    /**
     * Convert yyyy-MM-dd HH:mm:ss:SSS format time to Date format time
     * 
     * @param dateTimeStampStr yyyy-MM-dd HH:mm:ss:SSS format time (such as: 2022-06-17 16:06:17)
     * @return Date format time
     */
    public static Date parseDateTimeStamp(String dateTimeStampStr) {
        return parse(dateTimeStampStr, FORMAT_MILLISECOND);
    }
    
    /**
     * Convert timestamp to date
     *
     * @param timestamp
     * @return
     */
    public static Date parse(Long timestamp) {
        return new Date(timestamp);
    }
 
    /**
     * Convert string format time to Date format time
     * 
     * @param dateString string time (such as: 2022-06-17 16:06:17)
     * @return formatStyle format content
     * @return Date format time
     */
    public static Date parse(String dateString, String formatStyle) {
        String s = getString(dateString);
        if (s.isEmpty()) {
            return null;
        }
        try {
            return getSimpleDateFormat(formatStyle).parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
 
    /**
     * Get string payload
     * 
     * @param s string
     * @return valid content
     */
    private static String getString(String s) {
        return Objects.isNull(s) ? "" : s.trim();
    }
 
    /**
     * Get the start time of the day (i.e.: 0:00:00:00:00 milliseconds)
     * 
     * @param date specified time
     * @return the start time of the day
     */
    public static Date getDateStart(Date date) {
        if (Objects.isNull(date)) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
 
    /**
     * Get the cutoff time of the day (ie: 23:59:59 seconds 999 milliseconds)
     * 
     * @param date specified time
     * @return the start time of the day
     */
    public static Date getDateEnd(Date date) {
        if (Objects.isNull(date)) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }
 
    /**
     * Get date number
     * 
     * @param date date
     * @return date number
     */
    public static int getDateNo(Date date) {
        if (Objects.isNull(date)) {
            return 0;
        }
        return Integer.valueOf(format(date, FORMAT_NO_DATE));
    }
 
    /**
     * Get datetime number (to seconds)
     * 
     * @param date date
     * @return date number
     */
    public static long getDateTimeNo(Date date) {
        if (Objects.isNull(date)) {
            return 0L;
        }
        return Long.parseLong(format(date, FORMAT_NO_SECOND));
    }
 
    /**
     * Get datetime number (to milliseconds)
     * 
     * @param date date
     * @return date number
     */
    public static long getDateTimeStampNo(Date date) {
        if (Objects.isNull(date)) {
            return 0L;
        }
        return Long.parseLong(format(date, FORMAT_NO_MILLISECOND));
    }
 
    /**
     * Get day of week
     * 
     * @param date time
     * @return 0 (time is empty), 1 (Monday), 2 (Tuesday), 3 (Wednesday), 4 (Thursday), 5 (Friday), 6 (Saturday), 7 (Sunday)
     */
    public static int getWeek(Date date) {
        if (Objects.isNull(date)) {
            return 0;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return getWeek(calendar);
    }
    
    /**
     * Get day of week
     *
     * @param date time
     * @return 0 (time is empty), 1 (Monday), 2 (Tuesday), 3 (Wednesday), 4 (Thursday), 5 (Friday), 6 (Saturday), 7 (Sunday)
     */
    public static String getWeekStr(Date date) {
        if (Objects.isNull(date)) {
            return "Unknown";
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return getWeekStr(calendar);
    }
 
    /**
     * Get day of week
     * 
     * @param calendar time
     * @return 0 (time is empty), 1 (Monday), 2 (Tuesday), 3 (Wednesday), 4 (Thursday), 5 (Friday), 6 (Saturday), 7 (Sunday)
     */
    private static int getWeek(Calendar calendar) {
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
        case Calendar.MONDAY:
            return 1;
        case Calendar.TUESDAY:
            return 2;
        case Calendar.WEDNESDAY:
            return 3;
        case Calendar.THURSDAY:
            return 4;
        case Calendar.FRIDAY:
            return 5;
        case Calendar.SATURDAY:
            return 6;
        case Calendar.SUNDAY:
            return 7;
        default:
            return 0;
        }
    }
    
    /**
     * Get day of week
     *
     * @param calendar time
     * @return week
     */
    private static String getWeekStr(Calendar calendar) {
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY:
                return "Monday";
            case Calendar.TUESDAY:
                return "Tuesday";
            case Calendar.WEDNESDAY:
                return "Wednesday";
            case Calendar.THURSDAY:
                return "Thursday";
            case Calendar.FRIDAY:
                return "Friday";
            case Calendar.SATURDAY:
                return "Saturday";
            case Calendar.SUNDAY:
                return "Sunday";
            default:
                return "Unknown";
        }
    }
 
    /**
     * Get the week number of this year for which the date is (Monday of this year is the first week, see the instructions below for details)<br>
     * 
     * 【Description】<br>
     * For example, although 2022-01-01 (Saturday) and 2022-01-02 (Sunday) are in 2022, they belong to the last week of 2021.<br>
     * Then these two days will not be counted in the first week of 2022, and 0 will be returned at this time; and the first week of 2022 will be calculated from 2022-01-03 (Monday). <br>
     * 
     * @param date time
     * @return -1 (the time is empty), 0 (the last week of the previous year), other numbers (the week of this year)
     */
    public static int getWeekOfYear(Date date) {
        if (Objects.isNull(date)) {
            return -1;
        }
        int weeks = getWeekOfYearIgnoreLastYear(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int week = getWeek(calendar);
        if (week == 1) {
            return weeks;
        }
        return weeks - 1;
    }
 
    /**
     * Get the week number of this year (January 1 of this year is the first day of week 1)<br>
     * 
     * @param date time
     * @return -1 (time is empty), other number (week of the year)
     */
    public static int getWeekOfYearIgnoreLastYear(Date date) {
        int seven = 7;
        if (Objects.isNull(date)) {
            return -1;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int days = calendar.get(Calendar.DAY_OF_YEAR);
        int weeks = days / seven;
        // If it is a multiple of 7, it indicates exactly how many weeks
        if (days % seven == 0) {
            return weeks;
        }
        // If there is a remainder, add 1
        return weeks + 1;
    }
 
    /**
     * Get time node object
     * 
     * @param date time object
     * @return DateNode
     */
    public static DateNode getDateNode(Date date) {
        if (Objects.isNull(date)) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        DateNode node = new DateNode();
        node.setTime(format(date, FORMAT_MILLISECOND));
        node.setYear(calendar.get(Calendar.YEAR));
        node.setMonth(calendar.get(Calendar.MONTH) + 1);
        node.setDay(calendar.get(Calendar.DAY_OF_MONTH));
        node.setHour(calendar.get(Calendar.HOUR_OF_DAY));
        node.setMinute(calendar.get(Calendar.MINUTE));
        node.setSecond(calendar.get(Calendar.SECOND));
        node.setMillisecond(calendar.get(Calendar.MILLISECOND));
        node.setWeek(getWeek(calendar));
        node.setDayOfYear(calendar.get(Calendar.DAY_OF_YEAR));
        node.setWeekOfYear(getWeekOfYear(date));
        node.setWeekOfYearIgnoreLastYear(getWeekOfYearIgnoreLastYear(date));
        node.setMillisecondStamp(date.getTime());
        node.setSecondStamp(node.getMillisecondStamp() / 1000);
        return node;
    }
 
    /**
     * date change
     * 
     * @param date specified date
     * @param field change attribute (if the year is changed, the value is Calendar.DAY_OF_YEAR)
     * @param amount changes the size (increase when greater than 0, decrease when less than 0)
     * @return the changed date and time
     */
    public static Date add(Date date, int field, int amount) {
        if (Objects.isNull(date)) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(field, amount);
        return calendar.getTime();
    }
 
    /**
     * Add or subtract years to a specified date
     * 
     * @param date specified date
     * @param year Change year (increase when greater than 0, decrease when less than 0)
     * @return the date after changing the year
     */
    public static Date addYear(Date date, int year) {
        return add(date, Calendar.YEAR, year);
    }
 
    /**
     * Add or subtract months to a specified date
     * 
     * @param date specified date
     * @param month Change month (increase when greater than 0, decrease when less than 0)
     * @return the date after changing the month
     */
    public static Date addMonth(Date date, int month) {
        return add(date, Calendar.MONTH, month);
    }
 
    /**
     * Add or subtract days from specified date
     * 
     * @param date specified date
     * @param day The number of days to change (increase when greater than 0, decrease when less than 0)
     * @return the date after the changed number of days
     */
    public static Date addDay(Date date, int day) {
        return add(date, Calendar.DAY_OF_YEAR, day);
    }
 
    /**
     * Add or subtract the day of the week to a specified date
     * 
     * @param date specified date
     * @param week changes the number of weeks (increase when greater than 0, decrease when less than 0)
     * @return the date after changing the number of weeks
     */
    public static Date addWeek(Date date, int week) {
        return add(date, Calendar.WEEK_OF_YEAR, week);
    }
 
    /**
     * Add or subtract time to specified date
     * 
     * @param date specifies date and time
     * @param hour Change the hour (increase when greater than 0, decrease when less than 0)
     * @return date and time after changing hours
     */
    public static Date addHour(Date date, int hour) {
        return add(date, Calendar.HOUR_OF_DAY, hour);
    }
 
    /**
     * Add or subtract minutes to a specified date
     * 
     * @param date specifies date and time
     * @param minute Change minute (increase when greater than 0, decrease when less than 0)
     * @return Date and time after changing minutes
     */
    public static Date addMinute(Date date, int minute) {
        return add(date, Calendar.MINUTE, minute);
    }
 
    /**
     * Add or subtract seconds to a specified date
     * 
     * @param date specifies date and time
     * @param second Change seconds (increase when greater than 0, decrease when less than 0)
     * @return date and time after changing seconds
     */
    public static Date addSecond(Date date, int second) {
        return add(date, Calendar.SECOND, second);
    }
 
    /**
     * Add or subtract seconds to a specified date
     * 
     * @param date specifies date and time
     * @param millisecond Change the number of milliseconds (increase when greater than 0, decrease when less than 0)
     * @return date and time after changing milliseconds
     */
    public static Date addMillisecond(Date date, int millisecond) {
        return add(date, Calendar.MILLISECOND, millisecond);
    }
 
    /**
     * Get the date of the specified week of the week in which the date is located
     * 
     * @param date date time
     * @return index specifies the day of the week (1 - 7 corresponds to Monday to Sunday respectively)
     */
    public static Date getWeekDate(Date date, int index) {
        if (index < WEEK_1_MONDAY || index > WEEK_7_SUNDAY) {
            return null;
        }
        int week = getWeek(date);
        return addDay(date, index - week);
    }
 
    /**
     * Get the start date of the week where the date is located
     * 
     * @param date date time
     * @return the start date of the week
     */
    public static Date getWeekDateStart(Date date) {
        return getDateStart(getWeekDate(date, WEEK_1_MONDAY));
    }
 
    /**
     * Get the start date of the week where the date is located
     * 
     * @param date date time
     * @return the start date of the week
     */
    public static Date getWeekDateEnd(Date date) {
        return getWeekDateEnd(getWeekDate(date, WEEK_7_SUNDAY));
    }
 
    /**
     * Get all days of the week in which the date falls (Monday to Sunday)
     * 
     * @param date date
     * @return All dates of the week in which the sunshine occurs
     */
    public static List<Date> getWeekDateList(Date date) {
        if (Objects.isNull(date)) {
            return Collections.emptyList();
        }
        // Get the start time of this week
        Date weekFromDate = getWeekDateStart(date);
        // Get deadline for this week
        Date weekeEndDate = getWeekDateEnd(date);
        return getBetweenDateList(weekFromDate, weekeEndDate, true);
    }
 
    /**
     * Get all days of the week in which the date falls (Monday to Sunday)
     * 
     * @param dateString
     * @return All dates of the week in which the sunshine occurs
     */
    public static List<String> getWeekDateList(String dateString) {
        Date date = parseDate(dateString);
        if (Objects.isNull(date)) {
            return Collections.emptyList();
        }
        return getDateStrList(getWeekDateList(date));
    }
 
    /**
     * Get all dates in the month where the date falls
     * 
     * @param date
     * @return All dates in the month of the Rizhao Institute
     */
    public static List<Date> getMonthDateList(Date date) {
        if (Objects.isNull(date)) {
            return Collections.emptyList();
        }
        Date monthDateStart = getMonthDateStart(date);
        Date monthDateEnd = getMonthDateEnd(date);
        return getBetweenDateList(monthDateStart, monthDateEnd, true);
    }
 
    /**
     * Get all dates in the month where the date falls
     * 
     * @param dateString
     * @return All dates in the month of the Rizhao Institute
     */
    public static List<String> getMonthDateList(String dateString) {
        Date date = parseDate(dateString);
        if (Objects.isNull(date)) {
            return Collections.emptyList();
        }
        return getDateStrList(getMonthDateList(date));
    }
    
    /**
     * Get the first day of the month where this date is located
     * 
     * @param date date
     * @return The first day of the month where this date is located
     */
    public static Date getMonthDateStart(Date date) {
        if (Objects.isNull(date)) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return getDateStart(calendar.getTime());
    }
 
    /**
     * Get the last day of the month where this date is located
     * 
     * @param date date
     * @return The last day of the month where this date is located
     */
    public static Date getMonthDateEnd(Date date) {
        if (Objects.isNull(date)) {
            return null;
        }
        Date monthDateStart = getMonthDateStart(date);
        Date nextMonthDateStart = getMonthDateStart(addMonth(monthDateStart, 1));
        return getDateEnd(addDay(nextMonthDateStart, -1));
    }
    
    /**
     * Get the number of seconds difference between two dates
     *
     * @param date1 date1
     * @param date2 date2
     * @return The difference in seconds (if -1 is returned, at least one date is empty and comparison cannot be made at this time)
     */
    public static long countBetweenSecond(Date date1, Date date2) {
        if (Objects.isNull(date1) || Objects.isNull(date2)) {
            return -1;
        }
        // Calculate millisecond difference directly and convert to seconds  
        long diffInMilliseconds = Math.abs(date2.getTime() - date1.getTime());
        return diffInMilliseconds / 1000;
    }
    
 
    /**
     * Get all dates between two dates
     * 
     * @param date1 date1
     * @param date2 date2
     * @return the start time of all dates between two dates
     */
    public static List<Date> getBetweenDateList(Date date1, Date date2, boolean isContainParams) {
        if (Objects.isNull(date1) || Objects.isNull(date2)) {
            return Collections.emptyList();
        }
        // Determine the date before and after
        Date fromDate = date1;
        Date toDate = date2;
        if (date2.before(date1)) {
            fromDate = date2;
            toDate = date1;
        }
        // Get the start time of each day for two dates
        Date from = getDateStart(fromDate);
        Date to = getDateStart(toDate);
        // Get the date and start looping
        List<Date> dates = new ArrayList<Date>();
        if (isContainParams) {
            dates.add(from);
        }
        Date date = from;
        boolean isBefore = true;
        while (isBefore) {
            date = addDay(date, 1);
            isBefore = date.before(to);
            if (isBefore) {
                dates.add(getDateStart(date));
            }
        }
        if (isContainParams) {
            dates.add(to);
        }
        return dates;
    }
 
    /**
     * Get all dates between two dates
     * 
     * @param dateString1 date 1 (such as: 2022-06-20)
     * @param dateString2 date 2 (such as: 2022-07-15)
     * @return all dates between two dates (excluding parameter dates)
     */
    public static List<String> getBetweenDateList(String dateString1, String dateString2) {
        return getBetweenDateList(dateString1, dateString2, false);
    }
 
    /**
     * Get all dates between two dates
     * 
     * @param dateString1 date 1 (such as: 2022-06-20)
     * @param dateString2 date 2 (such as: 2022-07-15)
     * @param isContainParams Whether the two dates of the parameters are included
     * @return the start time of all dates between two dates
     */
    public static List<String> getBetweenDateList(String dateString1, String dateString2, boolean isContainParams) {
        Date date1 = parseDate(dateString1);
        Date date2 = parseDate(dateString2);
        List<Date> dates = getBetweenDateList(date1, date2, isContainParams);
        return getDateStrList(dates);
    }
 
    /**
     * List<Date> to List<String>
     * 
     * @param dates date collection
     * @return date string collection
     */
    public static List<String> getDateStrList(List<Date> dates) {
        if (dates.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> dateList = new ArrayList<String>();
        for (Date date : dates) {
            dateList.add(formatDate(date));
        }
        return dateList;
    }
    
    /**
     * 
     * timeZoneOffset represents the time zone. For example, China generally uses the East Eighth District, so timeZoneOffset is 8
     *
     * @param timeZoneOffset
     * @return
     */
    public static String getFormatedDateString(float timeZoneOffset, String pattern) {
        int thirteen = 13;
        int minusTwelve = -12;
        if (timeZoneOffset > thirteen || timeZoneOffset < minusTwelve) {
            timeZoneOffset = 0;
        }
        
        int newTime = (int) (timeZoneOffset * 60 * 60 * 1000);
        TimeZone timeZone;
        String[] ids = TimeZone.getAvailableIDs(newTime);
        if (ids.length == 0) {
            timeZone = TimeZone.getDefault();
        } else {
            timeZone = new SimpleTimeZone(newTime, ids[0]);
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setTimeZone(timeZone);
        return sdf.format(new Date());
    }
    @Data
    static class DateNode {
        /** Year */
        private int year;
        /** moon */
        private int month;
        /** day */
        private int day;
        /** hour */
        private int hour;
        /** point */
        private int minute;
        /** Second */
        private int second;
        /** milliseconds */
        private int millisecond;
        /** Day of the week (1 - 7 corresponds to Monday to Sunday) */
        private int week;
        /**The day of the year */
        private int dayOfYear;
        /** The week of the current year (week 1 of this year is week 1, 0 means it is the last week of last year) */
        private int weekOfYear;
        /** The week of the current year (week 1 of this year is week 1, 0 means it is the last week of last year) */
        private int weekOfYearIgnoreLastYear;
        /** Timestamp (second level) */
        private long secondStamp;
        /** Timestamp (millisecond level) */
        private long millisecondStamp;
        /** Show time */
        private String time;
 
    }
    
    /**
     * Convert a string that conforms to the corresponding format into a date <format customization>
     *
     * @param dateStr date string
     * @param pattern date format
     * @return Date Return type Returns null when the date string is empty or does not conform to the date format
     */
    public static Date getDate(String dateStr, String pattern) {
        return getDate(dateStr, pattern, null);
    }
    
    /**
     * Convert a string that conforms to the corresponding format into a date <format customization>
     *
     * @param dateStr date string
     * @param pattern date format
     * @param defaultDate default date
     * @return Date Return type Returns null when the date string is empty or does not conform to the date format
     */
    public static Date getDate(String dateStr, String pattern, Date defaultDate) {
        if (dateStr != null && pattern != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                return sdf.parse(dateStr);
            } catch (ParseException e) {
                throw new IllegalArgumentException("Failed to convert string to date", e);
            }
        }
        return defaultDate;
    }
}
