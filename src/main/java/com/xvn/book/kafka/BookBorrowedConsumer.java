package com.xvn.book.kafka;

import com.xvn.common.core.event.BookBorrowedEvent;
import com.xvn.common.core.event.KafkaTopics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BookBorrowedConsumer {

    @KafkaListener(topics = KafkaTopics.BOOK_BORROWED)
    public void consume(BookBorrowedEvent event) {
        log.info(
                "Book borrowed asynchronously: transactionId={}, bookId={}, title={}, borrowerId={}, ownerId={}",
                event.transactionId(),
                event.bookId(),
                event.bookTitle(),
                event.borrowerId(),
                event.ownerId()
        );
    }

}
