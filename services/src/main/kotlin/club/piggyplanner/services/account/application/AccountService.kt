package club.piggyplanner.services.account.application

import club.piggyplanner.services.account.domain.model.AccountId
import club.piggyplanner.services.account.domain.model.RecordId
import club.piggyplanner.services.account.domain.model.UserId
import club.piggyplanner.services.account.domain.operations.CreateDefaultAccount
import club.piggyplanner.services.account.domain.operations.CreateRecord
import club.piggyplanner.services.account.domain.operations.DeleteRecord
import club.piggyplanner.services.account.domain.operations.ModifyRecord
import club.piggyplanner.services.account.presentation.RecordDTO
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.queryhandling.QueryGateway
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.CompletableFuture

@Service
class AccountService(private val commandGateway: CommandGateway,
                     private val queryGateway: QueryGateway) {

    //TODO: Remove this when the saga pattern is implemented
    fun createDefaultAccount(saverId: UUID): CompletableFuture<AccountId> {
        return commandGateway.send(CreateDefaultAccount(UserId(saverId)))
    }

    fun createRecord(recordDTO: RecordDTO): CompletableFuture<Boolean> {
        return commandGateway.send(CreateRecord(
                recordDTO.getAccountId(),
                recordDTO.getRecordId(),
                recordDTO.recordType,
                recordDTO.getCategoryId(),
                recordDTO.getCategoryItemId(),
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
                recordDTO.getCategoryId(),
                recordDTO.getCategoryItemId(),
                recordDTO.getDate(),
                recordDTO.amount,
                recordDTO.memo
        ))
    }

    fun deleteRecord(accountId: UUID, recordId: UUID): CompletableFuture<Boolean> {
        return commandGateway.send(DeleteRecord(
                AccountId(accountId),
                RecordId(recordId)
        ))
    }
}
