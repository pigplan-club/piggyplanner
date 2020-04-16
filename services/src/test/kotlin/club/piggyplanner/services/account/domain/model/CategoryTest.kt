package club.piggyplanner.services.account.domain.model

import club.piggyplanner.services.account.domain.operations.CategoryCreated
import club.piggyplanner.services.account.domain.operations.CategoryItemCreated
import club.piggyplanner.services.account.domain.operations.CreateCategory
import club.piggyplanner.services.account.domain.operations.CreateCategoryItem
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
    private lateinit var category : Category
    
    @BeforeEach
    internal fun setUp() {
        fixture = AggregateTestFixture(Account::class.java)
        category = Category(CategoryId(UUID.randomUUID()), "Utility")
    }

    @Test
    internal fun `Create a correct Category`() {
        val createCategoryCommand = CreateCategory(
                accountId = AccountId(CommonTest.accountId),
                categoryId = category.categoryId,
                name = category.name)

        fixture.given(CommonTest.generateDefaultAccountCreatedEvent())
                .`when`(createCategoryCommand)
                .expectSuccessfulHandlerExecution()
                .expectEventsMatching(Matchers.payloadsMatching(Matchers.exactSequenceOf(
                        sameBeanAs(CategoryCreated(AccountId(CommonTest.accountId), category)))))
                .expectResultMessagePayload(true)
    }

    @Test
    internal fun `Create a correct Category Item`() {
        val createCategoryItemCommand = CreateCategoryItem(
                accountId = AccountId(CommonTest.accountId),
                categoryId = category.categoryId,
                categoryItemId = CommonTest.categoryItem.categoryItemId,
                name = CommonTest.categoryItem.name)

        fixture.given(CommonTest.generateDefaultAccountCreatedEvent())
                .andGiven(CategoryCreated(AccountId(CommonTest.accountId), category))
                .`when`(createCategoryItemCommand)
                .expectSuccessfulHandlerExecution()
                .expectEventsMatching(Matchers.payloadsMatching(Matchers.exactSequenceOf(
                        sameBeanAs(CategoryItemCreated(AccountId(CommonTest.accountId),
                                category.categoryId,
                                CommonTest.categoryItem)))))
                .expectResultMessagePayload(true)
    }

    @Test
    internal fun `Create a Category with not existing Account`() {
        val createCategoryCommand = CreateCategory(
                accountId = AccountId(UUID.randomUUID()),
                categoryId = category.categoryId,
                name = category.name)

        try {
            fixture.given(CommonTest.generateDefaultAccountCreatedEvent())
                    .`when`(createCategoryCommand)
        } catch (e: Error) {
            assertNotNull("Expected error message", e.message)
            assertEquals("Expected AssertionError class", e.javaClass, AssertionError::class.java)
        }
    }

    @Test
    internal fun `Create a Category Item with not existing Account`() {
        val createCategoryItemCommand = CreateCategoryItem(
                accountId = AccountId(CommonTest.accountId),
                categoryId = category.categoryId,
                categoryItemId = CommonTest.categoryItem.categoryItemId,
                name = CommonTest.categoryItem.name)

        try {
            fixture.given(CommonTest.generateDefaultAccountCreatedEvent())
                    .andGiven(CategoryCreated(AccountId(CommonTest.accountId), category))
                    .`when`(createCategoryItemCommand)
        } catch (e: Error) {
            assertNotNull("Expected error message", e.message)
            assertEquals("Expected AssertionError class", e.javaClass, AssertionError::class.java)
        }
    }

    @Test
    internal fun `Create a Category Item with not existing Category`() {
        val createCategoryItemCommand = CreateCategoryItem(
                accountId = AccountId(CommonTest.accountId),
                categoryId = CategoryId(UUID.randomUUID()),
                categoryItemId = CommonTest.categoryItem.categoryItemId,
                name = CommonTest.categoryItem.name)

        try {
            fixture.given(CommonTest.generateDefaultAccountCreatedEvent())
                    .andGiven(CategoryCreated(AccountId(CommonTest.accountId), category))
                    .`when`(createCategoryItemCommand)
        } catch (e: Error) {
            e.printStackTrace()
            assertNotNull("Expected error message", e.message)
            assertEquals("Expected CategoryNotFoundException class", e.javaClass, CategoryNotFoundException::class.java)
        }
    }
}
