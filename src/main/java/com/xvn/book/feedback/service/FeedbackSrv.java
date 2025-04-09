package com.xvn.book.feedback.service;

import com.xvn.book.book.entity.Book;
import com.xvn.book.book.repository.BookRepository;
import com.xvn.book.feedback.entity.Feedback;
import com.xvn.book.feedback.mapper.FeedbackMapper;
import com.xvn.book.feedback.repository.FeedbackRepository;
import com.xvn.book.user.entity.User;
import com.xvn.common.core.dto.PageDto;
import com.xvn.common.core.dto.feedback.req.FeedbackReq;
import com.xvn.common.core.dto.feedback.rsp.FeedbackRsp;
import com.xvn.common.core.enums.SysCodeEnum;
import com.xvn.common.core.exception.ApiException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedbackSrv {

    private final BookRepository bookRepository;

    private final FeedbackRepository feedbackRepository;

    private final FeedbackMapper feedbackMapper;

    public Integer save(FeedbackReq req, Authentication connectedUser) {
        Book book = bookRepository.findById(req.bookId())
                .orElseThrow(() -> new EntityNotFoundException("No book found with the ID :: " + req.bookId()));
        if (book.isArchived() || !book.isShareable()) {
            throw new ApiException(SysCodeEnum.BOOK_IS_ARCHIVED_OR_NOT_SHAREABLE);
        }
        User user = (User) connectedUser.getPrincipal();
        if (user.getId().equals(book.getOwner().getId())) {
            throw new ApiException(SysCodeEnum.BOOK_IS_OWNER);
        }
        Feedback feedback = feedbackMapper.toFeedback(req);
        return feedbackRepository.save(feedback).getId();
    }

    public PageDto<FeedbackRsp> findAllFeedbacksByBook(Integer bookId, int page, int size, Authentication connectedUser) {
        Pageable pageable = PageRequest.of(page, size);
        User user = (User) connectedUser.getPrincipal();
        Page<Feedback> feedbacks = feedbackRepository.findAllByBookId(bookId, pageable);
        List<FeedbackRsp> feedbackRsps = feedbacks.stream()
                .map(f -> feedbackMapper.toFeedbackRsp(f, user.getId()))
                .toList();
        return new PageDto<>(
                feedbackRsps,
                feedbacks.getNumber(),
                feedbacks.getSize(),
                feedbacks.getTotalElements(),
                feedbacks.getTotalPages(),
                feedbacks.isFirst(),
                feedbacks.isLast()
        );
    }
}
