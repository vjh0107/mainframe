package kr.junhyung.mainframe.exposed

import kr.junhyung.mainframe.core.transaction.TransactionManager
import kr.junhyung.mainframe.exposed.table.TableInitializer
import kr.junhyung.mainframe.exposed.transaction.ExposedTransactionManager
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Table
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizationAutoConfiguration
import org.springframework.context.annotation.Bean
import javax.sql.DataSource

@ImportAutoConfiguration(
    exclude = [
        TransactionManagerCustomizationAutoConfiguration::class,
        TransactionAutoConfiguration::class,
        DataSourceTransactionManagerAutoConfiguration::class,
    ]
)
@AutoConfiguration
public class ExposedAutoConfiguration {

    @Bean
    public fun database(dataSource: DataSource): Database {
        val database = Database.connect(dataSource)
        return database
    }

    @Bean
    public fun exposedTransactionManager(database: Database): TransactionManager {
        return ExposedTransactionManager(database)
    }

    @Bean
    public fun tableInitializer(
        database: Database,
        tables: ObjectProvider<Table>
    ): ApplicationRunner {
        return TableInitializer(database, tables)
    }

}