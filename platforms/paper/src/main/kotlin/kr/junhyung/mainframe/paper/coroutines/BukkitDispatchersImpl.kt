package kr.junhyung.mainframe.paper.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import java.util.concurrent.Executor

internal class BukkitDispatchersImpl(
    private val plugin: Plugin
) : BukkitDispatchers {

    @get:JvmName("mainDispatcher0")
    private val mainDispatcher by lazy {
        val executor = Bukkit.getScheduler().getMainThreadExecutor(plugin)
        executor.asCoroutineDispatcher()
    }

    @get:JvmName("asyncDispatcher0")
    private val asyncDispatcher by lazy {
        val executor = Executor { runnable ->
            Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable)
        }
        executor.asCoroutineDispatcher()
    }

    override fun getMainDispatcher(): CoroutineDispatcher {
        return mainDispatcher
    }

    override fun getAsyncDispatcher(): CoroutineDispatcher {
        return asyncDispatcher
    }

}