package com.xvn.book.kafka;

import com.xvn.book.email.EmailService;
import com.xvn.book.email.EmailTemplateName;
import com.xvn.common.core.event.KafkaTopics;
import com.xvn.common.core.event.UserRegisteredEvent;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserRegisteredConsumer {

    private final EmailService emailService;

    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;

    @KafkaListener(topics = KafkaTopics.USER_REGISTERED)
    public void consume(UserRegisteredEvent event) throws MessagingException {
        log.info("Processing user registered event {}", event);
        // emailService.sendEmail(
        //         event.email(),
        //         event.fullName(),
        //         EmailTemplateName.ACTIVATE_ACCOUNT,
        //         activationUrl,
        //         event.activationCode(),
        //         "Activate Account"
        // );
    }

}
