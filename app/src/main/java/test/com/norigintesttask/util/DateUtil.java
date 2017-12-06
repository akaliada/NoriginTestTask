package test.com.norigintesttask.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

    private static final String TIME_SHORT_PATTERN = "HH:mm";
    private static final String WEEKDAY_PATTERN = "EEEE";
    private static final String WEEKDAY_PATTERN_SHORT = "EEE";

    private static final DateFormat shortTimeFormat = new SimpleDateFormat(TIME_SHORT_PATTERN, Locale.getDefault());
    private static final DateFormat weekdayFormat = new SimpleDateFormat(WEEKDAY_PATTERN, Locale.getDefault());
    private static final DateFormat weekdayFormatShort = new SimpleDateFormat(WEEKDAY_PATTERN_SHORT, Locale.getDefault());

    public static String getShortTime(long timeMillis) {
        return shortTimeFormat.format(new Date(timeMillis));
    }

    public static String getWeekdayName(long dateMillis) {
        return weekdayFormat.format(new Date(dateMillis));
    }

    public static String getWeekdayNameShort(Date date) {
        return weekdayFormatShort.format(date);
    }

    public static int daysPassed(long newDate, long currentDate) {
        return (int) ((newDate - currentDate) / (24 * 60 * 60 * 1000));
    }

    public static Date getDateFromDate(long dateMillis, int offset) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateMillis);
        calendar.add(Calendar.DAY_OF_YEAR, offset);
        return calendar.getTime();
    }

    public static String getDayMonth(Date date) {
        return new SimpleDateFormat("dd.MM", Locale.getDefault()).format(date);
    }

    //TODO: This is a temporary method, required since mock api returns data for 18-3-2017.
    public static long getCurrentTimeInMillis() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 18);
        calendar.set(Calendar.MONTH, 2);
        calendar.set(Calendar.YEAR, 2017);
        return calendar.getTimeInMillis();
    }

    public static boolean isNowBetween(long start, long end) {
        return getCurrentTimeInMillis() >= start && getCurrentTimeInMillis() <= end;
    }

}
