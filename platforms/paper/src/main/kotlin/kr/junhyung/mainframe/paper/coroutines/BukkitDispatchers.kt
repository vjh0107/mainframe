package kr.junhyung.mainframe.paper.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import org.jetbrains.annotations.ApiStatus.NonExtendable

/**
 * Provides several [CoroutineDispatcher]s for the Bukkit platform.
 */
@NonExtendable
public interface BukkitDispatchers {

    public fun getMainDispatcher(): CoroutineDispatcher

    public fun getAsyncDispatcher(): CoroutineDispatcher

}