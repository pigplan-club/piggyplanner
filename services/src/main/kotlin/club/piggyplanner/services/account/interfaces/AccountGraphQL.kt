package club.piggyplanner.services.account.interfaces

import club.piggyplanner.services.account.domain.model.RecordType
import club.piggyplanner.services.account.domain.services.AccountService
import com.expediagroup.graphql.annotations.GraphQLDescription
import com.expediagroup.graphql.spring.operations.Mutation
import com.expediagroup.graphql.spring.operations.Query
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

@Component
class Mutations(private val accountService: AccountService) : Mutation {

    @GraphQLDescription("Create new Record")
    fun createRecord(accountId: UUID, recordType: RecordType, year: Int, month: Int, day: Int, value: BigDecimal, memo: String) =
            accountService.createRecord(accountId, recordType, LocalDate.of(year, month, day), value, memo)

    //TODO: remove it
    @GraphQLDescription("Create default Account to test")
    fun createDefaultAccount() =
            accountService.createDefaultAccount(UUID.randomUUID())
}

@Component
class Queries : Query {

    //TODO: remove it
    @GraphQLDescription("For testing, remove it after a real query is created")
    fun generateRandomUUID() = UUID.randomUUID()
}
