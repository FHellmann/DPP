package edu.hm.cs.vss;

import edu.hm.cs.vss.impl.SimpleTableManager;
import edu.hm.cs.vss.impl.TableImpl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

/**
 * Created by Fabio Hellmann on 16.03.2016.
 */
public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        int philosopherCount = 4;
        int chairCount = 4;
        if (args.length == 2) {
            philosopherCount = Integer.parseInt(args[0]);
            chairCount = Integer.parseInt(args[1]);
        }

        System.setOut(new PrintStream(new FileOutputStream(new File("log.txt"))));

        final Table table = new TableImpl();
        table.addChairs(chairCount);
        //table.setTableManager(new SimpleTableManager());

        final ExecutorService executorService = Executors.newFixedThreadPool(philosopherCount);

        IntStream.rangeClosed(1, philosopherCount)
                .mapToObj(index -> new Philosopher.Builder()
                        .with(table)
                        .setDeadlockFunction(philosopher -> {
                            philosopher.getTable().unblockForks(philosopher);
                        })
                        .create())
                .forEach(executorService::submit);
    }
}
