package club.piggyplanner.services.account.domain.model

import club.piggyplanner.services.account.domain.operations.CategoryItemCreated
import club.piggyplanner.services.common.domain.model.Entity
import club.piggyplanner.services.common.domain.model.EntityState
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.EntityId
import java.util.*

data class CategoryId(val id: UUID)

class Category(@EntityId val categoryId: CategoryId,
               var name: String) : Entity() {
    var categoryItems = mutableListOf<CategoryItem>()

    fun getCategoryItem(categoryItemIdToFind: CategoryItemId) =
            categoryItems.find { categoryItem -> categoryItem.categoryItemId == categoryItemIdToFind }

    @EventSourcingHandler
    fun on(event: CategoryItemCreated) {
        this.categoryItems.add(event.categoryItem)
    }

    fun addCategoryItem(categoryItem: CategoryItem) {
        this.categoryItems.add(categoryItem)
    }

    fun exceedQuota(categoryItemsQuota: Int) : Boolean {
        println("categoryItemsQuota = $categoryItemsQuota  size = ${this.categoryItems.size}")
        return this.categoryItems.filter { it.state == EntityState.ENABLED }.size >= categoryItemsQuota
    }

    companion object {
        fun createDefaultCaegories() {

        }
    }
}