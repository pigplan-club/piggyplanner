package club.piggyplanner.services.account.domain.projections

import org.springframework.data.repository.CrudRepository
import java.util.*

interface CategoryItemRepository : CrudRepository<CategoryItemProjection, UUID>