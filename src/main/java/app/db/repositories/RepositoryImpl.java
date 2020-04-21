package app.db.repositories;

import app.db.ConnectionException;
import app.db.Database;
import app.db.models.TimestampData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import static java.util.Collections.singletonList;

public class RepositoryImpl implements Repository {

    private final Database database;

    public RepositoryImpl(Database database) {
        this.database = database;
    }

    @Override
    public void initialize() {
        String query = "CREATE TABLE IF NOT EXISTS Timestamps(`id` int(11) NOT NULL auto_increment, `created_at` TIMESTAMP NOT NULL, PRIMARY KEY  (`id`))";

        try {
            database.executeQuery(query);
        } catch (SQLException | ConnectionException e) {
            throw new RuntimeException("Bad request", e);
        }
    }

    @Override
    public List<TimestampData> fetchData() {
        String query = "SELECT * FROM Timestamps";

        try {
            return database.rawQuery(query, this::mapTimestampData);
        } catch (SQLException | ConnectionException e) {
            throw new RuntimeException("Bad request", e);
        }
    }

    private TimestampData mapTimestampData(ResultSet set) throws SQLException {
        TimestampData data = new TimestampData();
        data.setTimestamp(set.getTimestamp("created_at"));
        return data;
    }

    @Override
    public void insertData(TimestampData data) throws ConnectionException {
        String query = "INSERT INTO Timestamps (created_at) VALUES (?)";

        Timestamp timestamp = new Timestamp(data.getTimestamp().getTime());

        try {
            database.executeQuery(query, singletonList(timestamp));
        } catch (SQLException e) {
            throw new RuntimeException("Bad query", e);
        }
    }

}