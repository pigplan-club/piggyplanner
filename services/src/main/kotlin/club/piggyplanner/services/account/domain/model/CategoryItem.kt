package club.piggyplanner.services.account.domain.model

import club.piggyplanner.services.common.domain.model.Entity
import java.util.*

data class CategoryItemId(val id: UUID)

class CategoryItem(val categoryItemId: CategoryItemId,
                   val name: String,
                   val category: Category) : Entity()
