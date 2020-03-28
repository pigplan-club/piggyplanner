package club.piggyplanner.services.account.domain.services

import club.piggyplanner.services.account.domain.model.AccountId
import club.piggyplanner.services.account.domain.model.Record
import club.piggyplanner.services.account.domain.model.RecordType
import club.piggyplanner.services.account.domain.model.UserId
import club.piggyplanner.services.account.domain.operations.CreateDefaultAccount
import club.piggyplanner.services.account.domain.operations.CreateRecord
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*
import java.util.concurrent.CompletableFuture

@Service
class AccountService(private val commandGateway: CommandGateway) {

    fun createDefaultAccount(userId: UUID): CompletableFuture<AccountId> {
        return commandGateway.send(CreateDefaultAccount(UserId(userId)))
    }

    fun createRecord(accountId: UUID, recordType: RecordType, date: LocalDate, value: BigDecimal, memo: String? = ""): CompletableFuture<Boolean> {
        return commandGateway.send(CreateRecord(AccountId(accountId),
            Record(type = recordType, dateTime = date, value = value, memo = memo)))
    }

}
