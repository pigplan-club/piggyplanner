package club.piggyplanner.services.core.infrastructure.config

import com.mongodb.MongoClient
import org.axonframework.eventhandling.tokenstore.TokenStore
import org.axonframework.extensions.mongo.DefaultMongoTemplate
import org.axonframework.extensions.mongo.eventsourcing.tokenstore.MongoTokenStore
import org.axonframework.serialization.json.JacksonSerializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class AxonConfig {

    @Bean
    fun tokenStore(client: MongoClient): TokenStore =
            MongoTokenStore.builder()
                    .mongoTemplate(DefaultMongoTemplate.builder().mongoDatabase(client).build())
                    .serializer(JacksonSerializer.builder().build())
                    .build()


//    @Bean
//    fun sagaStore(client: MongoClient): SagaStore<*> = MongoSagaStore.builder()
//            .mongoTemplate(DefaultMongoTemplate.builder().mongoDatabase(client).build())
//            .serializer(JacksonSerializer.builder().build())
//            .build()
}