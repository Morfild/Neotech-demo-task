package app.db;

import java.sql.SQLException;
import java.util.List;

public interface Database {
    <M> List<M> rawQuery(String query, ResultSetProcessor<M> processor) throws SQLException, ConnectionException;
    void executeQuery(String query) throws SQLException, ConnectionException;
    void executeQuery(String query, List<Object> args) throws SQLException, ConnectionException;
}
