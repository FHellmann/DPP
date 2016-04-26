package edu.hm.cs.vss;

import edu.hm.cs.vss.impl.TableImpl;

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

    /**
     * Add a chair to the table.
     *
     * @param chair to add.
     */
    void addChair(final Chair chair);

    /**
     * Gets a list of chairs, but only if the philosopher is allowed to sit at the table.
     * @param philosopher The philosopher who wants the chair.
     * @return A stream of chairs, or an empty one.
     */
    Stream<Chair> getChairs(Philosopher philosopher);

    /**
     * Get the neighbour chair of another chair. (If there is only one chair, then the same chair will be returned)
     *
     * @param chair to get the neighbour from.
     * @return the neighbour chair.
     */
    Chair getNeighbourChair(final Chair chair);

    /**
     * Set the table master for this table.
     *
     * @param tableMaster to set.
     */
    void setTableMaster(final TableMaster tableMaster);

    /**
     * Get the table master - never be <code>null</code>.
     *
     * @return the table master.
     */
    TableMaster getTableMaster();

    class Builder {
        private int amountChairs;
        private TableMaster tableMaster;

        public Builder withChairCount(final int amountOfChairs) {
            amountChairs = amountOfChairs;
            return this;
        }

        public Builder withTableMaster(TableMaster tableMaster) {
            this.tableMaster = tableMaster;
            return this;
        }

        public Table create() {
            final TableImpl table = new TableImpl(amountChairs);
            table.setTableMaster(tableMaster);
            return table;
        }
    }
}
