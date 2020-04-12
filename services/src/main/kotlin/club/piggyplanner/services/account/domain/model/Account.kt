package club.piggyplanner.services.account.domain.model

import club.piggyplanner.services.account.domain.operations.*
import club.piggyplanner.services.common.domain.model.Entity
import club.piggyplanner.services.common.domain.model.EntityState
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.modelling.command.AggregateMember
import org.axonframework.spring.stereotype.Aggregate
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

@Aggregate(snapshotTriggerDefinition = "accountSnapshotTriggerDefinition")
internal class Account() : Entity() {

    @AggregateIdentifier
    private lateinit var accountId: AccountId
    private lateinit var saverId: SaverId
    private lateinit var name: String

    private var recordsQuotaByMonth: Int = -1
    private var categoriesQuota: Int = -1
    private var categoryItemsQuota: Int = -1

    @AggregateMember
    private val records = mutableListOf<Record>()

    @AggregateMember
    private val categories = mutableListOf<Category>()

    @CommandHandler
    constructor(command: CreateDefaultAccount) : this() {
        AggregateLifecycle.apply(DefaultAccountCreated(
                command.accountId,
                command.saverId,
                DEFAULT_ACCOUNT_NAME,
                command.recordsQuotaByMonth,
                command.categoriesQuota,
                command.categoryItemsQuota)
        )
        command.records.forEach { AggregateLifecycle.apply(RecordCreated(command.accountId, it)) }
        command.categories.forEach { AggregateLifecycle.apply(CategoryCreated(command.accountId, it)) }
    }

    @CommandHandler
    fun handle(command: CreateCategory): Boolean {
        if (categories.find { category -> category.categoryId == command.categoryId } != null)
            throw CategoryAlreadyAddedException()

        if (categories.filter { it.state == EntityState.ENABLED }.size >= this.categoriesQuota)
            throw CategoriesQuotaExceededException()

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

        if (category.wasExceededQuota(this.categoryItemsQuota))
            throw CategoryItemsQuotaExceededException()

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

        if (numberRecordsForSelectedMonth(command.date) >= recordsQuotaByMonth)
            throw RecordsQuotaExceededException()

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
        this.recordsQuotaByMonth = event.recordsQuotaByMonth
        this.categoriesQuota = event.categoriesQuota
        this.categoryItemsQuota = event.categoryItemsQuota
    }

    @EventSourcingHandler
    fun on(event: RecordCreated) {
        this.records.add(event.record)
    }

    @EventSourcingHandler
    fun on(event: CategoryCreated) {
        this.categories.add(event.category)
    }

    private fun numberRecordsForSelectedMonth(date: LocalDate): Int {
        val firstDayOftheMonth = date.minusDays(date.dayOfMonth - 1.toLong())
        val lastDayOftheMonth = date.minusDays(date.dayOfMonth.toLong()).plusMonths(1)

        return records
                .filter { it.state == EntityState.ENABLED }
                .filter { it.date >= firstDayOftheMonth && it.date <= lastDayOftheMonth }
                .size
    }

    companion object {
        const val DEFAULT_ACCOUNT_NAME = "Personal"
    }
}

data class AccountId(val id: UUID)
