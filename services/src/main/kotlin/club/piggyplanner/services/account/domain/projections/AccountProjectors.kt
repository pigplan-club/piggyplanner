package club.piggyplanner.services.account.domain.projections

import club.piggyplanner.services.account.domain.model.CategoryItemNotFoundException
import club.piggyplanner.services.account.domain.operations.FetchCategoryItem
import org.axonframework.queryhandling.QueryHandler
import org.springframework.stereotype.Component

@Component
class Aggregate1Projector(private val categoryItemRepository: CategoryItemRepository) {

    @QueryHandler
    fun handle(query: FetchCategoryItem): CategoryItemProjection =
            categoryItemRepository.findById(query.categoryItemId).orElseThrow(CategoryItemNotFoundException(query.categoryItemId))
}