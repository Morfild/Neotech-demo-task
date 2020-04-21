package app.db;

import java.sql.*;
import java.util.List;
import java.util.Properties;

import static app.db.Utils.*;

public class MySQLDatabase implements Database {

    private final String url;
    private final Properties properties;

    public MySQLDatabase(String url,
                         String scheme,
                         String user,
                         String password,
                         long timeout) {
        this.url = String.format("jdbc:mysql://%s/%s", url, scheme);
        this.properties = createProperties(user, password, timeout);
    }

    private Properties createProperties(String user, String password, long timeout) {
        Properties properties = new Properties();
        properties.setProperty("user", user);
        properties.setProperty("password", password);
        properties.setProperty("autoReconnect", "false");
        properties.setProperty("connectTimeout", String.valueOf(timeout));
        properties.setProperty("socketTimeout", String.valueOf(timeout));
        return properties;
    }

    @Override
    public <M> List<M> rawQuery(String query, ResultSetProcessor<M> processor) throws SQLException, ConnectionException {
        try (Connection connection = openConnection()) {
            Statement st = connection.createStatement();
            return mapResultSet(st.executeQuery(query), processor);
        } catch (SQLException e) {
            if (isConnectionException(e)) {
                throw new ConnectionException(e);
            } else {
                throw e;
            }
        }
    }

    @Override
    public void executeQuery(String query) throws SQLException, ConnectionException {
        try (Connection connection = openConnection()) {
            Statement st = connection.createStatement();
            st.execute(query);
        } catch (SQLException e) {
            if (isConnectionException(e)) {
                throw new ConnectionException(e);
            } else {
                throw e;
            }
        }
    }

    @Override
    public void executeQuery(String query, List<Object> args) throws SQLException, ConnectionException {
        try (Connection connection = openConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            mapParamsIntoPreparedStatement(preparedStatement, args);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            if (isConnectionException(e)) {
                throw new ConnectionException(e);
            } else {
                throw e;
            }
        }
    }

    private Connection openConnection() throws SQLException {
        return DriverManager.getConnection(url, properties);
    }

}
