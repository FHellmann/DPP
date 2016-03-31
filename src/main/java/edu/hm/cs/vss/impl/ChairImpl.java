package edu.hm.cs.vss.impl;

import edu.hm.cs.vss.Chair;
import edu.hm.cs.vss.Fork;

import java.util.Optional;

/**
 * Created by Fabio Hellmann on 17.03.2016.
 */
public class ChairImpl implements Chair {
    private static int count = 1;
    private String name = "Chair-" + (count++);
    private Fork fork;
    private boolean block;

    public ChairImpl() {
        this.fork = new ForkImpl(name);
    }

    @Override
    public Fork getFork() {
        return fork;
    }

    @Override
    public boolean isAvailable() {
        return !block;
    }

    @Override
    public synchronized Optional<Chair> blockIfAvailable() {
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

    @Override
    public String toString() {
        return name;
    }
}
