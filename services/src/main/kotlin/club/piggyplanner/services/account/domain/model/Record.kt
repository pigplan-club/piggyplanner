package club.piggyplanner.services.account.domain.model

import club.piggyplanner.services.account.domain.operations.RecordModified
import club.piggyplanner.services.common.domain.model.Entity
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.EntityId
import java.time.LocalDate

class Record(@EntityId val recordId: RecordId,
             var type: RecordType,
             var categoryItem: CategoryItem,
             var date: LocalDate,
             var amount: RecordAmount,
             var memo: String? = ""
) : Entity() {

    @EventSourcingHandler
    fun on(event: RecordModified) {
        this.type = event.record.type
        this.categoryItem = event.record.categoryItem
        this.date = event.record.date
        this.amount = event.record.amount
        this.memo = event.record.memo
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Record

        if (recordId.id != other.recordId.id) return false

        return true
    }

    override fun hashCode(): Int {
        return recordId.hashCode()
    }


}