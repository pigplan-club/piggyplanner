package club.piggyplanner.services.account.domain.projections

import club.piggyplanner.services.account.domain.model.CategoryItem
import club.piggyplanner.services.account.domain.operations.CategoryCreated
import club.piggyplanner.services.account.domain.operations.CategoryItemCreated
import club.piggyplanner.services.account.domain.operations.DefaultAccountCreated
import club.piggyplanner.services.account.domain.operations.RecordCreated
import club.piggyplanner.services.account.infrastructure.repository.*
import org.axonframework.eventhandling.EventHandler
import org.springframework.stereotype.Component

@Component
class AccountProjector(private val accountStore: AccountStore,
                       private val categoryStore: CategoryStore,
                       private val categoryItemStore: CategoryItemStore,
                       private val recordStore: RecordStore) {

    @EventHandler
    fun on(event: DefaultAccountCreated) {
        val accountProjection = AccountProjection(
                event.accountId.id,
                event.accountName,
                event.saverId.id,
                listOf()
        )
        accountStore.save(accountProjection)
    }

    @EventHandler
    fun on(event: CategoryCreated) {
        val categoryProjection = CategoryProjection(
                event.category.categoryId.id,
                event.accountId.id,
                event.category.name,
                toCategoryItemProjections(event.category.categoryItems)
        )
        categoryStore.save(categoryProjection)
    }

    @EventHandler
    fun on(event: CategoryItemCreated) {
        val categoryItemProjection = CategoryItemProjection(
                event.categoryItem.categoryItemId.id,
                event.categoryItem.name
        )
        categoryItemStore.save(categoryItemProjection)
    }

    @EventHandler
    fun on(event: RecordCreated) {
        val recordProjection = RecordProjection(
                event.record.recordId.id,
                event.accountId.id,
                event.record.type,
                event.categoryId.id,
                event.record.categoryItem.categoryItemId.id,
                event.record.date,
                event.record.amount.value,
                event.record.memo
        )
        recordStore.save(recordProjection)
    }

    private fun toCategoryItem(categoryItem: CategoryItem): CategoryItemProjection =
            CategoryItemProjection(
                    categoryItem.categoryItemId.id,
                    categoryItem.name
            )

    private fun toCategoryItemProjections(categoryItems: MutableList<CategoryItem>): List<CategoryItemProjection> =
            categoryItems.map {
                toCategoryItem(it)
            }
}