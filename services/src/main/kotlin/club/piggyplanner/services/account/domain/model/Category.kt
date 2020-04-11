package club.piggyplanner.services.account.domain.model

import club.piggyplanner.services.account.domain.operations.CategoryItemCreated
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.EntityId
import java.util.*

data class CategoryId(val id: UUID)

class Category(@EntityId val categoryId: CategoryId,
               var name: String) {
    var categoryItems = mutableListOf<CategoryItem>()

    fun getCategoryItem(categoryItemIdToFind: CategoryItemId) =
            categoryItems.find { categoryItem -> categoryItem.categoryItemId == categoryItemIdToFind }

    @EventSourcingHandler
    fun on(event: CategoryItemCreated) {
        this.categoryItems.add(event.categoryItem)
    }
}