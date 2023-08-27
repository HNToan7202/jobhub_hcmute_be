package vn.iotstar.jobhub_hcmute_be.constant;

import java.util.Calendar;

public class CalendarUtil {
    public static Calendar startTheDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar;
    }
}
