package club.piggyplanner.services.account.domain.model

import club.piggyplanner.services.account.domain.operations.CreateDefaultAccount
import club.piggyplanner.services.account.domain.operations.DefaultAccountCreated
import org.axonframework.test.aggregate.AggregateTestFixture
import org.axonframework.test.aggregate.FixtureConfiguration
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
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
        val accountId = UUID.randomUUID()

        fixture.given()
                .`when`(CreateDefaultAccount(accountId, userId))
                .expectEvents(DefaultAccountCreated(accountId, userId, Account.DEFAULT_ACCOUNT_NAME))
    }
}
