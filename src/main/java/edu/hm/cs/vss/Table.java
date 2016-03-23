package edu.hm.cs.vss;

import java.util.Optional;

/**
 * Created by Fabio Hellmann on 17.03.2016.
 */
public interface Table {
    /**
     * For initialization of the chair count.
     *
     * @param chairCount to init.
     */
    void addChairs(final int chairCount);

    /**
     * Get the next free chair or null. If a free chair was found, this chair will be automatically blocked.
     *
     * @param philosopher who want a seat.
     * @return the next free chair or null.
     */
    Optional<Chair> getFreeChair(final Philosopher philosopher);

    /**
     * Get the neighbour chair of another chair. (If there is only one chair, then the same chair will be returned)
     *
     * @param chair to get the neighbour from.
     * @return the neighbour chair.
     */
    Chair getNeighbourChair(final Chair chair);

    /**
     * Check whether the fork is available or not.
     *
     * @param fork to check.
     * @return <code>true</code> if the fork is available.
     */
    boolean isForkFree(final Fork fork);

    /**
     * Set the chair blocked, so no other philosopher can sit down on this chair.
     *
     * @param chair       to block.
     * @param philosopher who sit on the chair.
     */
    Optional<Chair> blockChair(final Chair chair, final Philosopher philosopher);

    /**
     * Set the chair free.
     *
     * @param philosopher who sit on the chair till now.
     */
    void unblockChair(final Philosopher philosopher);

    /**
     * Set the fork blocked, so no other philosopher can pick up this fork.
     *
     * @param fork        to block.
     * @param philosopher who got this fork.
     */
    Optional<Fork> blockFork(final Fork fork, final Philosopher philosopher);

    /**
     * Set the fork free.
     *
     * @param philosopher who got this fork till now.
     */
    void unblockForks(final Philosopher philosopher);
}
