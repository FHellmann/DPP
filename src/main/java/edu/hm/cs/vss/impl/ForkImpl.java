package edu.hm.cs.vss.impl;

import edu.hm.cs.vss.Fork;
import edu.hm.cs.vss.Philosopher;

import java.util.Optional;

/**
 * Created by Fabio Hellmann on 17.03.2016.
 */
public class ForkImpl implements Fork {
    private Philosopher philosopher;

    @Override
    public Optional<Philosopher> getPhilosoph() {
        return Optional.ofNullable(philosopher);
    }

    @Override
    public void pick(Philosopher philosopher) {
        this.philosopher = philosopher;
        this.philosopher.pickedFork();
    }

    @Override
    public void release() {
        this.philosopher.releaseForks();
    }
}
