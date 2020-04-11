package club.piggyplanner.services.account.domain.model

import club.piggyplanner.services.account.domain.operations.ModifyRecord
import club.piggyplanner.services.account.domain.operations.RecordModified
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.modelling.command.EntityId
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

data class RecordId(val id: UUID)

enum class RecordType {
    INCOME,
    EXPENSE
}

class Record(@EntityId val recordId: RecordId,
             var type: RecordType,
             var categoryItem: CategoryItem,
             var date: LocalDate,
             var amount: BigDecimal,
             var memo: String? = "") {
    init {
        if (amount <= BigDecimal.ZERO) {
            throw AmountInvalidException()
        }
    }

    @CommandHandler
    fun handle(command: ModifyRecord): Boolean {
        if (command.amount <= BigDecimal.ZERO) {
            throw AmountInvalidException()
        }

        AggregateLifecycle.apply(RecordModified(Record(
                recordId = command.recordId,
                type = command.recordType,
                categoryItem = command.categoryItem,
                date = command.date,
                amount = command.amount,
                memo = command.memo)))
        return true
    }

    @EventSourcingHandler
    fun on(event: RecordModified) {
        this.type = event.record.type
        this.date = event.record.date
        this.amount = event.record.amount
        this.memo = event.record.memo
    }
}