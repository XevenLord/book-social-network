package com.xvn.book.feedback.mapper;

import com.xvn.book.book.entity.Book;
import com.xvn.book.feedback.entity.Feedback;
import com.xvn.common.core.dto.feedback.req.FeedbackReq;
import com.xvn.common.core.dto.feedback.rsp.FeedbackRsp;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class FeedbackMapper {

    public Feedback toFeedback(FeedbackReq req) {
        return Feedback.builder()
                .note(req.note())
                .comment(req.comment())
                .book(Book.builder()
                        .id(req.bookId())
                        .archived(false)
                        .shareable(false)
                        .build())
                .build();
    }

    public FeedbackRsp toFeedbackRsp(Feedback feedback, Integer userId) {
        return FeedbackRsp.builder()
                .note(feedback.getNote())
                .comment(feedback.getComment())
                .ownFeedback(Objects.equals(feedback.getCreatedBy(), userId))
                .build();
    }
}
