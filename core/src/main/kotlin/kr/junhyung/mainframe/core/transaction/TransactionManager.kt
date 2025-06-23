package kr.junhyung.mainframe.core.transaction

import kotlin.coroutines.CoroutineContext

public interface TransactionManager {

    public suspend fun <T> transactional(
        coroutineContext: CoroutineContext? = null,
        transactionIsolation: Int? = null,
        readOnly: Boolean? = null,
        block: suspend Transaction.() -> T
    ): T

}