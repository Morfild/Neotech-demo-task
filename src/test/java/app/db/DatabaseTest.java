package app.db;

import com.wix.mysql.EmbeddedMysql;
import com.wix.mysql.config.MysqldConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;

import static com.wix.mysql.EmbeddedMysql.anEmbeddedMysql;
import static com.wix.mysql.config.MysqldConfig.aMysqldConfig;
import static com.wix.mysql.distribution.Version.v5_7_10;

public class DatabaseTest {

    private final static String USER_NAME = "test-db";
    private final static String USER_PASSWORD = "password";
    private final static String SCHEMA = "db";

    private EmbeddedMysql mysqld;
    private Database database;

    @Before
    public void createsDBAndService() {
        MysqldConfig config = aMysqldConfig(v5_7_10)
                .withPort(3306)
                .withUser(USER_NAME, USER_PASSWORD)
                .build();

        mysqld = anEmbeddedMysql(config)
                .addSchema(SCHEMA)
                .start();

        database = new MySQLDatabase("localhost:3306", SCHEMA, USER_NAME, USER_PASSWORD, 300);
    }

    @Test(expected = ConnectionException.class)
    public void slowConnectionMustThrowAnError() throws ConnectionException, SQLException {
        database.executeQuery("DO SLEEP(500)");
    }

    @Test
    public void fastQueryMustShouldNotThrowAnyErrors() throws ConnectionException, SQLException {
        database.executeQuery("SELECT 1");
    }

    @Test(expected = ConnectionException.class)
    public void connectionToInaccessibleServerMustThrowAnError() throws ConnectionException, SQLException {
        MySQLDatabase database = new MySQLDatabase("138.68.87.86:3306", "-", "-", "-", 300);
        database.executeQuery("SELECT * FROM Sometable");
    }

    @After
    public void stopsConnection2DB() {
        mysqld.stop();
    }

}