package club.piggyplanner.services.account.domain.model

import club.piggyplanner.services.common.domain.model.Entity

class CategoryItem(val categoryItemId: CategoryItemId) : Entity() {

    lateinit var name: String

    constructor(categoryItemId: CategoryItemId, name: String) : this(categoryItemId) {
        this.name = name
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CategoryItem

        if (categoryItemId != other.categoryItemId) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = categoryItemId.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }
}
