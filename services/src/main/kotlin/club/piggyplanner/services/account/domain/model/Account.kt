package club.piggyplanner.services.account.domain.model

import club.piggyplanner.services.account.domain.operations.CreateDefaultAccount
import club.piggyplanner.services.account.domain.operations.CreateRecord
import club.piggyplanner.services.account.domain.operations.DefaultAccountCreated
import club.piggyplanner.services.account.domain.operations.RecordCreated
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.modelling.command.AggregateMember
import org.axonframework.spring.stereotype.Aggregate
import java.util.*

@Aggregate(snapshotTriggerDefinition = "accountSnapshotTriggerDefinition")
internal class Account() {

    @AggregateIdentifier
    private lateinit var accountId: AccountId
    private lateinit var saverId: SaverId
    private lateinit var name: String

    @AggregateMember
    private val records = mutableListOf<Record>()

    @CommandHandler
    constructor(command: CreateDefaultAccount) : this() {
        AggregateLifecycle.apply(DefaultAccountCreated(command.accountId, command.saverId, DEFAULT_ACCOUNT_NAME))
        command.records.forEach { AggregateLifecycle.apply(RecordCreated(command.accountId, it)) }
    }

    @CommandHandler
    fun handle(command: CreateRecord): Boolean {
        if (records.find { record -> record.recordId == command.recordId } != null)
            throw RecordAlreadyAddedException()

        AggregateLifecycle.apply(RecordCreated(command.accountId,
                Record(
                        recordId = command.recordId,
                        type = command.recordType,
                        categoryItem = command.categoryItem,
                        date = command.date,
                        amount = command.amount,
                        memo = command.memo)))
        return true
    }

    @EventSourcingHandler
    fun on(event: DefaultAccountCreated) {
        this.accountId = event.accountId
        this.saverId = event.saverId
        this.name = event.accountName
    }

    @EventSourcingHandler
    fun on(event: RecordCreated) {
        this.records.add(event.record)
    }

    companion object {
        const val DEFAULT_ACCOUNT_NAME = "Personal"
    }
}

data class AccountId(val id: UUID)
