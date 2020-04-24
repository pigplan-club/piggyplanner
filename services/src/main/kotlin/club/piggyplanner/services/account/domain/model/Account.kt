package club.piggyplanner.services.account.domain.model

import club.piggyplanner.services.account.domain.operations.*
import club.piggyplanner.services.account.infrastructure.config.AccountConfigProperties
import club.piggyplanner.services.common.domain.model.Entity
import club.piggyplanner.services.common.domain.model.EntityState
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.modelling.command.AggregateMember
import org.axonframework.spring.stereotype.Aggregate
import java.time.LocalDate

@Aggregate(snapshotTriggerDefinition = "accountSnapshotTriggerDefinition")
class Account() : Entity() {

    @AggregateIdentifier
    private lateinit var accountId: AccountId
    private lateinit var userId: UserId
    private lateinit var name: String

    private var recordsQuotaByMonth: Int = -1
    private var categoriesQuota: Int = -1
    private var categoryItemsQuota: Int = -1

    @AggregateMember
    private val records = mutableSetOf<Record>()

    @AggregateMember
    private val categories = mutableSetOf<Category>()

    @CommandHandler
    constructor(command: CreateDefaultAccount, accountConfigProperties: AccountConfigProperties) : this() {
        AggregateLifecycle.apply(DefaultAccountCreated(
                command.accountId,
                command.userId,
                accountConfigProperties.defaultAccountName,
                accountConfigProperties.recordsQuotaByMonth,
                accountConfigProperties.categoriesQuota,
                accountConfigProperties.categoryItemsQuota)
        )

        command.categories.forEach { AggregateLifecycle.apply(CategoryCreated(command.accountId, it)) }
    }

    @CommandHandler
    fun handle(command: CreateCategory): Boolean {
        if (categories.filter { it.state == EntityState.ENABLED }.size >= this.categoriesQuota)
            throw CategoriesQuotaExceededException()

        val newCategory = Category(command.categoryId, command.name)
        if (categories.contains(newCategory))
            throw CategoryAlreadyAddedException()

        AggregateLifecycle.apply(CategoryCreated(command.accountId, newCategory))
        return true
    }

    @CommandHandler
    fun handle(command: CreateCategoryItem): Boolean {
        val category = categories.find { category -> category.categoryId == command.categoryId }
                ?: throw CategoryNotFoundException(command.categoryId.id)

        if (category.wasExceededQuota(this.categoryItemsQuota))
            throw CategoryItemsQuotaExceededException()

        val newCategoryItem = CategoryItem(command.categoryItemId, command.name)
        if (category.containsCategoryItem(newCategoryItem))
            throw CategoryItemAlreadyAddedException()

        AggregateLifecycle.apply(CategoryItemCreated(command.accountId,
                command.categoryId, newCategoryItem))

        return true
    }

    @CommandHandler
    fun handle(command: CreateRecord): Boolean {
        if (numberRecordsForSelectedMonth(command.date) >= recordsQuotaByMonth)
            throw RecordsQuotaExceededException()

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
                        categoryId = category.categoryId,
                        categoryItemId = categoryItem.categoryItemId,
                        date = command.date,
                        amount = RecordAmount(command.amount),
                        memo = command.memo)))
        return true
    }

    @CommandHandler
    fun handle(command: ModifyRecord): Boolean {
        if (records.find { record -> record.recordId == command.recordId } == null)
            throw RecordNotFoundException(command.recordId.id)

        val category = categories.find { category -> category.categoryId == command.categoryId }
                ?: throw CategoryNotFoundException(command.categoryId.id)
        val categoryItem = category.getCategoryItem(command.categoryItemId)
                ?: throw CategoryItemNotFoundException(command.categoryItemId.id)

        AggregateLifecycle.apply(RecordModified(command.accountId,
                Record(
                        recordId = command.recordId,
                        type = command.recordType,
                        categoryId = category.categoryId,
                        categoryItemId = categoryItem.categoryItemId,
                        date = command.date,
                        amount = RecordAmount(command.amount),
                        memo = command.memo)))
        return true
    }

    @CommandHandler
    fun handle(command: DeleteRecord): Boolean {
        if (records.find { record -> record.recordId == command.recordId } == null)
            throw RecordNotFoundException(command.recordId.id)

        AggregateLifecycle.apply(RecordDeleted(command.accountId,
                command.recordId))

        return true
    }

    @EventSourcingHandler
    fun on(event: DefaultAccountCreated) {
        this.accountId = event.accountId
        this.userId = event.userId
        this.name = event.accountName
        this.recordsQuotaByMonth = event.recordsQuotaByMonth
        this.categoriesQuota = event.categoriesQuota
        this.categoryItemsQuota = event.categoryItemsQuota
    }

    @EventSourcingHandler
    fun on(event: CategoryCreated) {
        this.categories.add(event.category)
    }

    @EventSourcingHandler
    fun on(event: RecordCreated) {
        this.records.add(event.record)
    }

    @EventSourcingHandler
    fun on(event: RecordDeleted) {
        val record = this.records.find { record -> record.recordId == event.recordId }
        this.records.remove(record)
    }

    private fun numberRecordsForSelectedMonth(date: LocalDate): Int {
        val firstDayOftheMonth = date.minusDays(date.dayOfMonth - 1.toLong())
        val lastDayOftheMonth = date.minusDays(date.dayOfMonth.toLong()).plusMonths(1)

        return records
                .filter { it.date >= firstDayOftheMonth && it.date <= lastDayOftheMonth }
                .size
    }
}
