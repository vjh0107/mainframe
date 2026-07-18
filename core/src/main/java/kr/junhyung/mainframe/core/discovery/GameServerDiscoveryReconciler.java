package kr.junhyung.mainframe.core.discovery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class GameServerDiscoveryReconciler implements SmartLifecycle {

    private static final Logger log = LoggerFactory.getLogger(GameServerDiscoveryReconciler.class);

    private final String name;
    private final Duration interval;
    private final Runnable task;

    private ScheduledExecutorService executor;
    private volatile boolean running;

    public GameServerDiscoveryReconciler(String name, Duration interval, Runnable task) {
        this.name = name;
        this.interval = interval;
        this.task = task;
    }

    @Override
    public synchronized void start() {
        if (running) {
            return;
        }
        executor = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, name);
            thread.setDaemon(true);
            return thread;
        });
        long delay = interval.toMillis();
        executor.scheduleWithFixedDelay(this::run, delay, delay, TimeUnit.MILLISECONDS);
        running = true;
    }

    @Override
    public synchronized void stop() {
        running = false;
        if (executor != null) {
            executor.shutdownNow();
            executor = null;
        }
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    private void run() {
        try {
            task.run();
        } catch (Exception exception) {
            log.warn("Resync '{}' failed", name, exception);
        }
    }
}
