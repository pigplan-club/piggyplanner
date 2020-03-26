package club.piggyplanner.services.account.domain.operations

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.*

data class CreateDefaultAccount(@TargetAggregateIdentifier val accountId: UUID, val userId: UUID)