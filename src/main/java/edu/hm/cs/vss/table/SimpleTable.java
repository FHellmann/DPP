package edu.hm.cs.vss.table;

import edu.hm.cs.vss.Chair;
import edu.hm.cs.vss.Fork;
import edu.hm.cs.vss.Philosopher;
import edu.hm.cs.vss.Table;
import edu.hm.cs.vss.impl.ChairImpl;

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

    public Map<Chair, Philosopher> getChairsByPhilosopher() {
        return blockedChairs;
    }

    public Map<Fork, Philosopher> getForksByPhilosopher() {
        return blockedForks;
    }

    @Override
    public void addChairs(int chairCount) {
        IntStream.rangeClosed(1, chairCount)
                .mapToObj(index -> new ChairImpl())
                .collect(Collectors.toCollection(() -> chairs));
    }

    @Override
    public Optional<Chair> getFreeChair(final Philosopher philosopher) {
        final Optional<Chair> chairOptional = chairs.parallelStream()
                .filter(chair -> !blockedChairs.containsKey(chair))
                .findFirst();
        chairOptional.ifPresent(chair -> blockChair(chair, philosopher));
        return chairOptional;
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
    public boolean isForkFree(Fork fork) {
        return !blockedForks.containsKey(fork);
    }

    @Override
    public void blockChair(Chair chair, Philosopher philosopher) {
        blockedChairs.put(chair, philosopher);
    }

    @Override
    public void unblockChair(Philosopher philosopher) {
        unblockForks(philosopher);
        if (blockedChairs.containsValue(philosopher)) {
            final Optional<Map.Entry<Chair, Philosopher>> chairPhilosopherEntry = blockedChairs.entrySet().stream()
                    .filter(entry -> entry.getValue().equals(philosopher))
                    .findFirst();
            if (chairPhilosopherEntry.isPresent()) {
                blockedChairs.remove(chairPhilosopherEntry.get().getKey());
            }
        }
    }

    @Override
    public void blockFork(Fork fork, Philosopher philosopher) {
        blockedForks.put(fork, philosopher);
    }

    @Override
    public void unblockForks(Philosopher philosopher) {
        if (blockedForks.containsValue(philosopher)) {
            final Optional<Map.Entry<Fork, Philosopher>> forkPhilosopherEntry = blockedForks.entrySet().stream()
                    .filter(entry -> entry.getValue().equals(philosopher))
                    .findFirst();
            if (forkPhilosopherEntry.isPresent()) {
                blockedForks.remove(forkPhilosopherEntry.get().getKey());
            }
        }
    }
}
