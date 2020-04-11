package club.piggyplanner.services.account.application

import club.piggyplanner.services.account.domain.model.*
import club.piggyplanner.services.account.domain.operations.CreateDefaultAccount
import club.piggyplanner.services.account.domain.operations.CreateRecord
import club.piggyplanner.services.account.domain.operations.FetchCategoryItem
import club.piggyplanner.services.account.domain.operations.ModifyRecord
import club.piggyplanner.services.account.domain.projections.CategoryItemProjection
import club.piggyplanner.services.account.presentation.RecordDTO
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.axonframework.queryhandling.QueryGateway
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.CompletableFuture

@Service
class AccountService(private val commandGateway: CommandGateway,
                     private val queryGateway: QueryGateway) {

    //TODO: Remove this when the saga pattern is implemented
    fun createDefaultAccount(saverId: UUID): CompletableFuture<AccountId> {
        return commandGateway.send(CreateDefaultAccount(SaverId(saverId)))
    }

    fun createRecord(recordDTO: RecordDTO): CompletableFuture<Boolean> {
        val categoryItemProjection = queryGateway.query(FetchCategoryItem(recordDTO.categoryItemId),
                ResponseTypes.instanceOf(CategoryItemProjection::class.java)).get()

        val categoryItem = convertToCategoryItem(categoryItemProjection)

        return commandGateway.send(CreateRecord(
                recordDTO.getAccountId(),
                recordDTO.getRecordId(),
                recordDTO.recordType,
                categoryItem,
                recordDTO.getDate(),
                recordDTO.amount,
                recordDTO.memo
        ))
    }

    fun modifyRecord(recordDTO: RecordDTO): CompletableFuture<Boolean> {
        val categoryItemProjection = queryGateway.query(FetchCategoryItem(recordDTO.categoryItemId),
                ResponseTypes.instanceOf(CategoryItemProjection::class.java)).get()

        val categoryItem = convertToCategoryItem(categoryItemProjection)

        return commandGateway.send(ModifyRecord(
                recordDTO.getAccountId(),
                recordDTO.getRecordId(),
                recordDTO.recordType,
                categoryItem,
                recordDTO.getDate(),
                recordDTO.amount,
                recordDTO.memo
        ))
    }

    private fun convertToCategoryItem(categoryItemProjection: CategoryItemProjection): CategoryItem {
        return CategoryItem(
                CategoryItemId(categoryItemProjection.id),
                categoryItemProjection.name,
                Category(
                        CategoryId(categoryItemProjection.category.id),
                        categoryItemProjection.category.name)
        )
    }
}
