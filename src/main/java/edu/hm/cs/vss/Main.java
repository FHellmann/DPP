package edu.hm.cs.vss;

import edu.hm.cs.vss.table.SimpleTable;
import edu.hm.cs.vss.log.FileLogger;
import edu.hm.cs.vss.log.merger.LogMerger;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Fabio Hellmann on 16.03.2016.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        long runtime = TimeUnit.MILLISECONDS.convert(10, TimeUnit.SECONDS);
        int philosopherCount = 4;
        int chairCount = 4;
        final boolean veryHungry;
        if (args.length == 3) {
            int index = 0;
            runtime = TimeUnit.MILLISECONDS.convert(Long.parseLong(args[index++]), TimeUnit.SECONDS);
            philosopherCount = Integer.parseInt(args[index++]);
            chairCount = Integer.parseInt(args[index++]);
            veryHungry = args[index] != null && args[index].length() > 0;
        } else {
            veryHungry = false;
        }

        final Table table = new SimpleTable();
        table.addChairs(chairCount);

        final int cpuCores = Runtime.getRuntime().availableProcessors() * 2; // With hyper-threading is 2 * cpu core count
        final ExecutorService executorService = Executors.newCachedThreadPool(r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        });

        final List<Philosopher> philosopherList = IntStream.rangeClosed(1, philosopherCount)
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
                .collect(Collectors.toList());

        System.out.println("Start program...");

        philosopherList.forEach(executorService::execute);

        try {
            Thread.sleep(runtime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Exit program! [Runtime = " + runtime + "]");

        // Waiting for all threads to finish
        executorService.shutdown();
        try {
            executorService.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Threads terminated");

        // Merge all log files
        final File file = new File(".");
        LogMerger.merge(file);
    }
}
