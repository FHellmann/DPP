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
     * Get the fork, which lay at the right side.
     *
     * @param chair to get the forks from.
     * @return the forks.
     */
    Optional<Fork> getForkAtChair(final Chair chair, final Philosopher philosopher);

    /**
     * Set the chair blocked, so no other philosopher can sit down on this chair.
     *
     * @param chair       to block.
     * @param philosopher who sit on the chair.
     */
    void blockChair(final Chair chair, final Philosopher philosopher);

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
    void blockFork(final Fork fork, final Philosopher philosopher);

    /**
     * Set the fork free.
     *
     * @param philosopher who got this fork till now.
     */
    void unblockForks(final Philosopher philosopher);

    /**
     * Set the table "Master".
     *
     * @param tableManager to set as table "Master".
     */
    void setTableManager(final TableManager tableManager);

    /**
     * Get the table "Master".
     *
     * @return the table "Master".
     */
    Optional<TableManager> getTableManager();
}
