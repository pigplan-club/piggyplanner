package club.piggyplanner.services.account.domain.model

import java.math.BigDecimal
import java.util.*

data class SaverId(val id: UUID)

data class AccountId(val id: UUID)

data class CategoryId(val id: UUID)

data class CategoryItemId(val id: UUID)

data class RecordId(val id: UUID)

data class RecordAmount(val value: BigDecimal){
    init {
        if (value <= BigDecimal.ZERO) {
            throw AmountInvalidException()
        }
    }
}