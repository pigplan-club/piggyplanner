package club.piggyplanner.services.common.domain.model

enum class EntityState{
    ENABLED,
    DISABLED
}

open class Entity {
    var state : EntityState = EntityState.ENABLED
}