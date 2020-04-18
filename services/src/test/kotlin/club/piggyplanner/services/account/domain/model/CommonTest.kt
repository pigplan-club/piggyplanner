package club.piggyplanner.services.account.domain.model

import club.piggyplanner.services.account.domain.operations.DefaultAccountCreated
import club.piggyplanner.services.account.infrastructure.config.AccountConfigProperties
import java.util.*

class CommonTest {

    companion object {
        private val accountConfigProperties = AccountConfigProperties("Personal", 1, 2, 2)
        private val userId = UUID.randomUUID()
        val accountId: UUID = UUID.randomUUID()
        val category = Category(CategoryId(UUID.randomUUID()), "Utility")
        val categoryItem = CategoryItem(CategoryItemId(UUID.randomUUID()), "Energy")


        fun generateDefaultAccountCreatedEvent(): DefaultAccountCreated {
            return DefaultAccountCreated(AccountId(accountId), SaverId(userId), accountConfigProperties.defaultAccountName,
                    accountConfigProperties.recordsQuotaByMonth,
                    accountConfigProperties.categoriesQuota,
                    accountConfigProperties.categoryItemsQuota)
        }

    }

}