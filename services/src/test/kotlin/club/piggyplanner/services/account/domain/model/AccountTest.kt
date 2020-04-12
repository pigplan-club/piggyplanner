package club.piggyplanner.services.account.domain.model

import club.piggyplanner.services.account.domain.operations.CreateDefaultAccount
import club.piggyplanner.services.account.domain.operations.DefaultAccountCreated
import org.axonframework.test.aggregate.AggregateTestFixture
import org.axonframework.test.aggregate.FixtureConfiguration
import org.axonframework.test.matchers.Matchers
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
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
                .expectEventsMatching(Matchers.payloadsMatching(Matchers.exactSequenceOf(
                        com.shazam.shazamcrest.matcher.Matchers.sameBeanAs(
                                DefaultAccountCreated(createDefaultAccountCommand.accountId,
                                        SaverId(userId),
                                        Account.DEFAULT_ACCOUNT_NAME,
                                        createDefaultAccountCommand.recordsQuotaByMonth,
                                        createDefaultAccountCommand.categoriesQuota,
                                        createDefaultAccountCommand.categoryItemsQuota
                                )
                        ))))
                .expectResultMessagePayload(createDefaultAccountCommand.accountId)
    }

}
