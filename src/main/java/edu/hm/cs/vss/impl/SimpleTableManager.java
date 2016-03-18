package edu.hm.cs.vss.impl;

import edu.hm.cs.vss.Philosopher;
import edu.hm.cs.vss.TableManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.OptionalInt;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * Created by Fabio Hellmann on 18.03.2016.
 */
public class SimpleTableManager implements TableManager {
    private static final int MAX_DEVIATION = 10;

    private final List<Philosopher> philosophers = Collections.synchronizedList(new ArrayList<>());

    @Override
    public Boolean apply(Philosopher philosopher) {
        // Add philosopher if not already registered
        if(!philosophers.contains(philosopher)) {
            philosophers.add(philosopher);
        }

        // Check the meals between all philosophers
        final OptionalInt minMealCount = philosophers.parallelStream()
                .mapToInt(Philosopher::getMealCount)
                .min();

        if(minMealCount.isPresent() && philosopher.getMealCount() >= minMealCount.getAsInt() + MAX_DEVIATION) {
            philosopher.banned(TimeUnit.MILLISECONDS.convert(100, TimeUnit.MILLISECONDS));
            return false;
        }
        philosopher.unbanned();
        return true;
    }
}
