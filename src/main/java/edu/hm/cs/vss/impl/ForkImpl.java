package edu.hm.cs.vss.impl;

import edu.hm.cs.vss.Fork;

import java.util.Optional;

/**
 * Created by Fabio Hellmann on 17.03.2016.
 */
public class ForkImpl implements Fork {
    private static int count = 1;
    private String name = "Fork-" + (count++);
    private String chairName;
    private boolean block;

    public ForkImpl(String chairName) {
        this.chairName = chairName;
    }

    @Override
    public String toString() {
        return name + " from " + chairName;
    }

    @Override
    public boolean isAvailable() {
        return !block;
    }

    @Override
    public synchronized Optional<Fork> block() {
        if(isAvailable()) {
            block = true;
            return Optional.of(this);
        }
        return Optional.empty();
    }

    @Override
    public synchronized void unblock() {
        block = false;
    }
}
