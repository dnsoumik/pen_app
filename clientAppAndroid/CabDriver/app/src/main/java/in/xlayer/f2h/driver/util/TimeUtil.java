package in.xlayer.f2h.driver.util;

import android.annotation.SuppressLint;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Soumik Debnath on 14-05-2018.
 */

@SuppressLint("SimpleDateFormat")
public class TimeUtil {

    private static String TAG = TimeUtil.class.getSimpleName();

    public static String parseTimeStampToDateFormat(Long timeStamp, String dateFormat) {
        try {
            Calendar cal = Calendar.getInstance();
            TimeZone tz = cal.getTimeZone();
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
            //SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
            sdf.setTimeZone(tz);
            //long timestamp = data[index].getStartTime();
            return sdf.format(new Date(timeStamp * 1000L));
        } catch (IllegalArgumentException e) {
            System.out.println("Exception :" + e);
            return "";
        }
    }

    public static long getFromTimeStringLocalToTimeStamp(String timeString, String dateFormat) {
        try {
            String str_date = "11-June-07";
            DateFormat formatter;
            Date date;
            formatter = new SimpleDateFormat(dateFormat);
            date = (Date) formatter.parse(timeString);
            Timestamp timeStampDate = new
                    Timestamp(date.getTime());

//            Timestamp ts = new Timestamp(date.getTime());
//            SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yy HH:mm:ss");
//                    System.out.println(formatter.format(ts));
//            dateInTimeStamp = calendar.getTime().getTime()/1000;
//            dateInString = format.format(ts);
//            Log.i(TAG, "Today is " + timeStampDate + " : " + date.getTime()/1000);
            return date.getTime() / 1000;
        } catch (ParseException e) {
            System.out.println("Exception :" + e);
            return 0L;
        }
    }

    public static String parseStampFromUnixToLocalWithAMPM(long timestamp) {
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy hh:mm a");
        //SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        sdf.setTimeZone(tz);
        //long timestamp = data[index].getStartTime();
        return sdf.format(new Date(timestamp * 1000L));
    }

    public static long getStampFromLocalToUnixStamp(String stamp_format, String date_format) {
        try {
//            String str_date = "11-June-07";
            DateFormat formatter;
            Date date;
            formatter = new SimpleDateFormat(date_format);
            date = (Date) formatter.parse(stamp_format);
            Timestamp timeStampDate = new
                    Timestamp(date.getTime());

//            Timestamp ts = new Timestamp(date.getTime());
//            SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yy HH:mm:ss");
//                    System.out.println(formatter.format(ts));
//            dateInTimeStamp = calendar.getTime().getTime()/1000;
//            dateInString = format.format(ts);
//            Log.i(TAG, "Today is " + timeStampDate + " : " + date.getTime()/1000);
            return date.getTime() / 1000;
        } catch (ParseException e) {
            System.out.println("Exception :" + e);
            return 0L;
        }
    }

    public static long getFromCalenderDateInStamp(int year, int month, int day, int hour, int minute, int seconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute, seconds);
//        Timestamp ts = new Timestamp(calendar.getTime().getTime());
//        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
        return calendar.getTime().getTime() / 1000;
//        dateInString = formatter.format(ts);
    }

    public static String getFromCalenderDateInString(int year, int month, int day, String format) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, 0, 0, 0);
        Timestamp ts = new Timestamp(calendar.getTime().getTime());
        SimpleDateFormat formatter = new SimpleDateFormat(format);
//        return calendar.getTime().getTime()/1000;
        return formatter.format(ts);
    }

    public static String getFromCalenderTimeInString(int hour, int minute, int seconds, String format) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(0, 0, 0, hour, minute, seconds);
        Timestamp ts = new Timestamp(calendar.getTime().getTime());
        SimpleDateFormat formatter = new SimpleDateFormat(format);
//        return calendar.getTime().getTime()/1000;
        return formatter.format(ts);
    }
}
