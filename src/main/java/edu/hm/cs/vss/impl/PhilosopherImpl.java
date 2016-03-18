package edu.hm.cs.vss.impl;

import edu.hm.cs.vss.Philosopher;
import edu.hm.cs.vss.Table;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Created by Fabio Hellmann on 17.03.2016.
 */
public class PhilosopherImpl implements Philosopher {
    private final String name;
    private final Table table;
    private final long timeSleep;
    private final long timeEat;
    private final long timeMediate;
    private final Consumer<Philosopher> deadlock;
    private volatile int forkCount;
    private int eatIterations;
    private int mealCount;
    private long bannedTime = -1;

    public PhilosopherImpl(final String name,
                           final Table table,
                           final long timeSleep,
                           final long timeEat,
                           final long timeMediate,
                           final Consumer<Philosopher> deadlock) {
        this(name, table, timeSleep, timeEat, timeMediate, deadlock, DEFAULT_EAT_ITERATIONS);
    }

    public PhilosopherImpl(final String name,
                           final Table table,
                           final long timeSleep,
                           final long timeEat,
                           final long timeMediate,
                           final Consumer<Philosopher> deadlock,
                           final int eatIterations) {
        this.name = name;
        this.table = table;
        this.timeSleep = timeSleep;
        this.timeEat = timeEat;
        this.timeMediate = timeMediate;
        this.deadlock = deadlock;
        this.eatIterations = eatIterations;
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
    public int getForkCount() {
        return forkCount;
    }

    @Override
    public void setForkCount(int forkCount) {
        this.forkCount = forkCount;
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
        return eatIterations;
    }

    @Override
    public void banned(long time) {
        bannedTime = time;
    }

    @Override
    public Optional<Long> getBannedTime() {
        if (bannedTime > 0) {
            return Optional.ofNullable(bannedTime);
        }
        return Optional.empty();
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

    @Override
    public Consumer<Philosopher> onDeadlock() {
        return deadlock;
    }
}
