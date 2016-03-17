package edu.hm.cs.vss;

import java.util.Optional;

/**
 * Created by Fabio Hellmann on 17.03.2016.
 */
public interface Chair {
    Optional<Fork> getFork();

    Optional<Philosoph> getPhilosoph();
}
