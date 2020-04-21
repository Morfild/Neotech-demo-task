package app.db.repositories;

import app.db.ConnectionException;
import app.db.models.TimestampData;

import java.util.List;

public interface Repository {
    void initialize();
    List<TimestampData> fetchData();
    void insertData(TimestampData data) throws ConnectionException;
}
