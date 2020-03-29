package club.piggyplanner.services.account.interfaces

import club.piggyplanner.services.account.domain.model.AccountId
import club.piggyplanner.services.account.domain.model.Record
import club.piggyplanner.services.account.domain.model.RecordType
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

data class RecordDTO(val accountId: UUID,
                     val recordType: RecordType,
                     val year: Int,
                     val month: Int,
                     val day: Int,
                     val amount: BigDecimal,
                     val memo: String? = ""){
    fun getAccountId() = AccountId(accountId)
    fun getRecord() = Record(type = recordType, dateTime = LocalDate.of(year, month, day), amount = amount, memo = memo)
}