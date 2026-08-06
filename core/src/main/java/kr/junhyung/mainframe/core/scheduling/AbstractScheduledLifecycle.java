package kr.junhyung.mainframe.core.scheduling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractScheduledLifecycle implements SmartLifecycle {

    private static final Logger log = LoggerFactory.getLogger(AbstractScheduledLifecycle.class);

    private final String name;
    private final Duration interval;
    private final AtomicBoolean running = new AtomicBoolean();

    private ThreadPoolTaskScheduler scheduler;
    private ScheduledFuture<?> task;

    protected AbstractScheduledLifecycle(String name, Duration interval) {
        this.name = name;
        this.interval = interval;
    }

    protected abstract void runOnce();

    protected Duration initialDelay() {
        return interval;
    }

    @Override
    public void start() {
        if (!running.compareAndSet(false, true)) {
            return;
        }
        scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);
        scheduler.setThreadNamePrefix(name + "-");
        scheduler.setDaemon(true);
        scheduler.setErrorHandler(error -> log.error("{} failed", name, error));
        scheduler.initialize();
        task = scheduler.scheduleWithFixedDelay(this::runOnce, Instant.now().plus(initialDelay()), interval);
        log.info("Scheduled {} every {}", name, interval);
    }

    @Override
    public void stop() {
        if (!running.compareAndSet(true, false)) {
            return;
        }
        if (task != null) {
            task.cancel(true);
            task = null;
        }
        if (scheduler != null) {
            scheduler.shutdown();
            scheduler = null;
        }
    }

    @Override
    public boolean isRunning() {
        return running.get();
    }
}
