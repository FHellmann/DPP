package edu.hm.cs.vss;

import java.util.Optional;
import java.util.stream.Stream;

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

    void addChair(final Chair chair);

    Optional<Chair> getChair(final Philosopher philosopher) throws InterruptedException;

    /**
     * Get the next free chair or null. If a free chair was found, this chair will be automatically blocked.
     *
     * @param philosopher who wants a seat.
     * @return the next free chair or null.
     */
    Stream<Chair> getFreeChairs(final Philosopher philosopher);

    /**
     * Get the neighbour chair of another chair. (If there is only one chair, then the same chair will be returned)
     *
     * @param chair to get the neighbour from.
     * @return the neighbour chair.
     */
    Chair getNeighbourChair(final Chair chair);

    void setTableMaster(final TableMaster tableMaster);

    Optional<TableMaster> getTableMaster();
}
