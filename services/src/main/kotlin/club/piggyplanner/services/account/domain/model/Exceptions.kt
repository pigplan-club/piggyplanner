package club.piggyplanner.services.account.domain.model

import java.util.*

class CategoryAlreadyAddedException : IllegalArgumentException("Category duplicated")

class CategoryItemAlreadyAddedException : IllegalArgumentException("Category Item duplicated")

class AmountInvalidException : IllegalArgumentException("Amount must be greater than 0")

class CategoryNotFoundException(id: UUID) : Exception("Category with id $id not found")

class CategoryItemNotFoundException(id: UUID) : Exception("Category Item with id $id not found")

class CategoriesQuotaExceededException : Exception("Categories quota exceeded")

class CategoryItemsQuotaExceededException : Exception("Category items quota exceeded")

class RecordAlreadyAddedException : IllegalArgumentException("Record duplicated")

class RecordNotFoundException(id: UUID): IllegalArgumentException("Record with id $id not found")

class RecordsQuotaExceededException : Exception("Records quota exceeded")
