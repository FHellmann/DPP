package edu.hm.cs.vss.impl;

import edu.hm.cs.vss.Fork;
import edu.hm.cs.vss.Philosopher;
import edu.hm.cs.vss.Table;

/**
 * Created by Fabio Hellmann on 17.03.2016.
 */
public class PhilosopherImpl implements Philosopher {
    private final String name;
    private final Table table;
    private final long timeSleep;
    private final long timeEat;
    private final long timeMediate;
    private int forkCount;
    private int mealCount;

    public PhilosopherImpl(final String name, final Table table, final long timeSleep, final long timeEat, final long timeMediate) {
        this.name = name;
        this.table = table;
        this.timeSleep = timeSleep;
        this.timeEat = timeEat;
        this.timeMediate = timeMediate;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Table getTable() {
        return table;
    }

    @Override
    public void pickUpFork(final Fork fork) {
        table.blockFork(fork, this);
        forkCount++;
    }

    @Override
    public void releaseForks() {
        table.unblockForks(this);
        forkCount = 0;
    }

    @Override
    public int getForkCount() {
        return forkCount;
    }

    @Override
    public int getMealCount() {
        return mealCount;
    }

    @Override
    public void incrementMealCount() {
        mealCount++;
    }

    @Override
    public int getEatIterationCount() {
        return 3; // Default is 3... when the Philosopher is very hungry then this number should be increased
    }

    @Override
    public long getTimeToSleep() {
        return timeSleep;
    }

    @Override
    public long getTimeToEat() {
        return timeEat;
    }

    @Override
    public long getTimeToMediate() {
        return timeMediate;
    }
}
