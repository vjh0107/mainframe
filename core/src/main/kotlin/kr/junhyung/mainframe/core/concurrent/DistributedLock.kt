package kr.junhyung.mainframe.core.concurrent

import kotlin.time.Duration

/**
 * A standard interface for a distributed lock.
 */
public interface DistributedLock {

    public suspend fun <T> withLock(
        key: String,
        waitTime: Duration,
        leaseTime: Duration,
        action: suspend () -> T
    ): T

    public suspend fun tryLock(
        key: String,
        waitTime: Duration,
        leaseTime: Duration
    ): Boolean

    public suspend fun unlock(key: String)

    public suspend fun isLocked(key: String): Boolean

}