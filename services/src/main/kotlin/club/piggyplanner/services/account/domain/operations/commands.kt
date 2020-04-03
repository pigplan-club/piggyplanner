package club.piggyplanner.services.account.domain.operations

import club.piggyplanner.services.account.domain.model.*
import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

data class CreateDefaultAccount(val userId: UserId) {
    @TargetAggregateIdentifier
    val accountId: AccountId = AccountId(UUID.randomUUID())
    val records: List<Record> = emptyList()
}

data class CreateRecord(@TargetAggregateIdentifier val accountId: AccountId,
                        val recordId: RecordId,
                        val recordType: RecordType,
                        val date: LocalDate,
                        val amount: BigDecimal,
                        val memo: String? = "")

data class ModifyRecord(@TargetAggregateIdentifier val accountId: AccountId,
                        val recordId: RecordId,
                        val recordType: RecordType,
                        val date: LocalDate,
                        val amount: BigDecimal,
                        val memo: String? = "")

