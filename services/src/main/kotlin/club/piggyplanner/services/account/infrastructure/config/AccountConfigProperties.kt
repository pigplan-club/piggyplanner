package club.piggyplanner.services.account.infrastructure.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "account")
data class AccountConfigProperties(
        val defaultAccountName: String,
        val recordsQuotaByMonth: Int,
        val categoriesQuota: Int,
        val categoryItemsQuota: Int
)