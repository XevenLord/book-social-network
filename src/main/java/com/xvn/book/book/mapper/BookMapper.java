package com.xvn.book.book.mapper;

import com.xvn.book.book.entity.Book;
import com.xvn.book.book.entity.BookTxnHis;
import com.xvn.common.core.dto.book.req.BookReq;
import com.xvn.common.core.dto.book.rsp.BookRsp;
import com.xvn.common.core.dto.book.rsp.BorrowedBookRsp;
import com.xvn.common.core.util.FileUtil;
import org.springframework.stereotype.Service;

@Service
public class BookMapper {

    public Book toBook(BookReq req) {
        return Book.builder()
                .id(req.id())
                .title(req.title())
                .authorName(req.authorName())
                .synopsis(req.synopsis())
                .archived(false)
                .shareable(req.shareable())
                .build();
    }

    public BookRsp toBookRsp(Book book) {
        return BookRsp.builder()
                .id(book.getId())
                .title(book.getTitle())
                .authorName(book.getAuthorName())
                .isbn(book.getIsbn())
                .synopsis(book.getSynopsis())
                .rate(book.getRate())
                .archived(book.isArchived())
                .shareable(book.isShareable())
                .owner(book.getOwner().fullName())
                // todo implement this later
                 .cover(FileUtil.readFileFromLocation(book.getBookCover()))
                .build();
    }

    public BorrowedBookRsp toBorrowedBookRsp(BookTxnHis bookTxnHis) {
        return BorrowedBookRsp.builder()
                .id(bookTxnHis.getId())
                .title(bookTxnHis.getBook().getTitle())
                .authorName(bookTxnHis.getBook().getAuthorName())
                .isbn(bookTxnHis.getBook().getIsbn())
                .rate(bookTxnHis.getBook().getRate())
                .returned(bookTxnHis.isReturned())
                .returnApproved(bookTxnHis.isReturnApproved())
                .build();
    }
}
