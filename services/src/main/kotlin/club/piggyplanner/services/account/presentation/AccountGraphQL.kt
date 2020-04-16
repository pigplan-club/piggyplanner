package club.piggyplanner.services.account.presentation

import club.piggyplanner.services.account.application.AccountService
import com.expediagroup.graphql.annotations.GraphQLDescription
import com.expediagroup.graphql.spring.operations.Mutation
import com.expediagroup.graphql.spring.operations.Query
import org.springframework.stereotype.Component
import java.util.*

@Component
class Mutations(private val accountService: AccountService) : Mutation {

    @GraphQLDescription("Create new Record")
    fun createRecord(recordDTO: RecordDTO) =
            accountService.createRecord(recordDTO)

    @GraphQLDescription("Modify an existing Record")
    fun modifyRecord(recordDTO: RecordDTO) =
            accountService.modifyRecord(recordDTO)


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
