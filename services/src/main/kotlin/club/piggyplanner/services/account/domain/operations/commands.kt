package club.piggyplanner.services.account.domain.operations

import club.piggyplanner.services.account.domain.model.*
import club.piggyplanner.services.account.domain.services.CategoryServices.Companion.getDefaultCategories
import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

data class CreateDefaultAccount(@TargetAggregateIdentifier val saverId: SaverId) {
    val accountId: AccountId = AccountId(UUID.randomUUID())
    val records: List<Record> = emptyList()
    val categories: List<Category> = getDefaultCategories()
    val recordsQuotaByMonth = DEFAULT_RECORDS_QUOTA_BY_MONTH
    val categoriesQuota = DEFAULT_CATEGORIES_QUOTA
    val categoryItemsQuota = DEFAULT_CATEGORY_ITEMS_QUOTA

    companion object {
        const val DEFAULT_RECORDS_QUOTA_BY_MONTH = 150
        const val DEFAULT_CATEGORIES_QUOTA = 10
        const val DEFAULT_CATEGORY_ITEMS_QUOTA = 80
    }
}

data class CreateCategory(@TargetAggregateIdentifier val accountId: AccountId,
                          val categoryId: CategoryId,
                          val name: String)

data class CreateCategoryItem(@TargetAggregateIdentifier val accountId: AccountId,
                              val categoryId: CategoryId,
                              val categoryItemId: CategoryItemId,
                              val name: String)

data class CreateRecord(@TargetAggregateIdentifier val accountId: AccountId,
                        val recordId: RecordId,
                        val recordType: RecordType,
                        val categoryId: CategoryId,
                        val categoryItemId: CategoryItemId,
                        val date: LocalDate,
                        val amount: BigDecimal,
                        val memo: String? = "")

data class ModifyRecord(@TargetAggregateIdentifier val accountId: AccountId,
                        val recordId: RecordId,
                        val recordType: RecordType,
                        val categoryId: CategoryId,
                        val categoryItemId: CategoryItemId,
                        val date: LocalDate,
                        val amount: BigDecimal,
                        val memo: String? = "")

