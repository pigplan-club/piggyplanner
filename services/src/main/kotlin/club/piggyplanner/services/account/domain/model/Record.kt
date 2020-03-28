package club.piggyplanner.services.account.domain.model

import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

data class Record(val id: RecordId = RecordId(UUID.randomUUID()),
                  val type: RecordType,
                  val dateTime: LocalDate,
                  val value: BigDecimal,
                  val memo: String? = "")