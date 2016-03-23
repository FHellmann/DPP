package edu.hm.cs.vss.impl;

import edu.hm.cs.vss.Philosopher;
import edu.hm.cs.vss.Table;
import edu.hm.cs.vss.log.Logger;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Created by Fabio Hellmann on 17.03.2016.
 */
public class PhilosopherImpl implements Philosopher {
    private final String name;
    private Logger logger;
    private final Table table;
    private final long timeSleep;
    private final long timeEat;
    private final long timeMediate;
    private final Consumer<Philosopher> deadlock;
    private int eatIterations;
    private int mealCount;
    private long bannedTime = -1;

    public PhilosopherImpl(final String name,
                           final Logger logger,
                           final Table table,
                           final long timeSleep,
                           final long timeEat,
                           final long timeMediate,
                           final boolean veryHungry,
                           final Consumer<Philosopher> deadlock) {
        this(name, logger, table, timeSleep, timeEat, veryHungry ? timeMediate / 2 : timeMediate, deadlock, veryHungry ? DEFAULT_EAT_ITERATIONS * 2 : DEFAULT_EAT_ITERATIONS);
    }

    public PhilosopherImpl(final String name,
                           final Logger logger,
                           final Table table,
                           final long timeSleep,
                           final long timeEat,
                           final long timeMediate,
                           final Consumer<Philosopher> deadlock,
                           final int eatIterations) {
        this.name = name;
        this.logger = logger;
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
    public Logger getLogger() {
        return logger;
    }

    @Override
    public Table getTable() {
        return table;
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
    public void banned() {
        bannedTime = DEFAULT_TIME_TO_BANN;
    }

    @Override
    public void unbanned() {
        bannedTime = -1;
    }

    @Override
    public Optional<Long> getBannedTime() {
        if (bannedTime >= 0) {
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
