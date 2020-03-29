package club.piggyplanner.services.account.domain.services

import club.piggyplanner.services.account.domain.model.AccountId
import club.piggyplanner.services.account.domain.model.RecordType
import club.piggyplanner.services.account.interfaces.RecordDTO
import org.axonframework.commandhandling.gateway.CommandGateway
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.util.AssertionErrors.assertEquals
import org.springframework.test.util.AssertionErrors.assertNotNull
import java.math.BigDecimal
import java.time.DateTimeException
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
        val year = 2020
        val month = 12
        val day = 12
        val amount = BigDecimal.valueOf(123456.78)
        val memo = "Another test memo"
        val recordDTO = RecordDTO(accountId, recordType, year, month, day, amount, memo)

        val future = CompletableFuture<Boolean>()
        future.complete(true)
        Mockito.`when`(commandGateway.send<Boolean>(any())).thenReturn(future)

        assertDoesNotThrow {accountService.createRecord(recordDTO)}
        assert(accountService.createRecord(recordDTO).get())
    }

    @Test
    fun `Create a new valid record with no memo`() {
        val accountId = UUID.randomUUID()
        val recordType = RecordType.INCOME
        val year = 2020
        val month = 2
        val day = 12
        val amount = BigDecimal.valueOf(133.78)
        val recordDTO = RecordDTO(accountId, recordType, year, month, day, amount)

        val future = CompletableFuture<Boolean>()
        future.complete(true)
        Mockito.`when`(commandGateway.send<Boolean>(any())).thenReturn(future)

        assertDoesNotThrow {accountService.createRecord(recordDTO)}
        assert(accountService.createRecord(recordDTO).get())
    }

    @Test
    fun `Validate dates for RecordDTO`() {
        val accountId = UUID.randomUUID()
        val amount = BigDecimal.valueOf(133.78)

        var recordToTest = RecordDTO(accountId, RecordType.INCOME, 2020, 13, 12, amount)
        assertThrows<DateTimeException> { recordToTest.getRecord() }

        recordToTest = RecordDTO(accountId, RecordType.EXPENSE, 2019, 2, 29, amount)
        assertThrows<DateTimeException> { recordToTest.getRecord() }

        recordToTest = RecordDTO(accountId, RecordType.INCOME, 2020, 0, 1, amount)
        assertThrows<DateTimeException> { recordToTest.getRecord() }

        recordToTest = RecordDTO(accountId, RecordType.EXPENSE, 2020, 1, 0, amount)
        assertThrows<DateTimeException> { recordToTest.getRecord() }
    }
}