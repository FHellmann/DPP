package edu.hm.cs.vss.table;

import edu.hm.cs.vss.Chair;
import edu.hm.cs.vss.Philosopher;
import edu.hm.cs.vss.log.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Fabio on 22.03.2016.
 */
public class TableWithMaster extends SimpleTable {
    private static final int MAX_DEVIATION = 10;
    /**
     * If the table should be modified during runtime, then this list should be concurrent too.
     */
    private final List<Philosopher> philosopherList = new CopyOnWriteArrayList<>();

    public TableWithMaster(Logger logger) {
        super(logger);
    }

    @Override
    public Optional<Chair> getFreeChair(Philosopher philosopher) {
        if (!philosopherList.contains(philosopher)) {
            philosopherList.add(philosopher);
        }

        // TODO Kann bei zu vielen Philosophen zu lange dauern!
        if (philosopher.getMealCount() >= philosopherList.stream().mapToInt(Philosopher::getMealCount).min().orElse(0) + MAX_DEVIATION) {
            philosopher.banned();
            return Optional.empty();
        }

        philosopher.unbanned();
        return super.getFreeChair(philosopher);
    }
}
