package app.db.repositories;

import app.db.ConnectionException;
import app.db.MySQLDatabase;
import app.db.models.TimestampData;
import com.wix.mysql.EmbeddedMysql;
import com.wix.mysql.config.MysqldConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.util.List;

import static app.TestUtilities.createDateTime;
import static app.TestUtilities.parseDateTime;
import static com.wix.mysql.EmbeddedMysql.anEmbeddedMysql;
import static com.wix.mysql.config.MysqldConfig.aMysqldConfig;
import static com.wix.mysql.distribution.Version.v5_7_10;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class RepositoryTest {

    private final static String USER_NAME = "test-repository";
    private final static String USER_PASSWORD = "password";
    private final static String SCHEMA = "repository";

    private EmbeddedMysql mysqld;
    private Repository repository;

    @Before
    public void createsDBAndService() {
        MysqldConfig config = aMysqldConfig(v5_7_10)
                .withPort(3306)
                .withUser(USER_NAME, USER_PASSWORD)
                .build();

        mysqld = anEmbeddedMysql(config)
                .addSchema(SCHEMA)
                .start();

        MySQLDatabase database = new MySQLDatabase("localhost:3306", SCHEMA, USER_NAME, USER_PASSWORD, 1000);
        repository = new RepositoryImpl(database);
        repository.initialize();
    }

    @Test
    public void fetchEmptyDataMustReturnEmptyList() {
        assertThat(repository.fetchData().size(), is(0));
    }

    @Test
    public void insertsOneTimestampAndFetchDataMustReturnSameTimestamp() throws ParseException, ConnectionException {
        repository.insertData(new TimestampData(createDateTime("01/10/18 15:25:00")));

        List<TimestampData> data = repository.fetchData();

        assertThat(data.size(), is(1));
        assertThat(parseDateTime(data.get(0).getTimestamp()), is("01/10/18 15:25:00"));
    }

    @Test
    public void mustReturnSortedByAscendingOrder() throws ParseException, ConnectionException {
        repository.insertData(new TimestampData(createDateTime("01/10/18 15:25:00")));
        repository.insertData(new TimestampData(createDateTime("01/10/18 15:26:00")));
        repository.insertData(new TimestampData(createDateTime("01/10/18 15:27:00")));
        repository.insertData(new TimestampData(createDateTime("01/10/18 15:28:00")));

        List<TimestampData> data = repository.fetchData();

        assertThat(data.size(), is(4));
        assertThat(parseDateTime(data.get(0).getTimestamp()), is("01/10/18 15:25:00"));
        assertThat(parseDateTime(data.get(1).getTimestamp()), is("01/10/18 15:26:00"));
        assertThat(parseDateTime(data.get(2).getTimestamp()), is("01/10/18 15:27:00"));
        assertThat(parseDateTime(data.get(3).getTimestamp()), is("01/10/18 15:28:00"));
    }

    @After
    public void stopsConnection2DB() {
        mysqld.stop();
    }

}
