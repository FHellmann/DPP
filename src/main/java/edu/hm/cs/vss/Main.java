package edu.hm.cs.vss;

import edu.hm.cs.vss.impl.SimpleTableManager;
import edu.hm.cs.vss.impl.TableImpl;
import edu.hm.cs.vss.log.FileLogger;
import edu.hm.cs.vss.log.merger.LogMerger;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * Created by Fabio Hellmann on 16.03.2016.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        int philosopherCount = 4;
        int chairCount = 4;
        final boolean veryHungry;
        if (args.length == 2) {
            philosopherCount = Integer.parseInt(args[0]);
            chairCount = Integer.parseInt(args[1]);
            veryHungry = args[2] != null && args[2].length() > 0;
        } else {
            veryHungry = false;
        }

        final Table table = new TableImpl();
        table.addChairs(chairCount);
        table.setTableManager(new SimpleTableManager());

        final int cpuCores = Runtime.getRuntime().availableProcessors() * 2; // With hyper-threading is 2 * cpu core count
        final ExecutorService executorService = Executors.newScheduledThreadPool(cpuCores);

        IntStream.rangeClosed(1, philosopherCount)
                .mapToObj(index -> {
                    final String name = (index == 1 && veryHungry) ? "Hungry-Philosopher-" + index : "Philosopher-" + index;
                    final Philosopher.Builder builder = new Philosopher.Builder()
                            .setLogger(new FileLogger(name))
                            .setTable(table)
                            .name(name)
                            .setDeadlockFunction(philosopher -> {
                                philosopher.releaseForks();
                                philosopher.onThreadSleep(1);
                            });
                    if (index == 1 && veryHungry) {
                        builder.setVeryHungry();
                    }
                    return builder.create();
                })
                .forEach(executorService::execute);

        // Exit the program
        System.out.println("Press ENTER to exit!");
        final Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        scanner.close();

        // Waiting for all threads to finish
        executorService.shutdown();
        try {
            executorService.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Merge all log files
        final File file = new File(".");
        LogMerger.merge(file);
    }
}
