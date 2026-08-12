package kr.junhyung.mainframe.platform.paper.transaction;

import org.bukkit.Bukkit;
import org.jspecify.annotations.NonNull;
import org.springframework.transaction.TransactionExecution;
import org.springframework.transaction.TransactionExecutionListener;

class PrimaryThreadTransactionExecutionListener implements TransactionExecutionListener {

    @Override
    public void beforeBegin(@NonNull TransactionExecution transaction) {
        if (Bukkit.isPrimaryThread() && Bukkit.getCurrentTick() > 0) {
            throw new IllegalStateException(
                    "Transactions must not be started on the Bukkit primary thread. "
                            + "Move the work off the main thread via @Async or a dedicated executor.");
        }
    }

}
