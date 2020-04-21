package app.db;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultSetProcessor<M> {
    M map(ResultSet resultSet) throws SQLException;
}
