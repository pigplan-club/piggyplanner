package club.piggyplanner.services.account.domain.model

import java.util.*

class CategoryAlreadyAddedException : IllegalArgumentException("Category id duplicated")

class CategoryItemAlreadyAddedException : IllegalArgumentException("Category Item id duplicated")

class RecordAlreadyAddedException : IllegalArgumentException("Record id duplicated")

class AmountInvalidException : IllegalArgumentException("Value must be greater than 0")

class CategoryNotFoundException(val id: UUID) : Exception("Category with id $id not found")

class CategoryItemNotFoundException(val id: UUID) : Exception("Category Item with id $id not found")
