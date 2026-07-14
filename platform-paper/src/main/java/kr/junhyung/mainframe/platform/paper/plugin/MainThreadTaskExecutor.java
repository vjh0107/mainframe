package kr.junhyung.mainframe.platform.paper.plugin;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jspecify.annotations.NonNull;
import org.springframework.core.task.AsyncTaskExecutor;

public class MainThreadTaskExecutor implements AsyncTaskExecutor {

    private final Plugin plugin;

    public MainThreadTaskExecutor(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(@NonNull Runnable task) {
        Bukkit.getScheduler().runTask(plugin, task);
    }

}
