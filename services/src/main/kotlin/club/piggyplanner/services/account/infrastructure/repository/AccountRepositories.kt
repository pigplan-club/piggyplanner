package club.piggyplanner.services.account.infrastructure.repository

import org.springframework.data.repository.CrudRepository
import java.util.*

interface AccountStore : CrudRepository<AccountProjection, UUID>

interface RecordStore : CrudRepository<RecordProjection, UUID>

interface CategoryStore : CrudRepository<CategoryProjection, UUID>

interface CategoryItemStore : CrudRepository<CategoryItemProjection, UUID>