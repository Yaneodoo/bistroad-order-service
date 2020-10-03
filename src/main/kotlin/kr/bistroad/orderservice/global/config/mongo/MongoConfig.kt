package kr.bistroad.orderservice.global.config.mongo

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.mongodb.core.convert.MongoCustomConversions

@Configuration
class MongoConfig {
    @Bean
    fun customConversions() = MongoCustomConversions(
        listOf<Converter<*, *>>(
            OffsetDateTimeConverter.OffsetDateTimeWriter(),
            OffsetDateTimeConverter.OffsetDateTimeReader()
        )
    )
}