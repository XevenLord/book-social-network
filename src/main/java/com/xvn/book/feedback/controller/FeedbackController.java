package com.xvn.book.feedback.controller;

import com.xvn.book.feedback.service.FeedbackSrv;
import com.xvn.common.core.dto.PageDto;
import com.xvn.common.core.dto.feedback.req.FeedbackReq;
import com.xvn.common.core.dto.feedback.rsp.FeedbackRsp;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/feedbacks")
@RequiredArgsConstructor
@Tag(name = "Feedbacks")
public class FeedbackController {

    private final FeedbackSrv feedbackSrv;

    @PostMapping
    public ResponseEntity<Integer> saveFeedback(
            @Valid @RequestBody FeedbackReq req,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(feedbackSrv.save(req, connectedUser));
    }

    @GetMapping("/book/{book-id}")
    public ResponseEntity<PageDto<FeedbackRsp>> findAllFeedbackByBook(
            @PathVariable("book-id") Integer bookId,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(feedbackSrv.findAllFeedbacksByBook(bookId, page, size, connectedUser));
    }

}
