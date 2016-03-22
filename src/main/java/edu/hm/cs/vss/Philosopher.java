package edu.hm.cs.vss;

import edu.hm.cs.vss.impl.PhilosopherImpl;
import edu.hm.cs.vss.log.ConsoleLogger;
import edu.hm.cs.vss.log.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by Fabio Hellmann on 17.03.2016.
 */
public interface Philosopher extends Runnable {
    int DEFAULT_EAT_ITERATIONS = 3;
    long DEFAULT_TIME_TO_SLEEP = TimeUnit.MILLISECONDS.convert(10, TimeUnit.MILLISECONDS);
    long DEFAULT_TIME_TO_MEDIATE = TimeUnit.MILLISECONDS.convert(5, TimeUnit.MILLISECONDS);
    long DEFAULT_TIME_TO_EAT = TimeUnit.MILLISECONDS.convert(1, TimeUnit.MILLISECONDS);
    long DEFAULT_TIME_TO_BANN = TimeUnit.MILLISECONDS.convert(0, TimeUnit.MILLISECONDS);

    /**
     * Get the name of the philosopher.
     *
     * @return the name.
     */
    String getName();

    /**
     * Get the logger of the philosopher.
     *
     * @return the logger.
     */
    Logger getLogger();

    /**
     * Get the table where the philosopher can get something to eat.
     *
     * @return the table.
     */
    Table getTable();

    /**
     * Get the amount of forks the philosopher hold in his hands.
     *
     * @return the amount of forks.
     */
    int getForkCount();

    /**
     * Set the amount of forks the philosopher hold in his hands now.
     */
    void setForkCount(final int count);

    /**
     * Get the amount of eaten meals.
     *
     * @return the amount of eaten meals.
     */
    int getMealCount();

    /**
     * If a meal was eat increment the counter.
     */
    void incrementMealCount();

    /**
     * Get the iteration count of how many times the philosopher want's to eat something. (Default is 3)
     *
     * @return the iteration count.
     */
    int getEatIterationCount();

    /**
     * Refuse the philosopher a seat at the table.
     */
    void banned();

    /**
     * Allow the philosopher to sit down at the table.
     */
    void unbanned();

    /**
     * Get the time the philosopher is no longer allowed to sit at the table.
     *
     * @return the time.
     */
    Optional<Long> getBannedTime();

    /**
     * Get the time to sleep. (in Milliseconds)
     *
     * @return the time to sleep.
     */
    long getTimeToSleep();

    /**
     * Get the time to eat. (in Milliseconds)
     *
     * @return the time to eat.
     */
    long getTimeToEat();

    /**
     * Get the time to mediate. (in Milliseconds)
     *
     * @return the time to mediate.
     */
    long getTimeToMediate();

    /**
     * Get the action to do on cause of a deadlock.
     *
     * @return the deadlock action.
     */
    Consumer<Philosopher> onDeadlock();

    default Chair sitDown() {
        return waitingForFreeChair();
    }

    default void standUp() {
        getTable().unblockChair(this);
        setForkCount(0);
    }

    default Fork pickFork(final Chair chair) {
        return waitingForFork(chair);
    }

    default void releaseForks() {
        getTable().unblockForks(this);
    }

    default Chair waitingForFreeChair() {
        say("Waiting for a free chair...");
        Optional<Chair> chairOptional = Optional.empty();
        while (!chairOptional.isPresent()) {
            chairOptional = getTable().getFreeChair(this);
            getBannedTime().ifPresent(time -> {
                say("The table master banned me! :(");
                onThreadSleep(getBannedTime().get());
            });
        }
        say("Sit down on " + chairOptional.get().toString());
        return chairOptional.get();
    }

    default Fork waitingForFork(final Chair chair) {
        say("Waiting for my " + (getForkCount() + 1) + " fork from " + chair.toString() + " to eat...");
        Optional<Fork> forkOptional = Optional.empty();
        int count = 0;
        while (!forkOptional.isPresent()) {
            forkOptional = getTable().getForkAtChair(chair, this);

            // Deadlock Detection!
            if (count++ > 3 && getForkCount() < 2 && !forkOptional.isPresent()) {
                onDeadlock().accept(this);
                count = 0;
            }
        }
        say("I've got my " + (getForkCount() + 1) + " fork from " + chair.toString());
        setForkCount(getForkCount() + 1);
        return forkOptional.get();
    }

    /**
     * The philosopher is eating.
     */
    default void eat() {
        incrementMealCount();
        say("Eating for " + getTimeToEat() + " ms");
        onThreadSleep(getTimeToEat());
    }

    /**
     * The philosopher is mediating.
     */
    default void mediate() {
        say("Mediating for " + getTimeToMediate() + " ms");
        onThreadSleep(getTimeToMediate());
    }

    /**
     * The philosopher is sleeping.
     */
    default void sleep() {
        say("Sleeping for " + getTimeToSleep() + " ms");
        onThreadSleep(getTimeToSleep());
    }

    /**
     * What the philosopher do in his life...
     */
    default void run() {
        say("I'm alive!");

        while (!Thread.currentThread().isInterrupted()) {
            // 3 Iterations by default... or more if the philosopher is very hungry
            IntStream.rangeClosed(0, getEatIterationCount() - 1)
                    .mapToObj(index -> sitDown())
                    .peek(chair -> Stream.of(chair, getTable().getNeighbourChair(chair))
                            .parallel().forEach(this::pickFork))
                    .peek(chair -> eat()) // Has a seat + 2 Forks -> eat
                    .peek(chair -> say("Stand up from " + chair.toString()))
                    .peek(chair -> standUp())
                    .forEach(chair -> mediate()); // Stand up and go to mediation / sleep

            // Sleep
            sleep();
        }
    }

    default void say(final String message) {
        getLogger().log("[" + getName() + "; Meals=" + getMealCount() + "]: " + message);
    }

    default void onThreadSleep(final long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    class Builder {
        private static int count = 1;
        private String name = "Philosopher-" + (count++);
        private Logger logger = new ConsoleLogger();
        private Table table;
        private long timeSleep = DEFAULT_TIME_TO_SLEEP;
        private long timeEat = DEFAULT_TIME_TO_EAT;
        private long timeMediate = DEFAULT_TIME_TO_MEDIATE;
        private Consumer<Philosopher> deadlockConsumer = philosopher -> philosopher.say("I'm in a deadlock!");
        private boolean veryHungry;

        public Builder name(final String name) {
            this.name = name;
            return this;
        }

        public Builder setTable(final Table table) {
            this.table = table;
            return this;
        }

        public Builder setLogger(final Logger logger) {
            this.logger = logger;
            return this;
        }

        public Builder setTimeToSleep(final long timeToSleep) {
            this.timeSleep = timeToSleep;
            return this;
        }

        public Builder setTimeToEat(final long timeToEat) {
            this.timeEat = timeToEat;
            return this;
        }

        public Builder setTimeToMediate(final long timeToMediate) {
            this.timeMediate = timeToMediate;
            return this;
        }

        public Builder setVeryHungry() {
            this.veryHungry = true;
            return this;
        }

        public Builder setDeadlockFunction(final Consumer<Philosopher> deadlockConsumer) {
            this.deadlockConsumer = deadlockConsumer;
            return this;
        }

        public Philosopher create() {
            if (table == null) {
                throw new NullPointerException("Table can not be null. Use new Philosopher.Builder().setTable(Table).create()");
            }
            return new PhilosopherImpl(name, logger, table, timeSleep, timeEat, timeMediate, veryHungry, deadlockConsumer);
        }
    }
}
