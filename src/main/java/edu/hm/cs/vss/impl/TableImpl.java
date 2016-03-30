package edu.hm.cs.vss.impl;

import edu.hm.cs.vss.*;
import edu.hm.cs.vss.impl.ChairImpl;
import edu.hm.cs.vss.log.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by Fabio Hellmann on 17.03.2016.
 */
public class TableImpl implements Table {
    /**
     * If the table should be modified during runtime, then this list should be concurrent too.
     */
    private final List<Chair> chairs = Collections.synchronizedList(new ArrayList<>());
    private final Logger logger;
    private TableMaster tableMaster;

    public TableImpl(final Logger logger) {
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
    public Stream<Chair> getFreeChairs(final Philosopher philosopher) {
        if(getTableMaster().isPresent() && getTableMaster().get().isAllowedToTakeSeat(philosopher)) {
            return chairs.parallelStream().filter(Chair::isAvailable);
        }
        return Stream.empty();
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
    public void setTableMaster(TableMaster tableMaster) {
        this.tableMaster = tableMaster;
    }

    @Override
    public Optional<TableMaster> getTableMaster() {
        return Optional.ofNullable(tableMaster);
    }
}
