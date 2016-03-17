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
    void addChairs(int chairCount);

    /**
     * Get the next free chair or null.
     *
     * @return the next free chair or null.
     */
    Optional<Chair> getFreeChair();

    /**
     * Get the neighbour chair of another chair. (If there is only one chair, then the same chair will be returned)
     *
     * @param chair to get the neighbour from.
     * @return the neighbour chair.
     */
    Chair getNeighbourChair(final Chair chair);

    /**
     * Set the chair blocked, so no other philosopher can sit down on this chair.
     *
     * @param chair       to block.
     * @param philosopher who sit on the chair.
     */
    void blockChair(Chair chair, Philosopher philosopher);

    /**
     * Set the chair free.
     *
     * @param philosopher who sit on the chair till now.
     */
    void unblockChair(Philosopher philosopher);

    /**
     * Set the fork blocked, so no other philosopher can pick up this fork.
     *
     * @param fork        to block.
     * @param philosopher who got this fork.
     */
    void blockFork(Fork fork, Philosopher philosopher);

    /**
     * Set the fork free.
     *
     * @param philosopher who got this fork till now.
     */
    void unblockForks(Philosopher philosopher);

    /**
     * Get the table "Master".
     *
     * @return the table "Master".
     */
    Optional<TableManager> getTableManager();
}
