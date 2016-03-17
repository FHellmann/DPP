package edu.hm.cs.vss.impl;

import edu.hm.cs.vss.Chair;
import edu.hm.cs.vss.Fork;

/**
 * Created by Fabio Hellmann on 17.03.2016.
 */
public class ChairImpl implements Chair {
    private Fork fork;

    public ChairImpl() {
        this.fork = new ForkImpl();
    }

    @Override
    public Fork getFork() {
        return fork;
    }
}
