package edu.hm.cs.vss.impl;

import edu.hm.cs.vss.Chair;
import edu.hm.cs.vss.Fork;
import edu.hm.cs.vss.Philosopher;

import java.util.Optional;

/**
 * Created by Fabio Hellmann on 17.03.2016.
 */
public class ChairImpl implements Chair {
    private Chair neighbour;
    private Fork fork;
    private Philosopher philosopher;

    @Override
    public Chair getNeighbourChair() {
        return neighbour;
    }

    @Override
    public Optional<Fork> getFork() {
        if (fork.getPhilosoph().isPresent()) {
            return Optional.empty();
        }
        return Optional.of(fork); // Only return fork if a philosopher is not available
    }

    @Override
    public Optional<Philosopher> getPhilosopher() {
        return Optional.ofNullable(philosopher);
    }

    @Override
    public void sitDown(Philosopher philosopher) {
        this.philosopher = philosopher;
    }

    @Override
    public void standUp() {
        this.philosopher = null;
    }
}
