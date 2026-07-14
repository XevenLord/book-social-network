package com.xvn.book.kafka;

import com.xvn.common.core.event.BookBorrowedEvent;
import com.xvn.common.core.event.KafkaTopics;
import com.xvn.common.core.event.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishUserRegistered(UserRegisteredEvent event) {
        log.info("[Kafka Start Send Event] USER_REGISTRED");
        kafkaTemplate.send(
            KafkaTopics.USER_REGISTERED,
            event.userId().toString(),
            event
        ).whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Kafka send success");
            } else {
                log.error("Kafka send failed", ex);
            }
        });
    }

    public void publishBookBorrowed(BookBorrowedEvent event) {
        kafkaTemplate.send(KafkaTopics.BOOK_BORROWED, event.transactionId().toString(), event);
    }

}
