package edu.hm.cs.vss;

import java.util.Optional;

/**
 * Created by Fabio Hellmann on 17.03.2016.
 */
public interface Chair {

    Fork getFork();

    boolean isAvailable();

    Optional<Chair> blockIfAvailable();

    void unblock();
}
