package app.db.models;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimestampData {
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private Date timestamp;

    public TimestampData(Date timestamp) {
        this.timestamp = timestamp;
    }

    public TimestampData() { }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return format.format(timestamp);
    }
}
