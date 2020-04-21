package app;

import app.db.ConnectionException;
import app.db.models.TimestampData;
import app.db.repositories.Repository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import static app.TestUtilities.createDateTime;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

public class ApplicationTest {

    private Repository repositoryMock;
    private Application application;

    @Before
    public void init() {
        repositoryMock = Mockito.spy(new MockRepository(2));
        application = new Application(repositoryMock, 100);
    }

    @Test
    public void runsAppWithWrongArgMustReturnFalse() {
        boolean result = application.run(new String[]{"-d"});
        assertThat(result, is(false));
    }

    @Test
    public void runsAppMultipleArgsButOneIsCorrectMustReturnFalse() {
        boolean result = application.run(new String[]{"-p", "-d"});
        assertThat(result, is(false));
    }

    @Test
    public void runsAppWithCorrectArgMustPrintAllEntries() throws ParseException {
        when(repositoryMock.fetchData()).thenReturn(singletonList(new TimestampData(createDateTime("01/10/18 15:25:00"))));

        boolean result = application.run(new String[]{"-p"});
        assertThat(result, is(true));

        verify(repositoryMock, times(1)).fetchData();
    }

    @Test
    public void runsProgramWithoutArgsMustStartTimerAndWritesTimestampToDB() throws ConnectionException {
        boolean result = application.run(new String[]{});
        assertThat(result, is(true));

        verify(repositoryMock, times(1)).insertData(any(TimestampData.class));

        application.stop();
    }

    @Test
    public void runsProgramWithoutArgsMustStartTimerAndWritesTimestampToDBWhenSlowConnectionMustRepeat() throws ConnectionException, InterruptedException {
        boolean result = application.run(new String[]{});
        assertThat(result, is(true));

        Thread.sleep(1000);

        ArgumentCaptor<TimestampData> captor = ArgumentCaptor.forClass(TimestampData.class);

        // must be executed 10 times, duration 1 second by 100 ms per each, every 2nd delays 200ms
        verify(repositoryMock, times(10)).insertData(captor.capture());

        application.stop();

        List<TimestampData> allInsertedItems = captor.getAllValues();
        assertThat(allInsertedItems.size(), is(10));

        for (int i = 1; i < allInsertedItems.size(); i++) {
            Date current = allInsertedItems.get(i - 1).getTimestamp();
            Date next = allInsertedItems.get(i).getTimestamp();
            assertThat(current.before(next), is(true));
        }
    }

    static class MockRepository implements Repository {

        private final int doSleepEvery;
        private int currentIndex = 0;

        MockRepository(int doSleepEvery) {
            this.doSleepEvery = doSleepEvery;
        }

        @Override
        public void initialize() { }

        @Override
        public List<TimestampData> fetchData() {
            return null;
        }

        @Override
        public void insertData(TimestampData data) {
            currentIndex++;
            if (currentIndex % doSleepEvery == 0) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ignored) { }
            }
        }
    }

}
