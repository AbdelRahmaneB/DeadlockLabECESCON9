package eu.javaspecialists.deadlock.lab2;

import java.util.concurrent.*;

/**
 * Our philosopher always first locks left, then right.  If all of the thinkers
 * sit in a circle and their threads call "drink()" at the same time, then they
 * will end up with a deadlock.
 * <p>
 * Instead of calling lock() on the two Krasi instances, we want to use
 * tryLock().  The idea is something like:
 * <p>
 * <pre>
 *     while(true) {
 *         try lock left
 *         if successful, try lock right
 *           if successful drink and unlock right
 *           unlock left
 *     }
 * </pre>
 * <p>
 * Be careful to not cause a livelock.
 *
 * @author Heinz Kabutz
 */
public class Thinker implements Callable<ThinkerStatus> {
    private final int id;
    private final Krasi left, right;
    private int drinks = 0;

    public Thinker(int id, Krasi left, Krasi right) {
        this.id = id;
        this.left = left;
        this.right = right;
    }

    public ThinkerStatus call() throws Exception {
        for (int i = 0; i < 1000; i++) {
            drink();
            think();
        }
        return drinks == 1000 ? ThinkerStatus.HAPPY_THINKER :
            ThinkerStatus.UNHAPPY_THINKER;
    }

    public void drink() {
        left.lock();
        try {
            right.lock();
            try {
                drinking();
            } finally {
                right.unlock();
            }
        } finally {
            left.unlock();
        }
    }

    private void drinking() {
        if (!left.isHeldByCurrentThread() || !right.isHeldByCurrentThread()) {
            throw new IllegalMonitorStateException("Not holding both locks");
        }
        System.out.printf("(%d) Drinking%n", id);
        drinks++;
    }

    public void think() {
        System.out.printf("(%d) Thinking%n", id);
    }
}
