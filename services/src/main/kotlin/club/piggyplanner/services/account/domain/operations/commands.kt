package club.piggyplanner.services.account.domain.operations

import club.piggyplanner.services.account.domain.model.AccountId
import club.piggyplanner.services.account.domain.model.Record
import club.piggyplanner.services.account.domain.model.UserId
import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.*

data class CreateDefaultAccount(val userId: UserId){
    @TargetAggregateIdentifier
    val accountId: AccountId = AccountId(UUID.randomUUID())
    val records : List<Record> = emptyList()
}

data class CreateRecord(@TargetAggregateIdentifier val accountId: AccountId,
                        val record : Record)