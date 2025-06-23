package kr.junhyung.mainframe.exposed.transaction

import kr.junhyung.mainframe.core.transaction.Transaction
import kr.junhyung.mainframe.core.transaction.TransactionManager
import org.jetbrains.exposed.sql.Transaction as ExposedTx
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import kotlin.coroutines.CoroutineContext

internal class ExposedTransactionManager(private val database: Database) : TransactionManager {

    override suspend fun <T> transactional(
        coroutineContext: CoroutineContext?,
        transactionIsolation: Int?,
        readOnly: Boolean?,
        block: suspend Transaction.() -> T
    ): T {
        val statement: suspend (ExposedTx) -> T = { transaction ->
            block.invoke(ExposedTransaction(transaction))
        }
        return newSuspendedTransaction(
            context = coroutineContext,
            db = database,
            transactionIsolation = transactionIsolation,
            readOnly = readOnly,
            statement = statement
        )
    }

}