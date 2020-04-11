package club.piggyplanner.services.account.domain.model

import club.piggyplanner.services.account.domain.operations.*
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.modelling.command.AggregateMember
import org.axonframework.spring.stereotype.Aggregate
import java.math.BigDecimal
import java.util.*

@Aggregate(snapshotTriggerDefinition = "accountSnapshotTriggerDefinition")
internal class Account() {

    @AggregateIdentifier
    private lateinit var accountId: AccountId
    private lateinit var saverId: SaverId
    private lateinit var name: String

    @AggregateMember
    private val records = mutableListOf<Record>()

    @AggregateMember
    private val categories = mutableListOf<Category>()

    @CommandHandler
    constructor(command: CreateDefaultAccount) : this() {
        AggregateLifecycle.apply(DefaultAccountCreated(command.accountId, command.saverId, DEFAULT_ACCOUNT_NAME))
        command.records.forEach { AggregateLifecycle.apply(RecordCreated(command.accountId, it)) }
        command.categories.forEach { AggregateLifecycle.apply(CategoryCreated(command.accountId, it)) }
    }

    @CommandHandler
    fun handle(command: CreateCategory): Boolean {
        if (categories.find { category -> category.categoryId == command.categoryId } != null)
            throw CategoryAlreadyAddedException()

        AggregateLifecycle.apply(CategoryCreated(command.accountId,
                Category(
                        command.categoryId,
                        command.name
                )))

        return true
    }

    @CommandHandler
    fun handle(command: CreateCategoryItem): Boolean {
        val category = categories.find { category -> category.categoryId == command.categoryId }
                ?: throw CategoryNotFoundException(command.categoryId.id)

        if (category.getCategoryItem(command.categoryItemId) != null)
            throw CategoryItemAlreadyAddedException()

        AggregateLifecycle.apply(CategoryItemCreated(command.accountId,
                CategoryItem(
                        command.categoryItemId,
                        command.name,
                        category
                )))

        return true
    }

    @CommandHandler
    fun handle(command: CreateRecord): Boolean {
        if (records.find { record -> record.recordId == command.recordId } != null)
            throw RecordAlreadyAddedException()

        val category = categories.find { category -> category.categoryId == command.categoryId }
                ?: throw CategoryNotFoundException(command.categoryId.id)
        val categoryItem = category.getCategoryItem(command.categoryItemId)
                ?: throw CategoryItemNotFoundException(command.categoryItemId.id)

        AggregateLifecycle.apply(RecordCreated(command.accountId,
                Record(
                        recordId = command.recordId,
                        type = command.recordType,
                        categoryItem = categoryItem,
                        date = command.date,
                        amount = command.amount,
                        memo = command.memo)))
        return true
    }

    @CommandHandler
    fun handle(command: ModifyRecord): Boolean {
        if (command.amount <= BigDecimal.ZERO) {
            throw AmountInvalidException()
        }

        val category = categories.find { category -> category.categoryId == command.categoryId }
                ?: throw CategoryNotFoundException(command.categoryId.id)
        val categoryItem = category.getCategoryItem(command.categoryItemId)
                ?: throw CategoryItemNotFoundException(command.categoryItemId.id)

        AggregateLifecycle.apply(RecordModified(Record(
                recordId = command.recordId,
                type = command.recordType,
                categoryItem = categoryItem,
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

    @EventSourcingHandler
    fun on(event: CategoryCreated) {
        this.categories.add(event.category)
    }

    companion object {
        const val DEFAULT_ACCOUNT_NAME = "Personal"
    }
}

data class AccountId(val id: UUID)
