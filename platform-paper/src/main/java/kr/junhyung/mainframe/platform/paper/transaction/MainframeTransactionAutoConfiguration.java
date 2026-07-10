package kr.junhyung.mainframe.platform.paper.transaction;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.TransactionExecutionListener;

@AutoConfiguration
@ConditionalOnClass(TransactionExecutionListener.class)
public class MainframeTransactionAutoConfiguration {

    @Bean
    static PrimaryThreadTransactionExecutionListener primaryThreadTransactionExecutionListener() {
        return new PrimaryThreadTransactionExecutionListener();
    }

}
