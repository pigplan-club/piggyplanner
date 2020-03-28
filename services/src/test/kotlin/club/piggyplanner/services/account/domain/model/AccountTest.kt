package club.piggyplanner.services.account.domain.model

import club.piggyplanner.services.account.domain.operations.CreateDefaultAccount
import club.piggyplanner.services.account.domain.operations.CreateRecord
import club.piggyplanner.services.account.domain.operations.DefaultAccountCreated
import club.piggyplanner.services.account.domain.operations.RecordCreated
import org.axonframework.test.aggregate.AggregateTestFixture
import org.axonframework.test.aggregate.FixtureConfiguration
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

class AccountTest {
    private lateinit var fixture: FixtureConfiguration<Account>

    @BeforeEach
    internal fun setUp() {
        fixture = AggregateTestFixture<Account>(Account::class.java)
    }

    @Test
    internal fun `Create a default Account`() {
        val userId = UUID.randomUUID()
        val createDefaultAccountCommand = CreateDefaultAccount(UserId(userId))

        fixture.givenNoPriorActivity()
                .`when`(createDefaultAccountCommand)
                .expectSuccessfulHandlerExecution()
                .expectEvents(DefaultAccountCreated(createDefaultAccountCommand.accountId,
                        UserId(userId),
                        Account.DEFAULT_ACCOUNT_NAME))
                .expectResultMessagePayload(createDefaultAccountCommand.accountId)
    }


    @Test
    internal fun `Create a correct Record`() {
        val userId = UUID.randomUUID()
        val accountId = UUID.randomUUID()
        val recordType = RecordType.EXPENSE
        val date = LocalDate.now()
        val value = BigDecimal.valueOf(12.34)
        val memo = "testing record"
        val record = Record(type = recordType, dateTime = date, value = value, memo = memo)
        val createRecordCommand = CreateRecord(AccountId(accountId), record)

        fixture.given(DefaultAccountCreated(AccountId(accountId), UserId(userId), Account.DEFAULT_ACCOUNT_NAME))
                .`when`(createRecordCommand)
                .expectSuccessfulHandlerExecution()
                .expectEvents(RecordCreated(AccountId(accountId), record))
    }

    @Test
    internal fun `Create a correct Record without memo`() {
        val userId = UUID.randomUUID()
        val accountId = UUID.randomUUID()
        val recordType = RecordType.EXPENSE
        val date = LocalDate.now()
        val value = BigDecimal.valueOf(9876.12)
        val record = Record(type = recordType, dateTime = date, value = value)
        val createRecordCommand = CreateRecord(AccountId(accountId), record)

        fixture.given(DefaultAccountCreated(AccountId(accountId), UserId(userId), Account.DEFAULT_ACCOUNT_NAME))
                .`when`(createRecordCommand)
                .expectSuccessfulHandlerExecution()
                .expectEvents(RecordCreated(AccountId(accountId), record))
    }
}
