package edu.hm.cs.vss;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Fabio Hellmann on 17.03.2016.
 */
public interface Chair {
    /**
     * Get a fork related to this seat.
     *
     * @return the fork.
     */
    Fork getFork();

    /**
     * @return <code>true</code> if the chair is available.
     */
    boolean isAvailable();

    /**
     * @return the count of already waiting philosophers.
     */
    int queuedPhilosopherCount();

    void sitDown(Philosopher phil) throws InterruptedException;

    /**
     * Set the chair available again.
     */
    void unblock();

    class Builder {
        private static int count = 1;

        public Chair create() {
            return new Chair() {
                private final String name = "Chair-" + (count++);
                private final Fork fork = new Fork.Builder().withChair(name).create();
                private final AtomicBoolean block = new AtomicBoolean(false);
                private final Semaphore sema = new Semaphore(255);

                @Override
                public Fork getFork() {
                    return fork;
                }

                @Override
                public boolean isAvailable() {
                    return !block.get();
                }

                @Override
                public int queuedPhilosopherCount() {
                    return sema.getQueueLength();
                }

                @Override
                public void sitDown(Philosopher phil) throws InterruptedException {
                    sema.acquire();
                    block.set(true);
                }

                @Override
                public void unblock() {
                    sema.release();
                    block.set(false);
                }

                @Override
                public String toString() {
                    return name;
                }
            };
        }
    }
}
