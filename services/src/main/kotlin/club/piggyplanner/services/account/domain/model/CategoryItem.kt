package club.piggyplanner.services.account.domain.model

import java.util.*

data class CategoryItemId(val id: UUID)

class CategoryItem(val categoryItemId: CategoryItemId,
                        val name: String,
                        val category: Category)
