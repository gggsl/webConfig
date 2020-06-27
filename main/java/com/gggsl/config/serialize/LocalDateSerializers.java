package com.gggsl.config.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.gggsl.common.utils.Dates;
import org.springframework.boot.jackson.JsonComponent;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;


@JsonComponent
public class LocalDateSerializers {

    private LocalDateSerializers() {
        super();
    }

    public static class LocalDateSerializer extends JsonSerializer<LocalDate> {

        @Override
        public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            Instant instant = value.atStartOfDay().atZone(Dates.CHINA_ZONE).toInstant();
            Long epochMilli = instant.toEpochMilli();
            gen.writeString(epochMilli.toString());
        }
    }

    public static class LocalDateDeserializer extends JsonDeserializer<LocalDate> {
        @Override
        public LocalDate deserialize(JsonParser p, DeserializationContext ctxt)
                throws IOException  {

            String text = p.getText();
            if (text.isEmpty()) {
                return null;
            }
            Instant instant = Instant.ofEpochMilli(Long.parseLong(text));
            return LocalDateTime.ofInstant(instant, Dates.CHINA_ZONE).toLocalDate();
        }

    }
}

