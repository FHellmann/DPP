package edu.hm.cs.vss;

import edu.hm.cs.vss.impl.TableImpl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

/**
 * Created by Fabio Hellmann on 16.03.2016.
 */
public class Main {
    public static void main(String[] args) {
        int philosopherCount = 4;
        int chairCount = 4;
        if (args.length == 2) {
            philosopherCount = Integer.parseInt(args[0]);
            chairCount = Integer.parseInt(args[1]);
        }

        final Table table = new TableImpl();
        table.addChairs(chairCount);

        final ExecutorService executorService = Executors.newFixedThreadPool(philosopherCount);

        IntStream.rangeClosed(1, philosopherCount)
                .mapToObj(index -> new Philosopher.Builder()
                        .with(table)
                        .create())
                .forEach(executorService::execute);
    }
}
