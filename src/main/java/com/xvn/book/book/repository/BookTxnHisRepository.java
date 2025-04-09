package com.xvn.book.book.repository;

import com.xvn.book.book.entity.BookTxnHis;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BookTxnHisRepository extends JpaRepository<BookTxnHis, Integer> {

    @Query("""
            SELECT history
            FROM BookTxnHis history
            WHERE history.user.id = :userId
            """)
    Page<BookTxnHis> findAllBorrowedBooks(Pageable pageable, Integer userId);

    @Query("""
            SELECT history
            FROM BookTxnHis history
            WHERE history.book.owner.id = :userId
            """)
    Page<BookTxnHis> findAllReturnedBooks(Pageable pageable, Integer userId);

    @Query("""
            SELECT (COUNT(*) > 0) AS isBorrowed
            FROM BookTxnHis history
            WHERE history.user.id = :userId
            AND history.book.id = :bookId
            AND history.returnApproved = false
            """)
    boolean isAdyBorrowedByUser(Integer bookId, Integer userId);

    @Query("""
            SELECT his
            FROM BookTxnHis his
            WHERE his.user.id = :userId
            AND his.book.id = :bookId
            AND his.returned = false
            AND his.returnApproved = false
            """)
    Optional<BookTxnHis> findByBookIdAndUserId(Integer bookId, Integer userId);

    @Query("""
            SELECT his
            FROM BookTxnHis his
            WHERE his.book.owner.id = :userId
            AND his.book.id = :bookId
            AND his.returned = true
            AND his.returnApproved = false
            """)
    Optional<BookTxnHis> findByBookIdAndOwnerId(Integer bookId, Integer userId);
}
