package club.piggyplanner.services.account.domain.model

import java.util.*
import java.util.function.Supplier

class RecordAlreadyAddedException : IllegalArgumentException("Record id duplicated")

class AmountInvalidException : IllegalArgumentException("Value must be greater than 0")

class CategoryItemNotFoundException(val id: UUID) : Supplier<Exception> {
    override fun get(): Exception {
        return Exception("Category Item with id $id not found")
    }
}
