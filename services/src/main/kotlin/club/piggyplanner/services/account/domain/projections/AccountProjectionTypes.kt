package club.piggyplanner.services.account.domain.projections

import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document
data class CategoryItemProjection(val id: UUID,
                                  val name: String,
                                  val category: CategoryProjection)

@Document
data class CategoryProjection(val id: UUID,
                              val name: String)