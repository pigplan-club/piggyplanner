package club.piggyplanner.services.account.domain.model

import club.piggyplanner.services.account.domain.operations.CategoryCreated
import club.piggyplanner.services.account.domain.operations.CreateCategory
import club.piggyplanner.services.account.domain.operations.CreateCategoryItem
import club.piggyplanner.services.account.domain.operations.DefaultAccountCreated
import club.piggyplanner.services.account.infrastructure.config.AccountConfigProperties
import com.shazam.shazamcrest.matcher.Matchers.sameBeanAs
import org.axonframework.test.aggregate.AggregateTestFixture
import org.axonframework.test.aggregate.FixtureConfiguration
import org.axonframework.test.matchers.Matchers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class CategoryTest {
    private lateinit var fixture: FixtureConfiguration<Account>
    lateinit var accountConfigProperties: AccountConfigProperties

    @BeforeEach
    internal fun setUp() {
        accountConfigProperties = AccountConfigProperties("Personal", 2, 2, 2)
        fixture = AggregateTestFixture(Account::class.java)
    }

    @Test
    internal fun `Create a correct Category`() {
        val userId = UUID.randomUUID()
        val accountId = UUID.randomUUID()
        val category = createCategoryForTest()
        val createCategoryCommand = CreateCategory(
                accountId = AccountId(accountId),
                categoryId = category.categoryId,
                name = category.name)

        fixture.given(DefaultAccountCreated(
                AccountId(accountId),
                SaverId(userId),
                accountConfigProperties.defaultAccountName,
                accountConfigProperties.recordsQuotaByMonth,
                accountConfigProperties.categoriesQuota,
                accountConfigProperties.categoryItemsQuota))
                .`when`(createCategoryCommand)
                .expectSuccessfulHandlerExecution()
                .expectEventsMatching(Matchers.payloadsMatching(Matchers.exactSequenceOf(
                        sameBeanAs(CategoryCreated(AccountId(accountId), category)))))
                .expectResultMessagePayload(true)
    }

    @Test
    internal fun `Create a correct Category Item`() {
        val userId = UUID.randomUUID()
        val accountId = UUID.randomUUID()
        val categoryItem = createCategoryItemForTest()

        val createCategoryItemCommand = CreateCategoryItem(
                accountId = AccountId(accountId),
                categoryId = categoryItem.category.categoryId,
                categoryItemId = categoryItem.categoryItemId,
                name = categoryItem.name)

        fixture.given(DefaultAccountCreated(AccountId(accountId), SaverId(userId), accountConfigProperties.defaultAccountName,
                accountConfigProperties.recordsQuotaByMonth,
                accountConfigProperties.categoriesQuota,
                accountConfigProperties.categoryItemsQuota))
                .andGiven(CategoryCreated(AccountId(accountId), categoryItem.category))
                .`when`(createCategoryItemCommand)
                .expectSuccessfulHandlerExecution()
//                .expectEventsMatching(Matchers.payloadsMatching(Matchers.exactSequenceOf(
//                        sameBeanAs(CategoryItemCreated(AccountId(accountId), categoryItem)))))
                .expectResultMessagePayload(true)
    }

    @Test
    internal fun `Create a Category with not existing Account`() {
        val userId = UUID.randomUUID()
        val accountId = UUID.randomUUID()
        val category = createCategoryForTest()
        val createCategoryCommand = CreateCategory(
                accountId = AccountId(UUID.randomUUID()),
                categoryId = category.categoryId,
                name = category.name)

        try {
            fixture.given(DefaultAccountCreated(AccountId(accountId), SaverId(userId), accountConfigProperties.defaultAccountName,
                    accountConfigProperties.recordsQuotaByMonth,
                    accountConfigProperties.categoriesQuota,
                    accountConfigProperties.categoryItemsQuota))
                    .`when`(createCategoryCommand)
        } catch (e: Error) {
            assertNotNull("Expected error message", e.message)
            assertEquals("Expected AssertionError class", e.javaClass, AssertionError::class.java)
        }
    }

    @Test
    internal fun `Create a Category Item with not existing Account`() {
        val userId = UUID.randomUUID()
        val accountId = UUID.randomUUID()
        val categoryItem = createCategoryItemForTest()

        val createCategoryItemCommand = CreateCategoryItem(
                accountId = AccountId(accountId),
                categoryId = categoryItem.category.categoryId,
                categoryItemId = categoryItem.categoryItemId,
                name = categoryItem.name)

        try {
            fixture.given(DefaultAccountCreated(AccountId(accountId), SaverId(userId), accountConfigProperties.defaultAccountName,
                    accountConfigProperties.recordsQuotaByMonth,
                    accountConfigProperties.categoriesQuota,
                    accountConfigProperties.categoryItemsQuota))
                    .andGiven(CategoryCreated(AccountId(accountId), categoryItem.category))
                    .`when`(createCategoryItemCommand)
        } catch (e: Error) {
            assertNotNull("Expected error message", e.message)
            assertEquals("Expected AssertionError class", e.javaClass, AssertionError::class.java)
        }
    }

    @Test
    internal fun `Create a Category Item with not existing Category`() {
        val userId = UUID.randomUUID()
        val accountId = UUID.randomUUID()
        val categoryItem = createCategoryItemForTest()

        val createCategoryItemCommand = CreateCategoryItem(
                accountId = AccountId(accountId),
                categoryId = CategoryId(UUID.randomUUID()),
                categoryItemId = categoryItem.categoryItemId,
                name = categoryItem.name)

        try {
            fixture.given(DefaultAccountCreated(AccountId(accountId), SaverId(userId), accountConfigProperties.defaultAccountName,
                    accountConfigProperties.recordsQuotaByMonth,
                    accountConfigProperties.categoriesQuota,
                    accountConfigProperties.categoryItemsQuota))
                    .andGiven(CategoryCreated(AccountId(accountId), categoryItem.category))
                    .`when`(createCategoryItemCommand)
        } catch (e: Error) {
            e.printStackTrace()
            assertNotNull("Expected error message", e.message)
            assertEquals("Expected CategoryNotFoundException class", e.javaClass, CategoryNotFoundException::class.java)
        }
    }

    companion object {
        fun createCategoryForTest() =
                Category(CategoryId(UUID.randomUUID()), "Utility")

        fun createCategoryItemForTest() =
                CategoryItem(CategoryItemId(UUID.randomUUID()), "Energy", createCategoryForTest())
    }
}
