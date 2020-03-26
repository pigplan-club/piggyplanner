package club.piggyplanner.services.account.domain.operations

import club.piggyplanner.services.account.domain.model.UserId
import org.axonframework.serialization.Revision
import java.util.*

@Revision("1.0")
data class DefaultAccountCreated(val accountId: UUID, val userId: UUID, val accountName: String)
