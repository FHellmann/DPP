package edu.hm.cs.vss;

/**
 * Created by Fabio Hellmann on 17.03.2016.
 */
public interface Chair {
    /**
     * Get a fork related to this seat.
     *
     * @return the fork.
     */
    Fork getFork();

    class Builder {
        private static int count = 1;

        public Chair create() {
            final String name = "Chair-" + (count++);
            final Fork fork = new Fork.Builder().withChair(name).create();
            return new Chair() {
                @Override
                public Fork getFork() {
                    return fork;
                }

                @Override
                public String toString() {
                    return name;
                }
            };
        }
    }
}
