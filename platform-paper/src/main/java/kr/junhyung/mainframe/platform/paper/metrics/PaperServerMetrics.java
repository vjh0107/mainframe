package kr.junhyung.mainframe.platform.paper.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.TimeGauge;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.bukkit.Bukkit;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.TimeUnit;

public class PaperServerMetrics implements MeterBinder {

    @Override
    public void bindTo(@NonNull MeterRegistry registry) {
        Gauge.builder("minecraft.tps", () -> Bukkit.getTPS()[0]).tag("window", "1m")
                .description("Server ticks per second, 1-minute average").register(registry);
        Gauge.builder("minecraft.tps", () -> Bukkit.getTPS()[1]).tag("window", "5m")
                .description("Server ticks per second, 5-minute average").register(registry);
        Gauge.builder("minecraft.tps", () -> Bukkit.getTPS()[2]).tag("window", "15m")
                .description("Server ticks per second, 15-minute average").register(registry);
        TimeGauge.builder("minecraft.mspt", Bukkit::getAverageTickTime, TimeUnit.MILLISECONDS)
                .description("Mean time to process a server tick").register(registry);
        Gauge.builder("minecraft.players.online", () -> Bukkit.getOnlinePlayers().size())
                .description("Players currently connected").register(registry);
        Gauge.builder("minecraft.players.max", Bukkit::getMaxPlayers)
                .description("Maximum player slots").register(registry);
    }

}
