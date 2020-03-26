package club.piggyplanner.services.account.domain.model

import club.piggyplanner.services.account.domain.operations.CreateDefaultAccount
import club.piggyplanner.services.account.domain.operations.DefaultAccountCreated
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.spring.stereotype.Aggregate

@Suppress("SpringJavaInjectionPointsAutowiringInspection")
@Aggregate
internal class Account {

    @AggregateIdentifier
    private lateinit var accountId: AccountId
    private lateinit var userId: UserId
    private lateinit var name: String

    @CommandHandler
    constructor(command: CreateDefaultAccount) {
        AggregateLifecycle.apply(DefaultAccountCreated(command.accountId, command.userId, DEFAULT_ACCOUNT_NAME))
    }

    @EventSourcingHandler
    fun on(event: DefaultAccountCreated) {
        this.accountId = AccountId(event.accountId)
        this.userId = UserId(event.userId)
        this.name = event.accountName
    }

    companion object {
        const val DEFAULT_ACCOUNT_NAME = "Personal"
    }
}
