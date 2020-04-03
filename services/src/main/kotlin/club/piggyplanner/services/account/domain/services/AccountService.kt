package club.piggyplanner.services.account.domain.services

import club.piggyplanner.services.account.domain.model.AccountId
import club.piggyplanner.services.account.domain.model.UserId
import club.piggyplanner.services.account.domain.operations.CreateDefaultAccount
import club.piggyplanner.services.account.domain.operations.CreateRecord
import club.piggyplanner.services.account.domain.operations.ModifyRecord
import club.piggyplanner.services.account.interfaces.RecordDTO
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.CompletableFuture

@Service
class AccountService(private val commandGateway: CommandGateway) {

    //TODO: Remove this when the saga pattern is implemented
    fun createDefaultAccount(userId: UUID): CompletableFuture<AccountId> {
        return commandGateway.send(CreateDefaultAccount(UserId(userId)))
    }

    fun createRecord(recordDTO: RecordDTO): CompletableFuture<Boolean> {
        return commandGateway.send(CreateRecord(
                recordDTO.getAccountId(),
                recordDTO.getRecordId(),
                recordDTO.recordType,
                recordDTO.getDate(),
                recordDTO.amount,
                recordDTO.memo
        ))
    }

    fun modifyRecord(recordDTO: RecordDTO): CompletableFuture<Boolean> {
        return commandGateway.send(ModifyRecord(
                recordDTO.getAccountId(),
                recordDTO.getRecordId(),
                recordDTO.recordType,
                recordDTO.getDate(),
                recordDTO.amount,
                recordDTO.memo
        ))
    }

}
