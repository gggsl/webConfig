package com.gggsl.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.Executor;

@Configuration
public class BeanConfig {

    @Bean
    Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new
                ThreadPoolTaskExecutor();
        int availableProcessors =
                Runtime.getRuntime().availableProcessors();
        executor.setCorePoolSize(availableProcessors);
        executor.setMaxPoolSize(availableProcessors * 5);
        executor.initialize();
        executor.setThreadNamePrefix("asyncTaskThread");
        return executor;
    }


}
