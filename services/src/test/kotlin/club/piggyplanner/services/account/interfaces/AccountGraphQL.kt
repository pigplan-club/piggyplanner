package club.piggyplanner.services.account.interfaces

import club.piggyplanner.services.account.domain.services.AccountService
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.*

@SpringBootTest
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class AccountGraphQL(
        @Autowired private val testClient: WebTestClient,
        @Autowired private val accountService: AccountService
    ) {

    private lateinit var accountId : UUID

    @BeforeAll
    fun init(){
        accountId = accountService.createDefaultAccount(UUID.randomUUID()).get().id
    }

    @Test
    fun `verify create valid record`() {
        val mutationName = "createRecord"
        val mutation = """
            mutation {
              createRecord(
                record: {
                  accountId: "$accountId",
                  recordType: INCOME,
                  year: 2020,
                  month: 2,
                  day: 29,
                  amount: 1,
                  memo: "test"
                }
              )
            }    
        """.trimIndent()

        testClient.post()
                .uri(GRAPHQL_ENDPOINT)
                .accept(APPLICATION_JSON)
                .contentType(GRAPHQL_MEDIA_TYPE)
                .bodyValue(mutation)
                .exchange()
                .expectStatus().isOk
                .verifyOnlyDataExists(mutationName)
                .jsonPath("$DATA_JSON_PATH.$mutationName").isEqualTo(true)
    }

    private fun WebTestClient.ResponseSpec.verifyOnlyDataExists(expectedQuery: String) =
            this.expectBody()
                .jsonPath("$DATA_JSON_PATH.$expectedQuery").exists()
                .jsonPath(ERRORS_JSON_PATH).doesNotExist()
                .jsonPath(EXTENSIONS_JSON_PATH).doesNotExist()

    companion object {
        const val DATA_JSON_PATH = "$.data"
        const val ERRORS_JSON_PATH = "$.errors"
        const val EXTENSIONS_JSON_PATH = "$.extensions"
        const val GRAPHQL_ENDPOINT = "/graphql"
        val GRAPHQL_MEDIA_TYPE = MediaType("application", "graphql")
    }
}