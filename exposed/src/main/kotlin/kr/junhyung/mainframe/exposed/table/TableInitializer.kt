package kr.junhyung.mainframe.exposed.table

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner

internal class TableInitializer(
    private val database: Database,
    private val tables: ObjectProvider<Table>
) : ApplicationRunner {

    override fun run(args: ApplicationArguments) {
        val nonProxiedTables = tables.map { table ->
            table::class.objectInstance ?: error("Table ${table::class.simpleName} is not a singleton")
        }
        transaction(database) {
            SchemaUtils.create(*nonProxiedTables.toTypedArray())
        }
    }

}