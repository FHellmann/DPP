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
    private final List<Philosopher> philosophers = Collections.synchronizedList(new ArrayList<>());

    @Override
    public List<Philosopher> getManagedPhilosophers() {
        return philosophers;
    }
}
