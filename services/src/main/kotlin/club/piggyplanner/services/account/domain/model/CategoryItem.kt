package club.piggyplanner.services.account.domain.model

import club.piggyplanner.services.common.domain.model.Entity

class CategoryItem(val categoryItemId: CategoryItemId,
                   val name: String) : Entity(){

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CategoryItem

        if (categoryItemId.id != other.categoryItemId.id) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = categoryItemId.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }
}
