package club.piggyplanner.services.account.domain.model

import club.piggyplanner.services.account.domain.operations.RecordModified
import club.piggyplanner.services.common.domain.model.Entity
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.EntityId
import java.time.LocalDate

class Record(@EntityId val recordId: RecordId) : Entity() {
    lateinit var type: RecordType private set
    lateinit var categoryId: CategoryId private set
    lateinit var categoryItemId: CategoryItemId private set
    lateinit var date: LocalDate private set
    lateinit var amount: RecordAmount private set
    var memo: String? = ""
        private set

    constructor (recordId: RecordId,
                 type: RecordType,
                 categoryId: CategoryId,
                 categoryItemId: CategoryItemId,
                 date: LocalDate,
                 amount: RecordAmount,
                 memo: String? = "") : this(recordId) {
        this.type = type
        this.categoryId = categoryId
        this.categoryItemId = categoryItemId
        this.date = date
        this.amount = amount
        this.memo = memo
    }

    @EventSourcingHandler
    fun on(event: RecordModified) {
        this.type = event.record.type
        this.categoryId = event.record.categoryId
        this.categoryItemId = event.record.categoryItemId
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