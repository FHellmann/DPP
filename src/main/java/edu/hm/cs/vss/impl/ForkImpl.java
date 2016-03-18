package edu.hm.cs.vss.impl;

import edu.hm.cs.vss.Fork;

/**
 * Created by Fabio Hellmann on 17.03.2016.
 */
public class ForkImpl implements Fork {
    private static int count = 1;
    private String name = "Fork-" + (count++);
    private String chairName;

    public ForkImpl(String chairName) {
        this.chairName = chairName;
    }

    @Override
    public String toString() {
        return name + " from " + chairName;
    }
}
