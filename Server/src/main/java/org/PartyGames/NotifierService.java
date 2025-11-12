package org.PartyGames;


import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/*
Please do stopNotification(), otherwise...
Java - No Destructor
 */
public class NotifierService {
    private final ScheduledExecutorService executor;
    private ScheduledFuture<?> task;

    public NotifierService() {
        executor = Executors.newSingleThreadScheduledExecutor();
        task = null;
    }
    public void addNotification(Runnable notification, int times_per_second) {
        if (task != null) {
            throw new IllegalThreadStateException("Notifier service instance only accepts a single task!");
        } else {
            final long PERIOD_NANO = 1_000_000_000L / times_per_second;
            task = executor.scheduleAtFixedRate(notification, 0, PERIOD_NANO, TimeUnit.NANOSECONDS);
        }
    }
    public void shutdown() {
        if (task != null) {
            task.cancel(true);
        }
    }
}
