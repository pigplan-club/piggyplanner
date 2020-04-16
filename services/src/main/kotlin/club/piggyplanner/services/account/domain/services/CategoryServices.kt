package club.piggyplanner.services.account.domain.services

import club.piggyplanner.services.account.domain.model.Category
import club.piggyplanner.services.account.domain.model.CategoryId
import club.piggyplanner.services.account.domain.model.CategoryItem
import club.piggyplanner.services.account.domain.model.CategoryItemId
import club.piggyplanner.services.account.domain.services.CategoryServices.DefaultCategoriesItems.*
import java.util.*


internal class CategoryServices {

    enum class DefaultCategoriesItems {
        ELECTRICITY,
        WATER,
        GAS,
        BREAKFAST,
        LUNCH,
        DINNER
    }

    enum class DefaultCategories(val categoryItems: List<DefaultCategoriesItems>) {
        UTILITY(mutableListOf(ELECTRICITY, WATER, GAS)),
        FOOD(mutableListOf(BREAKFAST, LUNCH, DINNER))

    }

    companion object {
        fun getDefaultCategories(): List<Category> =
                DefaultCategories.values().map { it -> createDefaultCategory(it) }


        private fun createDefaultCategory(defaultCategory: DefaultCategories): Category {
            val category = Category(CategoryId(UUID.randomUUID()), defaultCategory.name)
            defaultCategory.categoryItems.forEach { item ->
                category.addCategoryItem(
                        CategoryItem(
                                CategoryItemId(UUID.randomUUID()),
                                item.name
                        ))
            }

            return category
        }
    }
}