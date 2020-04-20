package club.piggyplanner.services.account.domain.model.utils

import club.piggyplanner.services.account.domain.model.*
import club.piggyplanner.services.account.domain.operations.CategoryCreated
import club.piggyplanner.services.account.domain.operations.CategoryItemCreated
import club.piggyplanner.services.account.domain.operations.CreateRecord
import club.piggyplanner.services.account.domain.operations.DefaultAccountCreated
import club.piggyplanner.services.account.infrastructure.config.AccountConfigProperties
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

class UtilTest {

    companion object {
        private val accountConfigProperties = AccountConfigProperties("Personal", 5, 5, 5)
        private val userId = UUID.randomUUID()
        val accountId: UUID = UUID.randomUUID()
        val category = Category(CategoryId(UUID.randomUUID()), "Utility")
        val categoryItem = CategoryItem(CategoryItemId(UUID.randomUUID()), "Energy")

        fun generateDefaultAccountCreatedEvent(
                newRecordsQuotaByMonth: Int? = accountConfigProperties.recordsQuotaByMonth,
                categoriesQuota: Int? = accountConfigProperties.categoriesQuota,
                categoryItemsQuota: Int? = accountConfigProperties.categoryItemsQuota
        ): DefaultAccountCreated {
            return DefaultAccountCreated(AccountId(accountId), UserId(userId), accountConfigProperties.defaultAccountName,
                    newRecordsQuotaByMonth!!,
                    categoriesQuota!!,
                    categoryItemsQuota!!)
        }

        fun generateRecordCommand(record: Record): CreateRecord {
            return CreateRecord(
                    accountId = AccountId(accountId),
                    recordId = record.recordId,
                    recordType = record.type,
                    categoryId = category.categoryId,
                    categoryItemId = categoryItem.categoryItemId,
                    date = record.date,
                    amount = record.amount.value,
                    memo = record.memo)
        }

        fun generateCategoryItemCreatedEvent() =
                CategoryItemCreated(AccountId(accountId), category.categoryId, categoryItem)

        fun generateCategoryCreatedEvent() =
                CategoryCreated(AccountId(accountId), category)

        fun createRecordForTest(withMemo: Boolean, amount: BigDecimal? = BigDecimal.ONE, date: LocalDate? = LocalDate.now()): Record {
            val recordId = UUID.randomUUID()
            val recordType = RecordType.EXPENSE

            if (withMemo)
                return Record(recordId = RecordId(recordId), type = recordType, categoryId = category.categoryId, categoryItemId = categoryItem.categoryItemId, date = date!!, amount = RecordAmount(amount!!), memo = "test memo")

            return Record(recordId = RecordId(recordId), type = recordType, categoryId = category.categoryId, categoryItemId = categoryItem.categoryItemId, date = date!!, amount = RecordAmount(amount!!))
        }
    }
}