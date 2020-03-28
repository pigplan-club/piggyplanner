package club.piggyplanner.services.account.domain.operations

import club.piggyplanner.services.account.domain.model.AccountId
import club.piggyplanner.services.account.domain.model.Record
import club.piggyplanner.services.account.domain.model.UserId
import org.axonframework.serialization.Revision

@Revision("1.0")
data class DefaultAccountCreated(val accountId: AccountId,
                                 val userId: UserId,
                                 val accountName: String)

@Revision("1.0")
data class RecordCreated(val accountId: AccountId,
                         val record: Record)
