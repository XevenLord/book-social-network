package com.xvn.book.book.service;

import com.xvn.book.book.entity.Book;
import com.xvn.book.book.mapper.BookMapper;
import com.xvn.book.book.repository.BookRepository;
import com.xvn.book.book.repository.BookTxnHisRepository;
import com.xvn.book.book.entity.BookTxnHis;
import com.xvn.book.user.entity.User;
import com.xvn.common.core.dto.PageDto;
import com.xvn.common.core.dto.book.req.BookReq;
import com.xvn.common.core.dto.book.rsp.BookRsp;
import com.xvn.common.core.dto.book.rsp.BorrowedBookRsp;
import com.xvn.common.core.enums.SysCodeEnum;
import com.xvn.common.core.exception.ApiException;
import com.xvn.common.core.service.FileStorageSrv;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.xvn.book.common.specification.BookSpec.withOwnerId;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookSrv {

    private final BookRepository bookRepository;

    private final BookTxnHisRepository bookTxnHisRepository;

    private final BookMapper bookMapper;

    private final FileStorageSrv fileStorageSrv;

    public Integer save(BookReq req, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Book book = bookMapper.toBook(req);
        log.info("CHecking on the book :: {}", book);
        book.setOwner(user);
        return bookRepository.save(book).getId();
    }

    public BookRsp findById(Integer bookId) {
        return bookRepository.findById(bookId)
                .map(bookMapper::toBookRsp)
                .orElseThrow(() -> new EntityNotFoundException("No book found with the ID :: " + bookId));
    }

    public PageDto<BookRsp> findAllBooks(int page, int size, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAllDisplayableBooks(pageable, user.getId());
        List<BookRsp> bookRsps = books.stream()
                .map(bookMapper::toBookRsp)
                .toList();
        log.info("Checking on the bookRsps :: {}", bookRsps);
        return new PageDto<>(
                bookRsps,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }

    public PageDto<BookRsp> findAllBooksByOwner(int page, int size, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAll(withOwnerId(user.getId()), pageable);
        List<BookRsp> bookRsps = books.stream()
                .map(bookMapper::toBookRsp)
                .toList();
        return new PageDto<>(
                bookRsps,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }

    public PageDto<BorrowedBookRsp> findAllBorrowedBooks(int page, int size, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTxnHis> allBorrowedBooks = bookTxnHisRepository.findAllBorrowedBooks(pageable, user.getId());
        List<BorrowedBookRsp> bookRsps = allBorrowedBooks.stream()
                .map(bookMapper::toBorrowedBookRsp)
                .toList();
        return new PageDto<>(
                bookRsps,
                allBorrowedBooks.getNumber(),
                allBorrowedBooks.getSize(),
                allBorrowedBooks.getTotalElements(),
                allBorrowedBooks.getTotalPages(),
                allBorrowedBooks.isFirst(),
                allBorrowedBooks.isLast()
        );
    }

    public PageDto<BorrowedBookRsp> findAllReturnedBooks(int page, int size, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTxnHis> allBorrowedBooks = bookTxnHisRepository.findAllReturnedBooks(pageable, user.getId());
        List<BorrowedBookRsp> bookRsps = allBorrowedBooks.stream()
                .map(bookMapper::toBorrowedBookRsp)
                .toList();
        return new PageDto<>(
                bookRsps,
                allBorrowedBooks.getNumber(),
                allBorrowedBooks.getSize(),
                allBorrowedBooks.getTotalElements(),
                allBorrowedBooks.getTotalPages(),
                allBorrowedBooks.isFirst(),
                allBorrowedBooks.isLast()
        );
    }

    public Integer updShareableSts(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with the ID :: " + bookId));
        User user = (User) connectedUser.getPrincipal();
        if (!user.getId().equals(book.getOwner().getId())) {
            throw new ApiException(SysCodeEnum.AUTH_NOT_PERMITTED);
        }
        book.setShareable(!book.isShareable());
        bookRepository.save(book);
        return book.getId();
    }

    public Integer updArchivedSts(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with the ID :: " + bookId));
        User user = (User) connectedUser.getPrincipal();
        if (!user.getId().equals(book.getOwner().getId())) {
            throw new ApiException(SysCodeEnum.AUTH_NOT_PERMITTED);
        }
        book.setArchived(!book.isArchived());
        bookRepository.save(book);
        return book.getId();
    }

    public Integer borrowBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with the ID :: " + bookId));
        // if archived or not shareable, not for borrow
        if (book.isArchived() || !book.isShareable()) {
            throw new ApiException(SysCodeEnum.BOOK_NOT_FOR_BORROW);
        }
        // User cannot borrow his own book
        User user = (User) connectedUser.getPrincipal();
        if (user.getId().equals(book.getOwner().getId())) {
            throw new ApiException(SysCodeEnum.BOOK_IS_OWNER);
        }
        // check if book is borrowed
        final boolean isAdyBorrowed = bookTxnHisRepository.isAdyBorrowedByUser(bookId, user.getId());
        if (isAdyBorrowed) {
            throw new ApiException(SysCodeEnum.BOOK_IS_BORROWED);
        }
        BookTxnHis bookTxn = BookTxnHis.builder()
                .user(user)
                .book(book)
                .returned(false)
                .returnApproved(false)
                .build();
        return bookTxnHisRepository.save(bookTxn).getId();
    }

    public Integer returnBorrowBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with the ID :: " + bookId));
        // if archived or not shareable, not for borrow
        if (book.isArchived() || !book.isShareable()) {
            throw new ApiException(SysCodeEnum.BOOK_NOT_FOR_BORROW);
        }
        // User cannot borrow or return his own book
        User user = (User) connectedUser.getPrincipal();
        if (user.getId().equals(book.getOwner().getId())) {
            throw new ApiException(SysCodeEnum.BOOK_IS_OWNER);
        }
        BookTxnHis txnHis = bookTxnHisRepository.findByBookIdAndUserId(bookId, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("No transaction found for the book :: " + bookId + " and user :: " + user.getId()));
        txnHis.setReturned(true);
        return bookTxnHisRepository.save(txnHis).getId();
    }

    public Integer approveReturnBorrowBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with the ID :: " + bookId));
        // if archived or not shareable, not for borrow
        if (book.isArchived() || !book.isShareable()) {
            throw new ApiException(SysCodeEnum.BOOK_NOT_FOR_BORROW);
        }
        // Other User can't approve return book
        User user = (User) connectedUser.getPrincipal();
        if (!user.getId().equals(book.getOwner().getId())) {
            throw new ApiException(SysCodeEnum.BOOK_NOT_OWNER);
        }
        BookTxnHis bookTxnHis = bookTxnHisRepository.findByBookIdAndOwnerId(bookId, user.getId())
                .orElseThrow(() -> new ApiException(SysCodeEnum.BOOK_NOT_RETURNED));
        bookTxnHis.setReturnApproved(true);
        return bookTxnHisRepository.save(bookTxnHis).getId();
    }

    public void uploadBookCoverPic(Integer bookId, MultipartFile file, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with the ID :: " + bookId));
        User user = (User) connectedUser.getPrincipal();
        var bookCover = fileStorageSrv.saveFile(file, user.getId());
        book.setBookCover(bookCover);
        bookRepository.save(book);
    }

    public void deleteBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with the ID :: " + bookId));
        User user = (User) connectedUser.getPrincipal();
        if (!user.getId().equals(book.getOwner().getId())) {
            throw new ApiException(SysCodeEnum.BOOK_NOT_OWNER);
        }
        bookRepository.delete(book);
    }

}
