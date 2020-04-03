package club.piggyplanner.services.account.domain.model

import java.lang.IllegalArgumentException

class RecordAlreadyAddedException : IllegalArgumentException("Record id duplicated")

class AmountInvalidException : IllegalArgumentException("Value must be greater than 0")
