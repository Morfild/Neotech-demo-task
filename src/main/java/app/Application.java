package app;


import app.db.ConnectionException;
import app.db.models.TimestampData;
import app.db.repositories.Repository;
import org.apache.commons.cli.*;

import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;

public class Application {

    private final ScheduledExecutorService timerScheduler = Executors.newSingleThreadScheduledExecutor();
    private final ExecutorService executor = Executors.newFixedThreadPool(1);
    private final Queue<TimestampData> queue = new ConcurrentLinkedQueue<>();
    private final Repository repository;
    private final long timerPeriod;

    private ScheduledFuture<?> timerFuture;

    Application(Repository repository, long timerPeriod) {
        this.repository = repository;
        this.timerPeriod = timerPeriod;
    }

    boolean run(String[] args) {
        CommandLine line = parseArguments(args);

        if (line == null) return false;

        if (line.hasOption('p')) {
            printAllEntries();
            return true;
        } else if (line.getArgList().isEmpty()) {
            startTimer();
            return true;
        } else {
            printAppHelp();
            return false;
        }
    }

    void stop() {
        if (timerFuture == null) return;
        timerFuture.cancel(true);
    }

    private void startTimer() {
        timerFuture = timerScheduler.scheduleAtFixedRate(() -> {
            insert(new TimestampData(new Date()));
        }, 0, timerPeriod, TimeUnit.MILLISECONDS);
    }

    private void insert(TimestampData data) {
        System.out.println("Tries to insert " + data.toString());

        if (queue.isEmpty()) {
            System.out.println("No queue. Inserting " + data.toString());
            executor.execute(() -> executeInsert(data));
        } else {
            System.out.println("There is a queue. " + data.toString());
        }

        queue.add(data);
    }

    private void executeInsert(TimestampData data) {
        try {
            repository.insertData(data);
            System.out.println(data.toString() + " inserted successfully");
            queue.poll();

            TimestampData peek = queue.peek();

            if (peek != null) {
                System.out.println("Tries to insert " + peek.toString() + " from queue");
                executeInsert(peek);
            } else {
                System.out.println("End of queue");
            }
        } catch (ConnectionException ignored) {
            ignored.printStackTrace();
            System.out.println("Connection error of " + data.toString() + ". Repeat...");
            executeInsert(data);
        }
    }

    private void printAllEntries() {
        List<TimestampData> entries = repository.fetchData();
        System.out.println("Total::" + entries.size());
        for (TimestampData data : entries) {
            System.out.println(data.toString());
        }
    }

    private CommandLine parseArguments(String[] args) {
        Options options = getOptions();
        CommandLine line = null;

        CommandLineParser parser = new DefaultParser();

        try {
            line = parser.parse(options, args);
        } catch (ParseException ex) {
            System.err.println("Failed to parse command line arguments");
            printAppHelp();
        }

        return line;
    }

    private Options getOptions() {
        Options options = new Options();
        options.addOption("p", false, "Use to show all database rows");
        options.addOption("", false, "Use empty arguments to execute the program");
        return options;
    }

    private void printAppHelp() {
        Options options = getOptions();
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("DemoApp", options);
    }

}
