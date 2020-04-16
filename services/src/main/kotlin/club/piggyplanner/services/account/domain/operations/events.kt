package club.piggyplanner.services.account.domain.operations

import club.piggyplanner.services.account.domain.model.*
import org.axonframework.serialization.Revision

@Revision("1.0")
data class DefaultAccountCreated(val accountId: AccountId,
                                 val saverId: SaverId,
                                 val accountName: String,
                                 val recordsQuotaByMonth: Int,
                                 val categoriesQuota: Int,
                                 val categoryItemsQuota: Int)

@Revision("1.0")
data class CategoryCreated(val accountId: AccountId,
                           val category: Category)

@Revision("1.0")
data class CategoryItemCreated(val accountId: AccountId,
                               val categoryId: CategoryId,
                               val categoryItem: CategoryItem)

@Revision("1.0")
data class RecordCreated(val accountId: AccountId,
                         val categoryId: CategoryId,
                         val record: Record)

@Revision("1.0")
data class RecordModified(val accountId: AccountId,
                          val record: Record)
