package club.piggyplanner.services.account.domain.services

import club.piggyplanner.services.account.domain.model.AccountId
import club.piggyplanner.services.account.domain.services.AccountService
import org.axonframework.commandhandling.gateway.CommandGateway
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.util.AssertionErrors.assertNotNull
import java.util.*
import java.util.concurrent.CompletableFuture

@SpringBootTest
class AccountServiceTest {

    @MockBean
    private lateinit var commandGateway: CommandGateway

    @Autowired
    private lateinit var accountService: AccountService

    @Test
    fun `Create a default account with a non empty userId`(){
        val userId = UUID.randomUUID()
        val future = CompletableFuture<AccountId>()
        future.complete(AccountId(UUID.randomUUID()))
        Mockito.`when`(commandGateway.send<AccountId>(any())).thenReturn(future)

        val completableFuture = accountService.createDefaultAccount(userId);
        assertNotNull("Expected AccountId not null", completableFuture.get().id)
    }
}