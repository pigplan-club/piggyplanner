package club.piggyplanner.services.account.domain.model

import club.piggyplanner.services.account.domain.model.utils.UtilTest
import club.piggyplanner.services.account.domain.operations.CategoryCreated
import club.piggyplanner.services.account.domain.operations.CategoryItemCreated
import club.piggyplanner.services.account.domain.operations.CreateCategory
import club.piggyplanner.services.account.domain.operations.CreateCategoryItem
import org.axonframework.modelling.command.AggregateNotFoundException
import org.axonframework.test.aggregate.AggregateTestFixture
import org.axonframework.test.aggregate.FixtureConfiguration
import org.junit.Assert.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class CategoryTest {
    private lateinit var fixture: FixtureConfiguration<Account>

    @BeforeEach
    internal fun setUp() {
        fixture = AggregateTestFixture(Account::class.java)
    }

    @Test
    internal fun `Create a Category should be correct`() {
        val createCategoryCommand = CreateCategory(
                accountId = AccountId(UtilTest.accountId),
                categoryId = UtilTest.category.categoryId,
                name = UtilTest.category.name)

        fixture.given(UtilTest.generateDefaultAccountCreatedEvent())
                .`when`(createCategoryCommand)
                .expectSuccessfulHandlerExecution()
                .expectEvents(CategoryCreated(AccountId(UtilTest.accountId), UtilTest.category))
                .expectResultMessagePayload(true)
    }

    @Test
    internal fun `Create a Category Item should be correct`() {
        val category = Category(CategoryId(UUID.randomUUID()), "Utility")
        val createCategoryItemCommand = CreateCategoryItem(
                accountId = AccountId(UtilTest.accountId),
                categoryId = category.categoryId,
                categoryItemId = UtilTest.categoryItem.categoryItemId,
                name = UtilTest.categoryItem.name)

        fixture.given(UtilTest.generateDefaultAccountCreatedEvent())
                .andGiven(CategoryCreated(AccountId(UtilTest.accountId), category))
                .`when`(createCategoryItemCommand)
                .expectSuccessfulHandlerExecution()
                .expectEvents(CategoryItemCreated(AccountId(UtilTest.accountId),
                        category.categoryId,
                        UtilTest.categoryItem))
                .expectResultMessagePayload(true)
    }

    @Test
    internal fun `Create a duplicated Category should throw CategoryAlreadyAddedException`() {
        val createCategoryCommand = CreateCategory(
                accountId = AccountId(UtilTest.accountId),
                categoryId = UtilTest.category.categoryId,
                name = UtilTest.category.name)

        fixture.given(UtilTest.generateDefaultAccountCreatedEvent())
                .andGiven(CategoryCreated(AccountId(UtilTest.accountId), UtilTest.category))
                .`when`(createCategoryCommand)
                .expectExceptionMessage("Category duplicated")
                .expectException(CategoryAlreadyAddedException::class.java)
    }

    @Test
    internal fun `Create a duplicated Category Item should throw CategoryItemAlreadyAddedException`() {
        val createCategoryItemCommand = CreateCategoryItem(
                accountId = AccountId(UtilTest.accountId),
                categoryId = UtilTest.category.categoryId,
                categoryItemId = UtilTest.categoryItem.categoryItemId,
                name = UtilTest.categoryItem.name)

        fixture.given(UtilTest.generateDefaultAccountCreatedEvent())
                .andGiven(CategoryCreated(AccountId(UtilTest.accountId), UtilTest.category))
                .andGiven(CategoryItemCreated(AccountId(UtilTest.accountId), UtilTest.category.categoryId, UtilTest.categoryItem))
                .`when`(createCategoryItemCommand)
                .expectExceptionMessage("Category Item duplicated")
                .expectException(CategoryItemAlreadyAddedException::class.java)
    }

    @Test
    internal fun `Create a Category with non existing Account should throw AggregateNotFoundException`() {
        val createCategoryCommand = CreateCategory(
                accountId = AccountId(UUID.randomUUID()),
                categoryId = UtilTest.category.categoryId,
                name = UtilTest.category.name)

        fixture.givenNoPriorActivity()
                .`when`(createCategoryCommand)
                .expectException(AggregateNotFoundException::class.java)
    }

    @Test
    internal fun `Create a Category Item with non existing Account shuld throw AssertionError`() {
        val createCategoryItemCommand = CreateCategoryItem(
                accountId = AccountId(UtilTest.accountId),
                categoryId = UtilTest.category.categoryId,
                categoryItemId = UtilTest.categoryItem.categoryItemId,
                name = UtilTest.categoryItem.name)

        try {
            fixture.given(UtilTest.generateDefaultAccountCreatedEvent())
                    .andGiven(CategoryCreated(AccountId(UtilTest.accountId), UtilTest.category))
                    .`when`(createCategoryItemCommand)
        } catch (e: Error) {
            assertNotNull("Expected error message", e.message)
            assertEquals("Expected AssertionError class", e.javaClass, AssertionError::class.java)
        }
    }

    @Test
    internal fun `Create a Category Item with non existing Category should throw CategoryNotFoundException`() {
        val newCategoryIdUUID = UUID.randomUUID()
        val createCategoryItemCommand = CreateCategoryItem(
                accountId = AccountId(UtilTest.accountId),
                categoryId = CategoryId(newCategoryIdUUID),
                categoryItemId = UtilTest.categoryItem.categoryItemId,
                name = UtilTest.categoryItem.name)

        fixture.given(UtilTest.generateDefaultAccountCreatedEvent())
                .andGiven(CategoryCreated(AccountId(UtilTest.accountId), UtilTest.category))
                .`when`(createCategoryItemCommand)
                .expectExceptionMessage("Category with id $newCategoryIdUUID not found")
                .expectException(CategoryNotFoundException::class.java)
    }

    @Test
    internal fun `Create a Category exceeding quota should throw CategoriesQuotaExceededException`() {
        val createCategoryCommand = CreateCategory(
                accountId = AccountId(UtilTest.accountId),
                categoryId = CategoryId(UUID.randomUUID()),
                name = "New Category")

        fixture.given(UtilTest.generateDefaultAccountCreatedEvent(categoriesQuota = 1))
                .andGiven(CategoryCreated(AccountId(UtilTest.accountId), UtilTest.category))
                .`when`(createCategoryCommand)
                .expectExceptionMessage("Categories quota exceeded")
                .expectException(CategoriesQuotaExceededException::class.java)
    }

    @Test
    internal fun `Create a Category Item exceeding quota should throw CategoryItemsQuotaExceededException`() {
        val createCategoryItemCommand = CreateCategoryItem(
                accountId = AccountId(UtilTest.accountId),
                categoryId = UtilTest.category.categoryId,
                categoryItemId = CategoryItemId(UUID.randomUUID()),
                name = "New Category Item")

        fixture.given(UtilTest.generateDefaultAccountCreatedEvent(categoryItemsQuota = 1))
                .andGiven(CategoryCreated(AccountId(UtilTest.accountId), UtilTest.category))
                .andGiven(CategoryItemCreated(AccountId(UtilTest.accountId), UtilTest.category.categoryId, UtilTest.categoryItem))
                .`when`(createCategoryItemCommand)
                .expectExceptionMessage("Category items quota exceeded")
                .expectException(CategoryItemsQuotaExceededException::class.java)
    }

    @Test
    internal fun `Category items when category is created should be empty`() {
        val categoryId = CategoryId(UUID.randomUUID())
        val category = Category(categoryId, "Category test")

        assertTrue("Category Items are ampty", category.categoryItems.isEmpty())
        assertEquals("Category Items is a mutable set", category.categoryItems, mutableSetOf<CategoryItem>())
    }

    @Test
    internal fun `Compare Category equality`() {
        val categoryId = CategoryId(UUID.randomUUID())
        val category1 = Category(categoryId, "Category test")
        val category2 = Category(categoryId, "Category test")
        val category3 = Category(CategoryId(UUID.randomUUID()), "Category test")
        val category4 = Category(categoryId, "Category test 2")
        val list = mutableSetOf(category1, category3)

        assertEquals("Same object should be equals", category1, category1)
        assertEquals("Same id and name should be equals", category1, category2)
        assertNotEquals("Different id and same name should be differents", category1, category3)
        assertNotEquals("Same id and different name should be differents", category1, category4)
        assertTrue(list.contains(category2))
        assertFalse(list.contains(category4))

        list.add(category2)
        assertEquals("Same object should not be added", 2, list.size)
    }

    @Test
    internal fun `Compare Category Items equality`() {
        val categoryItemId = CategoryItemId(UUID.randomUUID())
        val categoryItem1 = CategoryItem(categoryItemId, "Category Item test")
        val categoryItem2 = CategoryItem(categoryItemId, "Category Item test")
        val categoryItem3 = CategoryItem(CategoryItemId(UUID.randomUUID()), "Category test")
        val categoryItem4 = CategoryItem(categoryItemId, "Category Item test 2")
        val list = mutableSetOf(categoryItem1, categoryItem3)

        assertEquals("Same object should be equals", categoryItem1, categoryItem1)
        assertEquals("Same id and name should be equals", categoryItem1, categoryItem2)
        assertNotEquals("Different id and same name should be differents", categoryItem1, categoryItem3)
        assertNotEquals("Same id and different name should be differents", categoryItem1, categoryItem4)
        assertTrue(list.contains(categoryItem2))
        assertFalse(list.contains(categoryItem4))

        list.add(categoryItem2)
        assertEquals("Same object should not be added", 2, list.size)
    }
}