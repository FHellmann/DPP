package edu.hm.cs.vss.table;

import edu.hm.cs.vss.Chair;
import edu.hm.cs.vss.Fork;
import edu.hm.cs.vss.Philosopher;
import edu.hm.cs.vss.Table;
import edu.hm.cs.vss.impl.ChairImpl;
import edu.hm.cs.vss.log.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Fabio Hellmann on 17.03.2016.
 */
public class SimpleTable implements Table {
    /**
     * If the table should be modified during runtime, then this list should be concurrent too.
     */
    private final List<Chair> chairs = Collections.synchronizedList(new ArrayList<>());
    private final ConcurrentMap<Chair, Philosopher> blockedChairs = new ConcurrentHashMap<>();
    private final ConcurrentMap<Fork, Philosopher> blockedForks = new ConcurrentHashMap<>();
    private final Logger logger;

    public SimpleTable(final Logger logger) {
        this.logger = logger;
    }

    public Logger getLogger() {
        return logger;
    }

    @Override
    public void addChairs(int chairCount) {
        IntStream.rangeClosed(1, chairCount)
                .mapToObj(index -> new ChairImpl())
                .collect(Collectors.toCollection(() -> chairs));
    }

    @Override
    public Optional<Chair> getFreeChair(final Philosopher philosopher) {
        // TODO Kommentare
        // TODO Bessere Lösung möglich -> Philosophen müssen auf Plätze warten?!
        return chairs.parallelStream()
                .filter(chair -> !blockedChairs.containsKey(chair))
                .findFirst()
                .flatMap(chair -> blockChair(chair, philosopher));
    }

    @Override
    public Chair getNeighbourChair(final Chair chair) {
        int indexOfChair = chairs.indexOf(chair);
        if (indexOfChair == 0) {
            indexOfChair = chairs.size();
        }
        return chairs.get(indexOfChair - 1); // Get the chair from the left hand side
    }

    @Override
    public Optional<Chair> blockChair(Chair chair, Philosopher philosopher) {
        // TODO Kommentare
        if (blockedChairs.putIfAbsent(chair, philosopher) == null) {
            getLogger().log("Blocked " + chair.toString() + " by " + philosopher.getName());
            return Optional.of(chair);
        }
        return Optional.empty();
    }

    @Override
    public void unblockChair(Philosopher philosopher) {
        unblockForks(philosopher);
        blockedChairs.entrySet().stream()
                .filter(entry -> entry.getValue().equals(philosopher))
                .findFirst()
                .ifPresent(entry -> {
                    getLogger().log("Unblocked " + entry.getKey().toString() + " by " + philosopher.getName());
                    philosopher.say("Stand up from seat (" + entry.getKey().toString() + ")");
                    blockedChairs.remove(entry.getKey());
                });
    }

    @Override
    public Optional<Fork> blockFork(Fork fork, Philosopher philosopher) {
        if (blockedForks.putIfAbsent(fork, philosopher) == null) {
            getLogger().log("Blocked " + fork.toString() + " by " + philosopher.getName());
            return Optional.of(fork);
        }
        return Optional.empty();
    }

    @Override
    public void unblockForks(Philosopher philosopher) {
        final List<Fork> forkList = blockedForks.entrySet().stream()
                .filter(entry -> entry.getValue().equals(philosopher))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        forkList.forEach(fork -> {
                    getLogger().log("Unblocked " + fork.toString() + " by " + philosopher.getName());
                    philosopher.say("Release my fork (" + fork.toString() + ")");
                    blockedForks.remove(fork);
                });
    }
}
