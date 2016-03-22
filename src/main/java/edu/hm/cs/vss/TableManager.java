package edu.hm.cs.vss;

import java.util.List;
import java.util.OptionalInt;
import java.util.function.Function;

/**
 * Created by Fabio Hellmann on 17.03.2016.
 */
public interface TableManager extends Function<Philosopher, Boolean> {
    int MAX_DEVIATION = 10;

    List<Philosopher> getManagedPhilosophers();

    default Boolean apply(Philosopher philosopher) {
        // Check the meals between all philosophers
        final int minMealCount = getManagedPhilosophers().stream()
                .mapToInt(Philosopher::getMealCount)
                .min()
                .orElse(0);

        if (philosopher.getMealCount() >= minMealCount + MAX_DEVIATION) {
            philosopher.banned();
            return false;
        }

        philosopher.unbanned();
        return true;
    }

    default void register(Philosopher philosopher) {
        if (!getManagedPhilosophers().contains(philosopher)) {
            getManagedPhilosophers().add(philosopher);
        }
    }

    default void unregister(Philosopher philosopher) {
        if (getManagedPhilosophers().contains(philosopher)) {
            getManagedPhilosophers().remove(philosopher);
        }
    }
}
