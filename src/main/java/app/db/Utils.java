package app.db;

import com.mysql.cj.jdbc.exceptions.CommunicationsException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static <M> List<M> mapResultSet(ResultSet resultSet, ResultSetProcessor<M> mapping) {
        List<M> mapped = new ArrayList<>();
        while (true) {
            try {
                if (!resultSet.next())
                    break;
                mapped.add(mapping.map(resultSet));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return mapped;
    }

    public static void mapParamsIntoPreparedStatement(PreparedStatement preparedStatement, List<Object> args) throws SQLException {
        for (int i = 1; i <= args.size(); i++) {
            Object arg = args.get(i - 1);

            if (arg instanceof String) {
                preparedStatement.setString(i, (String) arg);
            } else if (arg instanceof Integer) {
                preparedStatement.setInt(i, (Integer) arg);
            } else if (arg instanceof Long) {
                preparedStatement.setLong(i, (Long) arg);
            } else if (arg instanceof Float) {
                preparedStatement.setFloat(i, (Float) arg);
            } else if (arg instanceof Double) {
                preparedStatement.setDouble(i, (Double) arg);
            } else if (arg instanceof Date) {
                preparedStatement.setDate(i, (Date) arg);
            } else if (arg instanceof Timestamp) {
                preparedStatement.setTimestamp(i, (Timestamp) arg);
            } else if (arg instanceof Boolean) {
                preparedStatement.setBoolean(i, (Boolean) arg);
            }
        }
    }

    public static boolean isConnectionException(Throwable e) {
        if (e == null) {
            return false;
        } else if (e instanceof CommunicationsException) {
            return true;
        } else {
            return isConnectionException(e.getCause());
        }
    }

}
