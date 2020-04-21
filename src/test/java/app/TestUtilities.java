package app;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TestUtilities {

    public static Date createDateTime(String dateStr) throws ParseException {
        return getDateTimeFormat().parse(dateStr);
    }

    public static String parseDateTime(Date date) {
        return getDateTimeFormat().format(date);
    }

    private static DateFormat getDateTimeFormat() {
        return new SimpleDateFormat("MM/dd/yy HH:mm:ss", Locale.ENGLISH);
    }

}
