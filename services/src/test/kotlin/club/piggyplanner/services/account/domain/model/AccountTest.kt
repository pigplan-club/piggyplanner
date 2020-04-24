package club.piggyplanner.services.account.domain.model

import club.piggyplanner.services.account.domain.operations.CreateDefaultAccount
import club.piggyplanner.services.account.domain.operations.DefaultAccountCreated
import club.piggyplanner.services.account.infrastructure.config.AccountConfigProperties
import org.axonframework.test.aggregate.AggregateTestFixture
import org.axonframework.test.aggregate.FixtureConfiguration
import org.axonframework.test.matchers.Matchers
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class AccountTest {
    private lateinit var fixture: FixtureConfiguration<Account>
    lateinit var accountConfigProperties: AccountConfigProperties

    @BeforeEach
    internal fun setUp() {
        accountConfigProperties = AccountConfigProperties("Personal", 2, 2, 2)
        fixture = AggregateTestFixture(Account::class.java)
    }

    @Test
    internal fun `Create a default Account should be correct`() {
        val userId = UUID.randomUUID()
        val createDefaultAccountCommand = CreateDefaultAccount(UserId(userId))

        fixture.registerInjectableResource(accountConfigProperties)
                .givenNoPriorActivity()
                .`when`(createDefaultAccountCommand)
                .expectSuccessfulHandlerExecution()
                .expectEventsMatching(Matchers.payloadsMatching(Matchers.exactSequenceOf(
                        com.shazam.shazamcrest.matcher.Matchers.sameBeanAs(
                                DefaultAccountCreated(createDefaultAccountCommand.accountId,
                                        UserId(userId),
                                        accountConfigProperties.defaultAccountName,
                                        accountConfigProperties.recordsQuotaByMonth,
                                        accountConfigProperties.categoriesQuota,
                                        accountConfigProperties.categoryItemsQuota
                                )
                        ))))
                .expectResultMessagePayload(createDefaultAccountCommand.accountId)
    }
}
