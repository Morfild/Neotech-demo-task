package app;

import app.db.Database;
import app.db.MySQLDatabase;
import app.db.repositories.Repository;
import app.db.repositories.RepositoryImpl;

import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        System.out.println("========" + Arrays.toString(args) + "==========");
        Database database = new MySQLDatabase(
                String.format("%s:%s", System.getenv("DB_HOST"), System.getenv("DB_PORT")),
                System.getenv("DB_SCHEME"),
                System.getenv("DB_USER"),
                System.getenv("DB_PASSWORD"),
                Long.parseLong(System.getenv("CONNECTION_TIMEOUT"))
        );
        Repository repository = new RepositoryImpl(database);
        repository.initialize();
        Application application = new Application(repository, Long.parseLong(System.getenv("TIMER_PERIOD")));
        application.run(args);
    }

}
