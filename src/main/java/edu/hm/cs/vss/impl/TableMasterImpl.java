package edu.hm.cs.vss.impl;

import edu.hm.cs.vss.Philosopher;
import edu.hm.cs.vss.TableMaster;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Fabio on 22.03.2016.
 */
public class TableMasterImpl implements TableMaster, Philosopher.OnStandUpListener {
    private final List<Philosopher> philosopherList = new CopyOnWriteArrayList<>();
    private volatile int minMealCount;

    @Override
    public void register(Philosopher philosopher) {
        philosopherList.add(philosopher);
        philosopher.setOnStandUpListener(this);
    }

    @Override
    public void unregister(Philosopher philosopher) {
        philosopherList.remove(philosopher);
        philosopher.setOnStandUpListener(null);
    }

    @Override
    public boolean isAllowedToTakeSeat(Philosopher philosopher) {
        return philosopher.getMealCount() <= MAX_DEVIATION + minMealCount;
    }

    @Override
    public void onStandUp(Philosopher philosopher) {
        minMealCount = philosopherList.parallelStream().mapToInt(Philosopher::getMealCount).min().orElse(0);
    }
}
