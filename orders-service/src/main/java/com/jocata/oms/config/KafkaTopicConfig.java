package com.jocata.oms.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic orderTopic(){
        return TopicBuilder.name("order-topic").build();
    }

    @Bean
    public NewTopic orderTopicAfterPayment(){
        return TopicBuilder.name("order-payment-topic").build();
    }

}
