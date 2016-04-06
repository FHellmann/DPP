package edu.hm.cs.vss.impl;

import edu.hm.cs.vss.Chair;
import edu.hm.cs.vss.Philosopher;
import edu.hm.cs.vss.Table;
import edu.hm.cs.vss.TableMaster;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by Fabio Hellmann on 17.03.2016.
 */
public class TableImpl implements Table {
    private static final TableMaster DEFAULT_TABLE_MASTER = philosopher -> true;
    private final List<Chair> chairs = Collections.synchronizedList(new ArrayList<>());
    private TableMaster tableMaster = DEFAULT_TABLE_MASTER;

    public TableImpl(final int chairCount) {
        if(chairCount < 2) {
            throw new IllegalArgumentException("The chair count need to be greater or equal then 2");
        }
        addChairs(chairCount);
    }

    @Override
    public void addChairs(int chairCount) {
        IntStream.rangeClosed(1, chairCount)
                .mapToObj(index -> new Chair.Builder().create())
                .forEach(this::addChair);
    }

    @Override
    public void addChair(Chair chair) {
        chairs.add(chair);
    }

    @Override
    public Stream<Chair> getFreeChairs(final Philosopher philosopher) throws InterruptedException {
        if (!getTableMaster().isAllowedToTakeSeat(philosopher)) {
            return Stream.empty();
        }
        return chairs.parallelStream().filter(Chair::isAvailable);
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
        if(tableMaster == null) {
            this.tableMaster = DEFAULT_TABLE_MASTER;
        } else {
            this.tableMaster = tableMaster;
        }
    }

    @Override
    public TableMaster getTableMaster() {
        return tableMaster;
    }
}
