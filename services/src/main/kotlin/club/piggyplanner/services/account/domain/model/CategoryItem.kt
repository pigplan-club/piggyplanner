package club.piggyplanner.services.account.domain.model

import java.util.*

data class CategoryItemId(val id: UUID)

data class CategoryItem(val id: CategoryItemId,
                        val name: String,
                        val category: Category)
