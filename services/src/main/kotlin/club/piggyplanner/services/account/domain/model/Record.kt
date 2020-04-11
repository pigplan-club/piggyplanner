package club.piggyplanner.services.account.domain.model

import club.piggyplanner.services.account.domain.operations.RecordModified
import org.axonframework.eventsourcing.EventSourcingHandler
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

    @EventSourcingHandler
    fun on(event: RecordModified) {
        this.type = event.record.type
        this.date = event.record.date
        this.amount = event.record.amount
        this.memo = event.record.memo
    }
}