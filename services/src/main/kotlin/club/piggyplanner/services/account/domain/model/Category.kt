package club.piggyplanner.services.account.domain.model

import club.piggyplanner.services.account.domain.operations.CategoryItemCreated
import club.piggyplanner.services.common.domain.model.Entity
import club.piggyplanner.services.common.domain.model.EntityState
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.EntityId

class Category(@EntityId val categoryId: CategoryId) : Entity() {

    lateinit var name: String private set
    var categoryItems = mutableSetOf<CategoryItem>()
        private set

    constructor(categoryId: CategoryId, name: String) : this(categoryId) {
        this.name = name
    }

    @EventSourcingHandler
    fun on(event: CategoryItemCreated) {
        this.categoryItems.add(event.categoryItem)
    }

    fun addCategoryItem(categoryItem: CategoryItem) {
        this.categoryItems.add(categoryItem)
    }

    fun wasExceededQuota(categoryItemsQuota: Int) =
            this.categoryItems.filter { it.state == EntityState.ENABLED }.size >= categoryItemsQuota

    fun getCategoryItem(categoryItemIdToFind: CategoryItemId) =
            categoryItems.find { categoryItem -> categoryItem.categoryItemId == categoryItemIdToFind }

    fun containsCategoryItem(categoryItemToFind: CategoryItem) =
            categoryItems.contains(categoryItemToFind)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Category

        if (categoryId != other.categoryId) return false
        if (name.toLowerCase() != other.name.toLowerCase()) return false

        return true
    }

    override fun hashCode(): Int {
        var result = categoryId.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }
}