package edu.hm.cs.vss.table;

import edu.hm.cs.vss.Chair;
import edu.hm.cs.vss.Philosopher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Created by Fabio on 22.03.2016.
 */
public class TableWithMaster extends SimpleTable {
    private static final int MAX_DEVIATION = 10;
    /**
     * If the table should be modified during runtime, then this list should be concurrent too.
     */
    private final List<Philosopher> philosopherList = Collections.synchronizedList(new ArrayList<>());

    @Override
    public Optional<Chair> getFreeChair(Philosopher philosopher) {
        if (!philosopherList.contains(philosopher)) {
            philosopherList.add(philosopher);
        }

        if (philosopher.getMealCount() >= philosopherList.stream().mapToInt(Philosopher::getMealCount).min().orElse(0) + MAX_DEVIATION) {
            philosopher.banned();
            return Optional.empty();
        }

        philosopher.unbanned();
        return super.getFreeChair(philosopher);
    }
}
