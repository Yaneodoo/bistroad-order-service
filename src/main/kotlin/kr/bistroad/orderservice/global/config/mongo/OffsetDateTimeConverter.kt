package kr.bistroad.orderservice.global.config.mongo

import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*

interface OffsetDateTimeConverter {
    @WritingConverter
    class OffsetDateTimeWriter : Converter<OffsetDateTime, Date> {
        override fun convert(source: OffsetDateTime): Date =
            Date.from(source.toInstant().atZone(ZONE_OFFSET).toInstant())
    }

    @ReadingConverter
    class OffsetDateTimeReader : Converter<Date, OffsetDateTime> {
        override fun convert(source: Date): OffsetDateTime =
            source.toInstant().atOffset(ZONE_OFFSET)
    }

    companion object {
        val ZONE_OFFSET: ZoneOffset = ZoneOffset.ofHours(9)
    }
}