package edu.hm.cs.vss;

import edu.hm.cs.vss.impl.PhilosopherImpl;
import edu.hm.cs.vss.log.ConsoleLogger;
import edu.hm.cs.vss.log.EmptyLogger;
import edu.hm.cs.vss.log.Logger;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.IntStream;

/**
 * Created by Fabio Hellmann on 17.03.2016.
 */
public interface Philosopher extends Runnable {
    int DEFAULT_EAT_ITERATIONS = 3;
    int MAX_DEADLOCK_COUNT = 3;
    long DEFAULT_TIME_TO_SLEEP = TimeUnit.MILLISECONDS.convert(10, TimeUnit.MILLISECONDS);
    long DEFAULT_TIME_TO_MEDIATE = TimeUnit.MILLISECONDS.convert(5, TimeUnit.MILLISECONDS);
    long DEFAULT_TIME_TO_EAT = TimeUnit.MILLISECONDS.convert(1, TimeUnit.MILLISECONDS);
    long DEFAULT_TIME_TO_BANN = TimeUnit.MILLISECONDS.convert(5, TimeUnit.MILLISECONDS);

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

    default Chair waitForSitDown() {
        say("Waiting for a nice seat...");

        Optional<Chair> chairOptional;
        do {
            chairOptional = getTable().getFreeChair(this);

            getBannedTime().ifPresent(time -> {
                say("I'm banned for " + time + " ms :'(");
                try {
                    onThreadSleep(time);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        } while (!chairOptional.isPresent());

        say("Found a nice seat (" + chairOptional.get().toString() + ")");

        return chairOptional.get();
    }

    default void standUp() {
        getTable().unblockChair(this);
    }

    default void waitForForks(final Chair chair) {
        say("Waiting for 2 forks...");

        final Fork fork = chair.getFork();

        final Chair neighbourChair = getTable().getNeighbourChair(chair);
        final Fork neighbourFork = neighbourChair.getFork();

        int deadlockDetectionCount = 0;
        int forkCount = 0;

        // TODO 2 Schleifen für diesen Vorgang
        do {
            if (getTable().blockFork(fork, this).isPresent()) {
                say("Picked up fork (" + fork.toString() + ")");
                forkCount++;
            }

            if (getTable().blockFork(neighbourFork, this).isPresent()) {
                say("Picked up fork (" + neighbourFork.toString() + ")");
                forkCount++;
            }

            if (deadlockDetectionCount++ > MAX_DEADLOCK_COUNT) {
                onDeadlock().accept(this);
                deadlockDetectionCount = 0;
                forkCount = 0;
            }
        } while (forkCount < 2);

        say("Found 2 forks! :D");
    }

    default void releaseForks() {
        getTable().unblockForks(this);
    }

    /**
     * The philosopher is eating.
     */
    default void eat() throws InterruptedException {
        incrementMealCount();
        say("Eating for " + getTimeToEat() + " ms");
        onThreadSleep(getTimeToEat());
    }

    /**
     * The philosopher is mediating.
     */
    default void mediate() throws InterruptedException {
        say("Mediating for " + getTimeToMediate() + " ms");
        onThreadSleep(getTimeToMediate());
    }

    /**
     * The philosopher is sleeping.
     */
    default void sleep() throws InterruptedException {
        say("Sleeping for " + getTimeToSleep() + " ms");
        onThreadSleep(getTimeToSleep());
    }

    /**
     * What the philosopher do in his life...
     */
    default void run() {
        say("I'm alive!");

        try {
            while (!Thread.currentThread().isInterrupted()) {
                // 3 Iterations by default... or more if the philosopher is very hungry
                IntStream.rangeClosed(0, getEatIterationCount() - 1)
                        .mapToObj(index -> waitForSitDown()) // Sit down on a free chair -> waiting for a free
                        .peek(this::waitForForks) // Grab two forks -> waiting for two free
                        .peek(tmp -> {
                            try {
                                eat();
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }) // Eat the next portion
                        .peek(tmp -> standUp()) // Stand up from chair
                        .forEach(tmp -> {
                            try {
                                mediate();
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }); // Go to mediate

                // Sleep
                sleep();
            }
        } catch (Exception e) {
        }
    }

    default void say(final String message) {
        getLogger().log("[" + getName() + "; Meals=" + getMealCount() + "]: " + message);
    }

    default void onThreadSleep(final long time) throws InterruptedException {
        Thread.sleep(time);
    }

    class Builder {
        private static int count = 1;
        private String name = "Philosopher-" + (count++);
        private Logger logger = new EmptyLogger();
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
