package edu.hm.cs.vss;

/**
 * Created by Fabio Hellmann on 30.03.2016.
 */
public interface TableMaster {
    int MAX_DEVIATION = 10;

    void register(final Philosopher philosopher);

    void unregister(final Philosopher philosopher);

    boolean isAllowedToTakeSeat(final Philosopher philosopher);
}
