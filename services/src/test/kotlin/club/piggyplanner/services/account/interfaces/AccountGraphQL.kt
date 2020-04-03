package club.piggyplanner.services.account.interfaces

import club.piggyplanner.services.account.domain.model.Record
import club.piggyplanner.services.account.domain.model.RecordId
import club.piggyplanner.services.account.domain.model.RecordType
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
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

@SpringBootTest
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class AccountGraphQL(
        @Autowired private val testClient: WebTestClient,
        @Autowired private val accountService: AccountService
) {

    private lateinit var accountId: UUID

    @BeforeAll
    fun init() {
        accountId = accountService.createDefaultAccount(UUID.randomUUID()).get().id
    }

    @Test
    fun `verify create valid record`() {
        val mutationName = "createRecord"
        val mutation = createRecordMutation(mutationName)
        testMutation(mutation, mutationName)
    }

    @Test
    fun `verify modify record`() {
        val recordId = UUID.randomUUID()
        var mutationName = "createRecord"
        val createMutation = createRecordMutation(mutationName, recordId)
        testMutation(createMutation, mutationName)

        mutationName = "modifyRecord"
        val modifyMutation = createRecordMutation(mutationName, recordId)
        testMutation(modifyMutation, mutationName)
    }

    private fun testMutation(mutation: String, mutationName: String) {
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

    private fun createRecordMutation(mutationName: String, recordId: UUID = UUID.randomUUID()): String {
        val record = Record(RecordId(recordId), RecordType.INCOME, LocalDate.now(), BigDecimal.TEN, "Test")
        return """
               mutation {
                 $mutationName(
                   recordDTO:{
                     accountId: "$accountId"
                     recordId: "${record.recordId.id}"
                     amount: ${record.amount}
                     recordType: ${record.type}
                     year: ${record.date.year}
                     month: ${record.date.monthValue}
                     day: ${record.date.dayOfMonth}
                     memo: "${record.memo}"
                   }
                 )
               }
            """.trimIndent()
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