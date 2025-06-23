package kr.junhyung.mainframe.core.transaction

import kotlin.coroutines.CoroutineContext

public interface Transaction {

    public fun isReadOnly(): Boolean

    public fun close()

    public fun commit()

    public fun rollback()

    public suspend fun <T> withTransaction(coroutineContext: CoroutineContext? = null, statement: suspend Transaction.() -> T)

}