package com.crushedlemon.chess.config;

import com.crushedlemon.chess.engine.ClassicRuleEngine;
import com.crushedlemon.chess.engine.DefaultRuleEngine;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonConfig {

    @Bean
    ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }

    @Bean
    ClassicRuleEngine getClassicRuleEngine() {
        return new ClassicRuleEngine();
    }

    @Bean
    DefaultRuleEngine getDefaultRuleEngine() {
        return new DefaultRuleEngine();
    }
}
