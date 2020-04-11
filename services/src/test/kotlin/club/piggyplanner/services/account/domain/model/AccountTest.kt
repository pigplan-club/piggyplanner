package club.piggyplanner.services.account.domain.model

import club.piggyplanner.services.account.domain.operations.*
import com.shazam.shazamcrest.matcher.Matchers.sameBeanAs
import org.axonframework.test.aggregate.AggregateTestFixture
import org.axonframework.test.aggregate.FixtureConfiguration
import org.axonframework.test.matchers.Matchers
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

class AccountTest {
    private lateinit var fixture: FixtureConfiguration<Account>

    @BeforeEach
    internal fun setUp() {
        fixture = AggregateTestFixture(Account::class.java)
    }

    @Test
    internal fun `Create a default Account`() {
        val userId = UUID.randomUUID()
        val createDefaultAccountCommand = CreateDefaultAccount(SaverId(userId))

        fixture.givenNoPriorActivity()
                .`when`(createDefaultAccountCommand)
                .expectSuccessfulHandlerExecution()
                .expectEvents(DefaultAccountCreated(createDefaultAccountCommand.accountId,
                        SaverId(userId),
                        Account.DEFAULT_ACCOUNT_NAME))
                .expectResultMessagePayload(createDefaultAccountCommand.accountId)
    }


    @Test
    internal fun `Create a correct Record`() {
        val userId = UUID.randomUUID()
        val accountId = UUID.randomUUID()
        val record = createRecordForTest(true)
        val createRecordCommand = CreateRecord(
                accountId = AccountId(accountId),
                recordId = record.recordId,
                recordType = record.type,
                categoryItem = record.categoryItem,
                date = record.date,
                amount = record.amount,
                memo = record.memo)

        fixture.given(DefaultAccountCreated(AccountId(accountId), SaverId(userId), Account.DEFAULT_ACCOUNT_NAME))
                .`when`(createRecordCommand)
                .expectSuccessfulHandlerExecution()
                .expectEventsMatching(Matchers.payloadsMatching(Matchers.exactSequenceOf(
                        sameBeanAs(RecordCreated(AccountId(accountId), record)))))
                .expectResultMessagePayload(true)
    }

    @Test
    internal fun `Create a correct Record without memo`() {
        val userId = UUID.randomUUID()
        val accountId = UUID.randomUUID()
        val record = createRecordForTest(false)
        val createRecordCommand = CreateRecord(
                accountId = AccountId(accountId),
                recordId = record.recordId,
                recordType = record.type,
                categoryItem = record.categoryItem,
                date = record.date,
                amount = record.amount,
                memo = record.memo)

        fixture.given(DefaultAccountCreated(AccountId(accountId), SaverId(userId), Account.DEFAULT_ACCOUNT_NAME))
                .`when`(createRecordCommand)
                .expectSuccessfulHandlerExecution()
                .expectEventsMatching(Matchers.payloadsMatching(Matchers.exactSequenceOf(
                        sameBeanAs(RecordCreated(AccountId(accountId), record)))))
                .expectResultMessagePayload(true)
    }

    @Test
    internal fun `Create a duplicated record`() {
        val userId = UUID.randomUUID()
        val accountId = UUID.randomUUID()
        val record = createRecordForTest(false)
        val createNewRecordCommand = CreateRecord(
                accountId = AccountId(accountId),
                recordId = record.recordId,
                recordType = RecordType.EXPENSE,
                categoryItem = record.categoryItem,
                date = LocalDate.MIN,
                amount = BigDecimal.ONE,
                memo = "This is another note")

        fixture.given(DefaultAccountCreated(AccountId(accountId), SaverId(userId), Account.DEFAULT_ACCOUNT_NAME))
                .andGiven(RecordCreated(AccountId(accountId), record))
                .`when`(createNewRecordCommand)
                .expectException(RecordAlreadyAddedException::class.java)
                .expectExceptionMessage("Record id duplicated")
    }

    @Test
    internal fun `Modify a Record`() {
        val userId = UUID.randomUUID()
        val accountId = UUID.randomUUID()
        val record = createRecordForTest(false)
        val recordModified = Record(
                record.recordId,
                type = RecordType.INCOME,
                date = record.date,
                categoryItem = CategoryItem(
                        CategoryItemId(UUID.randomUUID()),
                        "",
                        Category(
                                CategoryId(UUID.randomUUID()),
                                "")),
                amount = BigDecimal.TEN,
                memo = "New memo")

        val modifyRecordCommand = ModifyRecord(
                accountId = AccountId(accountId),
                recordId = recordModified.recordId,
                recordType = recordModified.type,
                categoryItem = recordModified.categoryItem,
                date = recordModified.date,
                amount = recordModified.amount,
                memo = recordModified.memo)

        fixture.given(DefaultAccountCreated(AccountId(accountId), SaverId(userId), Account.DEFAULT_ACCOUNT_NAME))
                .andGiven(RecordCreated(AccountId(accountId), record))
                .`when`(modifyRecordCommand)
                .expectSuccessfulHandlerExecution()
                .expectEventsMatching(Matchers.payloadsMatching(Matchers.exactSequenceOf(
                        sameBeanAs(RecordModified(recordModified)))))
    }

    private fun createRecordForTest(withMemo: Boolean): Record {
        val recordId = UUID.randomUUID()
        val recordType = RecordType.EXPENSE
        val date = LocalDate.now()
        val categoryItem = CategoryItem(
                CategoryItemId(UUID.randomUUID()),
                "Energy",
                Category(
                        CategoryId(UUID.randomUUID()),
                        "Utility"))
        val amount = BigDecimal.valueOf(9876.12)

        if (withMemo)
            return Record(recordId = RecordId(recordId), type = recordType, categoryItem = categoryItem, date = date, amount = amount, memo = "test memo")

        return Record(recordId = RecordId(recordId), type = recordType, categoryItem = categoryItem, date = date, amount = amount)
    }
}
