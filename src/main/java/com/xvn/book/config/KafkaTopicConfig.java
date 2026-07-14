package com.xvn.book.config;

import com.xvn.common.core.event.KafkaTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic userRegisteredTopic() {
        return TopicBuilder.name(KafkaTopics.USER_REGISTERED)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic bookBorrowedTopic() {
        return TopicBuilder.name(KafkaTopics.BOOK_BORROWED)
                .partitions(1)
                .replicas(1)
                .build();
    }

}
