package edu.hm.cs.vss.impl;

import edu.hm.cs.vss.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Fabio Hellmann on 17.03.2016.
 */
public class TableImpl implements Table {
    /** If the table should be modified during runtime, then this list should be concurrent too. */
    private final List<Chair> chairs = new ArrayList<>();
    private final ConcurrentMap<Chair, Philosopher> blockedChairs = new ConcurrentHashMap<>();
    private final ConcurrentMap<Fork, Philosopher> blockedForks = new ConcurrentHashMap<>();

    @Override
    public void addChairs(int chairCount) {
        IntStream.rangeClosed(1, chairCount)
                .mapToObj(index -> new ChairImpl())
                .collect(Collectors.toCollection(() -> chairs));
    }

    @Override
    public Optional<Chair> getFreeChair() {
        return chairs.parallelStream()
                .filter(chair -> !blockedChairs.containsKey(chair))
                .findFirst();
    }

    @Override
    public Chair getNeighbourChair(Chair chair) {
        int indexOfChair = chairs.indexOf(chair);
        if(indexOfChair == 0) {
            indexOfChair = chairs.size();
        }
        return chairs.get(indexOfChair - 1); // Get the chair from the left hand side
    }

    @Override
    public Optional<Fork> getForksAtChair(Chair chair) {
        if(!blockedForks.containsKey(chair.getFork())) {
            return Optional.of(chair.getFork());
        } else {
            final Chair neighbourChair = getNeighbourChair(chair);
            if(!blockedForks.containsKey(neighbourChair.getFork())) {
                return Optional.of(neighbourChair.getFork());
            }
        }
        return Optional.empty();
    }

    @Override
    public void blockChair(Chair chair, Philosopher philosopher) {
        blockedChairs.put(chair, philosopher);
    }

    @Override
    public void unblockChair(Philosopher philosopher) {
        if(blockedChairs.containsValue(philosopher)) {
            blockedChairs.remove(blockedChairs.entrySet().parallelStream()
                    .filter(entry -> entry.getValue().equals(philosopher))
                    .findFirst()
                    .get()
                    .getKey());
        }
    }

    @Override
    public void blockFork(Fork fork, Philosopher philosopher) {
        blockedForks.put(fork, philosopher);
    }

    @Override
    public void unblockForks(Philosopher philosopher) {
        if(blockedForks.containsValue(philosopher)) {
            blockedForks.remove(blockedForks.entrySet().parallelStream()
                    .filter(entry -> entry.getValue().equals(philosopher))
                    .findFirst()
                    .get()
                    .getKey());
        }
    }

    @Override
    public void setTableManager(TableManager tableManager) {

    }

    @Override
    public Optional<TableManager> getTableManager() {
        return Optional.empty(); // TODO Replace with a real table manager if exists
    }
}
