package com.gggsl.config.serialize;
 import com.fasterxml.jackson.core.JsonGenerator;
 import com.fasterxml.jackson.core.JsonParser;
 import com.fasterxml.jackson.databind.DeserializationContext;
 import com.fasterxml.jackson.databind.JsonDeserializer;
 import com.fasterxml.jackson.databind.JsonSerializer;
 import com.fasterxml.jackson.databind.SerializerProvider;
 import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
 import com.gggsl.common.utils.Dates;
 import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
 import org.springframework.boot.jackson.JsonComponent;
 import org.springframework.context.annotation.Bean;

 import java.io.IOException;
 import java.time.Instant;
 import java.time.LocalDateTime;
 import java.time.format.DateTimeFormatter;

@JsonComponent
 public class LocalDateTimeSerializers {

     private LocalDateTimeSerializers() {
         super();
      }
     public static class LocalDateTimeSerializer extends JsonSerializer<LocalDateTime>{


         @Override
         public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers)
                 throws IOException {
             Instant instant = value.atZone(Dates.CHINA_ZONE).toInstant();
             Long epochMilli = instant.toEpochMilli();
             gen.writeString(epochMilli.toString());

         }}
     public static class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime>{


         @Override
         public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt)
                 throws IOException  {

             String text = p.getText();
             if (text.isEmpty()) {
                 return null;
             }
             Instant instant = Instant.ofEpochMilli(Long.parseLong(text));

             return LocalDateTime.ofInstant(instant, Dates.CHINA_ZONE);
         }

     }

     //@Value("${spring.jackson.date-format:yyyy-MM-dd HH:mm:ss}")
     /*private String pattern = "yyyy-MM-dd HH:mm";

     // 方案一
     @Bean
     public LocalDateTimeSerializer localDateTimeDeserializer() {
         return new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(pattern));
     }

     @Bean
     public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
         return builder -> builder.serializerByType(LocalDateTime.class, localDateTimeDeserializer());
     }*/
 }
