package club.piggyplanner.services.account.domain.model

import java.util.*

data class CategoryId(val id: UUID)

class Category(val categoryId: CategoryId,
               var name: String)