package club.piggyplanner.services.account.infrastructure.config

import org.axonframework.eventsourcing.EventCountSnapshotTriggerDefinition
import org.axonframework.eventsourcing.Snapshotter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AccountConfigurations {

    @Bean
    fun accountSnapshotTriggerDefinition(snapshotter: Snapshotter) =
            EventCountSnapshotTriggerDefinition(snapshotter, ACCOUNT_SNAPSHOT_THRESHOLD)

    companion object {
        const val ACCOUNT_SNAPSHOT_THRESHOLD = 3
    }
}
