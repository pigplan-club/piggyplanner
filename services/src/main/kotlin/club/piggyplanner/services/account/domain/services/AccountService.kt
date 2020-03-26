package club.piggyplanner.services.account.domain.services

import club.piggyplanner.services.account.domain.model.AccountId
import club.piggyplanner.services.account.domain.operations.CreateDefaultAccount
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.CompletableFuture

@Service
class AccountService(private val commandGateway: CommandGateway) {

    fun createDefaultAccount(userId: UUID): CompletableFuture<AccountId> {
        return commandGateway.send(CreateDefaultAccount(UUID.randomUUID(), userId))
    }
}
