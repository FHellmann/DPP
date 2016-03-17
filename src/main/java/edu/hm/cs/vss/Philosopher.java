package edu.hm.cs.vss;

import edu.hm.cs.vss.impl.PhilosopherImpl;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by Fabio Hellmann on 17.03.2016.
 */
public interface Philosopher extends Runnable {

    /**
     * Get the name of the philosopher.
     *
     * @return the name.
     */
    String getName();

    /**
     * Get the table where the philosopher can get something to eat.
     *
     * @return the table.
     */
    Table getTable();

    /**
     * Sit down on a free chair.
     *
     * @param chair to sit on
     */
    default void sitDown(final Chair chair) {
        getTable().blockChair(chair, this);
    }

    /**
     * Stand up if the hunger is over.
     */
    default void standUp() {
        releaseForks();
        getTable().unblockChair(this);
    }

    /**
     * Pick up a fork to eat (2 forks are needed).
     *
     * @param fork to eat.
     */
    void pickUpFork(final Fork fork);

    /**
     * Release all the forks, to get ready for standing up.
     */
    void releaseForks();

    /**
     * Get the amount of forks the philosopher hold in his hands.
     *
     * @return the amount of forks.
     */
    int getForkCount();

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
     * The philosopher is eating.
     */
    default void eat() {
        incrementMealCount();
        try {
            say("Eating for " + getTimeToEat() + " ms");
            Thread.sleep(getTimeToEat());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * The philosopher is mediating.
     */
    default void mediate() {
        try {
            say("Mediating for " + getTimeToMediate() + " ms");
            Thread.sleep(getTimeToMediate());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * The philosopher is sleeping.
     */
    default void sleep() {
        try {
            say("Sleeping for " + getTimeToSleep() + " ms");
            Thread.sleep(getTimeToSleep());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

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
     * What the philosopher do in his life...
     */
    default void run() {
        say("I'm alive!");
        final Table table = getTable();

        while (true) {
            // 3 Iterations by default... or more if the philosopher is very hungry
            IntStream.rangeClosed(0, getEatIterationCount() - 1)
                    .peek(index -> say("Waiting for a free chair..."))
                    .mapToObj(index -> table.getFreeChair())
                    .filter(Optional::isPresent) // Looking for free seat
                    .map(Optional::get)
                    .peek(this::sitDown) // Seat found -> sit down
                    .peek(chair -> {
                        say("I've found a nice seat!");

                        while (getForkCount() < 2) { // Waiting for 2 forks
                            Stream.of(chair, table.getNeighbourChair(chair)).parallel() // Iterate over both forks
                                    .map(table::getForksAtChair)
                                    .filter(Optional::isPresent) // If fork is available
                                    .map(Optional::get)
                                    .forEach(this::pickUpFork); // Fork found -> Pick fork
                        }

                        say("I got " + getForkCount() + " forks. YES!");
                    })
                    .peek(chair -> eat()) // Has a seat + 2 Forks -> eat
                    .peek(chair -> standUp())
                    .forEach(chair -> mediate()); // Stand up and go to mediation / sleep

            // Sleep
            sleep();
        }
    }

    default void say(final String message) {
        System.out.println(String.format("%1$tH:%1$tM:%1$tS.%1$tL", new Date()) + " [" + getName() + "]: " + message);
    }

    class Builder {
        private static int count = 1;
        private String name = "Philosopher-" + (count++);
        private Table table;
        private long timeSleep = TimeUnit.MILLISECONDS.convert(10, TimeUnit.MILLISECONDS);
        private long timeEat = TimeUnit.MILLISECONDS.convert(1, TimeUnit.MILLISECONDS);
        private long timeMediate = TimeUnit.MILLISECONDS.convert(5, TimeUnit.MILLISECONDS);

        public Builder name(final String name) {
            this.name = name;
            return this;
        }

        public Builder with(final Table table) {
            this.table = table;
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

        public Philosopher create() {
            if (table == null) {
                throw new NullPointerException("Table can not be null. Use new Philosopher.Builder().with(Table).create()");
            }
            return new PhilosopherImpl(name, table, timeSleep, timeEat, timeMediate);
        }
    }
}
