package club.piggyplanner.services.account.presentation

import club.piggyplanner.services.account.domain.model.*
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

data class RecordDTO(val accountId: UUID,
                     val recordId: UUID,
                     val recordType: RecordType,
                     val categoryId: UUID,
                     val categoryItemId: UUID,
                     val year: Int,
                     val month: Int,
                     val day: Int,
                     val amount: BigDecimal,
                     val memo: String? = "") {
    fun getAccountId() = AccountId(accountId)
    fun getRecordId() = RecordId(recordId)
    fun getCategoryId() = CategoryId(categoryId)
    fun getCategoryItemId() = CategoryItemId(categoryItemId)
    fun getDate(): LocalDate = LocalDate.of(year, month, day)
}