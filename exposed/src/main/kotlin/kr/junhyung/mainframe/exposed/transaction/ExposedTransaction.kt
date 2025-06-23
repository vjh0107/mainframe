package kr.junhyung.mainframe.exposed.transaction

import kr.junhyung.mainframe.core.transaction.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.withSuspendTransaction
import org.jetbrains.exposed.sql.Transaction as ExposedTx
import kotlin.coroutines.CoroutineContext

internal class ExposedTransaction(private val transaction: ExposedTx) : Transaction {

    override fun isReadOnly(): Boolean {
        return transaction.readOnly
    }

    override fun close() {
        transaction.close()
    }

    override fun commit() {
        transaction.commit()
    }

    override fun rollback() {
        transaction.rollback()
    }

    override suspend fun <T> withTransaction(coroutineContext: CoroutineContext?, statement: suspend Transaction.() -> T) {
        val statement: suspend (ExposedTx) -> T = { transaction ->
            statement.invoke(ExposedTransaction(transaction))
        }
        transaction.withSuspendTransaction(coroutineContext, statement)
    }

}