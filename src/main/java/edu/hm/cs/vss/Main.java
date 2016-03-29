package edu.hm.cs.vss;

import edu.hm.cs.vss.log.EmptyLogger;
import edu.hm.cs.vss.log.FileLogger;
import edu.hm.cs.vss.log.Logger;
import edu.hm.cs.vss.log.merger.LogMerger;
import edu.hm.cs.vss.table.TableWithMaster;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Fabio Hellmann on 16.03.2016.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        final long runtime; // Duration of the program activity
        final int philosopherCount; // Amount of philosophers
        final int chairCount; // Amount of chairs
        final boolean veryHungry; // Is the first philosopher vergy hungry?
        if (args.length == 4) {
            // Manual user input
            int index = 0;
            runtime = TimeUnit.MILLISECONDS.convert(Long.parseLong(args[index++]), TimeUnit.SECONDS);
            philosopherCount = Integer.parseInt(args[index++]);
            chairCount = Integer.parseInt(args[index++]);
            veryHungry = args[index] != null && args[index].length() > 0;
        } else {
            // Defaults
            runtime = TimeUnit.MILLISECONDS.convert(1, TimeUnit.MINUTES);
            philosopherCount = 5;
            chairCount = 5;
            veryHungry = false;
        }

        final Table table = new TableWithMaster(new EmptyLogger());
        table.addChairs(chairCount);

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
                                try {
                                    philosopher.onThreadSleep((long) (Math.random() * philosopherCount));
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
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
        executorService.shutdownNow();
        try {
            executorService.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Threads terminated");

        Logger logger = new FileLogger("statistic");
        logger.log("############# Statistic #############");
        logger.log("# Hardware");
        logger.log("CPU-Cores (available to the JVM) = " + Runtime.getRuntime().availableProcessors());
        logger.log("Memory (available to the JVM) = " + Runtime.getRuntime().maxMemory());
        logger.log("# Philosophers");
        philosopherList.stream()
                .map(philosopher -> philosopher.getName() + ": " + philosopher.getMealCount())
                .forEach(logger::log);
        logger.log("############### END #################");

        // Merge all log files
        final File file = new File(".");
        LogMerger.merge(file);
    }
}
