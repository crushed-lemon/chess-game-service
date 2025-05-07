package com.crushedlemon.chess.config;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DbConfig {

    @Bean
    DynamoDB provideDynamoDb() {
        return new DynamoDB(AmazonDynamoDBClientBuilder.standard().build());
    }
}
