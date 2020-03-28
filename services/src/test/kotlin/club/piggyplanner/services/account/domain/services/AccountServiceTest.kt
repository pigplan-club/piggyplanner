package club.piggyplanner.services.account.domain.services

import club.piggyplanner.services.account.domain.model.AccountId
import club.piggyplanner.services.account.domain.model.Record
import club.piggyplanner.services.account.domain.model.RecordType
import org.axonframework.commandhandling.gateway.CommandGateway
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.util.AssertionErrors.assertEquals
import org.springframework.test.util.AssertionErrors.assertNotNull
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*
import java.util.concurrent.CompletableFuture

@SpringBootTest
private class AccountServiceTest {

    @MockBean
    private lateinit var commandGateway: CommandGateway

    @Autowired
    private lateinit var accountService: AccountService

    @Test
    fun `Create a default account`() {
        val userId = UUID.randomUUID()
        val future = CompletableFuture<AccountId>()
        future.complete(AccountId(UUID.randomUUID()))
        Mockito.`when`(commandGateway.send<AccountId>(any())).thenReturn(future)

        val completableFuture = accountService.createDefaultAccount(userId)
        assertNotNull("Expected AccountId not null", completableFuture.get().id)
        assertEquals("Expected response id equal to the mocked value", completableFuture.get().id, future.get().id)
    }

    @Test
    fun `Create a new valid record`() {
        val accountId = UUID.randomUUID()
        val recordType = RecordType.INCOME
        val date = LocalDate.now()
        val value = BigDecimal.valueOf(123456.78)
        val memo = "Another test memo"

        val future = CompletableFuture<Boolean>()
        future.complete(true)
        Mockito.`when`(commandGateway.send<Boolean>(any())).thenReturn(future)

        assertDoesNotThrow {accountService.createRecord(accountId, recordType, date, value, memo)}
        assert(accountService.createRecord(accountId, recordType, date, value, memo).get())
    }

    @Test
    fun `Create a new valid record with no memo`() {
        val accountId = UUID.randomUUID()
        val recordType = RecordType.INCOME
        val date = LocalDate.now()
        val value = BigDecimal.valueOf(123456.78)

        val future = CompletableFuture<Boolean>()
        future.complete(true)
        Mockito.`when`(commandGateway.send<Boolean>(any())).thenReturn(future)

        assertDoesNotThrow {accountService.createRecord(accountId, recordType, date, value)}
        assert(accountService.createRecord(accountId, recordType, date, value).get())
    }
}