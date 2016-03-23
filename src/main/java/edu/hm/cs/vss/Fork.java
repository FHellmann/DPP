package edu.hm.cs.vss;

/**
 * Created by Fabio Hellmann on 17.03.2016.
 */
public interface Fork {

    class Pair<R extends Fork, L extends Fork> {
        private final R rightItem;
        private final L leftItem;

        private Pair(R rightItem, L leftItem) {
            this.rightItem = rightItem;
            this.leftItem = leftItem;
        }

        public R getRightItem() {
            return rightItem;
        }

        public L getLeftItem() {
            return leftItem;
        }

        public static <R extends Fork, L extends Fork> Pair<R, L> of(R rightItem, L leftItem) {
            return new Pair<>(rightItem, leftItem);
        }
    }
}
