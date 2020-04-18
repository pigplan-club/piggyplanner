package club.piggyplanner.services.account.domain.model

import club.piggyplanner.services.account.domain.operations.*
import com.shazam.shazamcrest.matcher.Matchers.sameBeanAs
import org.axonframework.test.AxonAssertionError
import org.axonframework.test.aggregate.AggregateTestFixture
import org.axonframework.test.aggregate.FixtureConfiguration
import org.axonframework.test.matchers.Matchers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

class RecordTest {
    private lateinit var fixture: FixtureConfiguration<Account>

    @BeforeEach
    internal fun setUp() {
        fixture = AggregateTestFixture(Account::class.java)
    }

    @Test
    internal fun `Create a correct Record`() {
        val record = createRecordForTest(true)
        val createRecordCommand = generateRecordCommand(record)

        fixture.given(CommonTest.generateDefaultAccountCreatedEvent())
                .andGiven(generateCategoryCreatedEvent())
                .andGiven(generateCategoryItemCreatedEvent())
                .`when`(createRecordCommand)
                .expectSuccessfulHandlerExecution()
                .expectEventsMatching(Matchers.payloadsMatching(Matchers.exactSequenceOf(
                        sameBeanAs(
                                RecordCreated(AccountId(CommonTest.accountId), CommonTest.category.categoryId, record))
                )))
                .expectResultMessagePayload(true)
    }

    @Test
    internal fun `Create a correct Record without memo`() {
        val record = createRecordForTest(false)
        val createRecordCommand = generateRecordCommand(record)

        fixture.given(CommonTest.generateDefaultAccountCreatedEvent())
                .andGiven(generateCategoryCreatedEvent())
                .andGiven(generateCategoryItemCreatedEvent())
                .`when`(createRecordCommand)
                .expectSuccessfulHandlerExecution()
                .expectEventsMatching(Matchers.payloadsMatching(Matchers.exactSequenceOf(
                        sameBeanAs(
                                RecordCreated(AccountId(CommonTest.accountId), CommonTest.category.categoryId, record))
                )))
                .expectResultMessagePayload(true)
    }

    @Test
    internal fun `Create a Record with an invalid amount`() {
        assertThrows<AmountInvalidException>("Should throw AmountInvalidException") {
            createRecordForTest(false, BigDecimal.valueOf(-5))
        }
    }

    @Test
    internal fun `Create a duplicated record`() {
        val record = createRecordForTest(false)

        val createNewRecordCommand = CreateRecord(
                accountId = AccountId(CommonTest.accountId),
                recordId = record.recordId,
                recordType = RecordType.EXPENSE,
                categoryId = CommonTest.category.categoryId,
                categoryItemId = CommonTest.categoryItem.categoryItemId,
                date = LocalDate.MIN,
                amount = BigDecimal.ONE,
                memo = "This is another note")

        fixture.given(CommonTest.generateDefaultAccountCreatedEvent())
                .andGiven(generateCategoryCreatedEvent())
                .andGiven(generateCategoryItemCreatedEvent())
                .andGiven(RecordCreated(AccountId(CommonTest.accountId), CommonTest.category.categoryId, record))
                .`when`(createNewRecordCommand)
                .expectExceptionMessage("Record duplicated")
                .expectException(RecordAlreadyAddedException::class.java)
    }

    @Test
    internal fun `Modify a Record`() {
        val record = createRecordForTest(false)
        val recordModified = Record(
                record.recordId,
                type = RecordType.INCOME,
                date = record.date,
                categoryItem = CommonTest.categoryItem,
                amount = RecordAmount(BigDecimal.TEN),
                memo = "New memo")

        val modifyRecordCommand = ModifyRecord(
                accountId = AccountId(CommonTest.accountId),
                recordId = recordModified.recordId,
                recordType = recordModified.type,
                categoryId = CommonTest.category.categoryId,
                categoryItemId = CommonTest.categoryItem.categoryItemId,
                date = recordModified.date,
                amount = recordModified.amount.value,
                memo = recordModified.memo)

        fixture.given(CommonTest.generateDefaultAccountCreatedEvent())
                .andGiven(generateCategoryCreatedEvent())
                .andGiven(generateCategoryItemCreatedEvent())
                .andGiven(RecordCreated(AccountId(CommonTest.accountId), CommonTest.category.categoryId, record))
                .`when`(modifyRecordCommand)
                .expectSuccessfulHandlerExecution()
                .expectEventsMatching(Matchers.payloadsMatching(Matchers.exactSequenceOf(
                        sameBeanAs(RecordModified(AccountId(CommonTest.accountId), recordModified)))))
    }

    @Test
    internal fun `Create a Record with not existing Account`() {
        val record = createRecordForTest(true)
        val createRecordCommand = generateRecordCommand(record)

        try {
            fixture.given(CommonTest.generateDefaultAccountCreatedEvent())
                    .`when`(createRecordCommand)
        } catch (e: Error) {
            assertNotNull("Expected error message", e.message)
            assertEquals("Expected AssertionError class", e.javaClass, AssertionError::class.java)
        }
    }

    @Test
    internal fun `Error creating a Record exceeding quota by month`() {
        val record = createRecordForTest(true)
        val createNewRecordCommand = CreateRecord(
                accountId = AccountId(CommonTest.accountId),
                recordId = RecordId(UUID.randomUUID()),
                recordType = RecordType.EXPENSE,
                categoryId = CommonTest.category.categoryId,
                categoryItemId = CommonTest.categoryItem.categoryItemId,
                date = LocalDate.now(),
                amount = BigDecimal.ONE,
                memo = "This is another note")

        fixture.given(CommonTest.generateDefaultAccountCreatedEvent())
                .andGiven(generateCategoryCreatedEvent())
                .andGiven(generateCategoryItemCreatedEvent())
                .andGiven(RecordCreated(AccountId(CommonTest.accountId), CommonTest.category.categoryId, record))
                .`when`(createNewRecordCommand)
                .expectExceptionMessage("Records quota exceeded")
                .expectException(RecordsQuotaExceededException::class.java)
    }

    private fun generateRecordCommand(record: Record): CreateRecord {
        return CreateRecord(
                accountId = AccountId(CommonTest.accountId),
                recordId = record.recordId,
                recordType = record.type,
                categoryId = CommonTest.category.categoryId,
                categoryItemId = CommonTest.categoryItem.categoryItemId,
                date = record.date,
                amount = record.amount.value,
                memo = record.memo)
    }

    private fun generateCategoryItemCreatedEvent() =
            CategoryItemCreated(AccountId(CommonTest.accountId), CommonTest.category.categoryId, CommonTest.categoryItem)

    private fun generateCategoryCreatedEvent() =
            CategoryCreated(AccountId(CommonTest.accountId), CommonTest.category)

    private fun createRecordForTest(withMemo: Boolean, amount: BigDecimal? = BigDecimal.ONE): Record {
        val recordId = UUID.randomUUID()
        val recordType = RecordType.EXPENSE
        val date = LocalDate.now()

        if (withMemo)
            return Record(recordId = RecordId(recordId), type = recordType, categoryItem = CommonTest.categoryItem, date = date, amount = RecordAmount(amount!!), memo = "test memo")

        return Record(recordId = RecordId(recordId), type = recordType, categoryItem = CommonTest.categoryItem, date = date, amount = RecordAmount(amount!!))
    }
}
