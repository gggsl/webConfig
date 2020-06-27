package com.gggsl.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;

//@Profile("dev")
@EnableSwagger2
@Configuration
public class AppConfigDev {

    @Bean
    Docket swaggerUI() {

        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
                .paths(PathSelectors.any())
                .build()
                .globalOperationParameters(Arrays.asList());
    }

    @Bean
    ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("event_case")
                .description("")
                .termsOfServiceUrl("")
                .version("1.0")
                .build();

    }
}

