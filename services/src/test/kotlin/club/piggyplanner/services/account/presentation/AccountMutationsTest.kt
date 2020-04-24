package club.piggyplanner.services.account.presentation

import club.piggyplanner.services.account.domain.model.AccountId
import club.piggyplanner.services.account.domain.model.RecordType
import org.axonframework.commandhandling.gateway.CommandGateway
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import java.math.BigDecimal
import java.util.*
import java.util.concurrent.CompletableFuture

@SpringBootTest
internal class AccountMutationsTest {

    @MockBean
    private lateinit var commandGateway: CommandGateway

    @Autowired
    private lateinit var accountMutations: AccountMutations

    @Test
    fun `Create a default Account should be correct`() {
        val future = CompletableFuture<AccountId>()
        future.complete(AccountId(UUID.randomUUID()))
        Mockito.`when`(commandGateway.send<AccountId>(any())).thenReturn(future)

        val response = accountMutations.createDefaultAccount()
        assertNotNull("Expected AccountId not null", response.get().id)
        assertEquals("Expected response id equal to the mocked value", response.get().id, future.get().id)
    }

    @Test
    fun createRecord() {
        val future = CompletableFuture<Boolean>()
        future.complete(true)
        Mockito.`when`(commandGateway.send<Boolean>(any())).thenReturn(future)
        val recordDTO = RecordDTO(accountId = UUID.randomUUID(),
                recordId = UUID.randomUUID(),
                recordType = RecordType.INCOME,
                categoryId = UUID.randomUUID(),
                categoryItemId = UUID.randomUUID(),
                year = 2020,
                month = 1,
                day = 1,
                amount = BigDecimal.ONE,
                memo = "")

        val response = accountMutations.createRecord(recordDTO)
        assertNotNull("Expected response not null", response.get())
        assertEquals("Expected response equal to the mocked value", response.get(), future.get())
    }

    @Test
    fun modifyRecord() {
    }

    @Test
    fun deleteRecord() {
    }
}