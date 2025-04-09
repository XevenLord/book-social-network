package com.xvn.book.book.entity;

import com.xvn.book.feedback.entity.Feedback;
import com.xvn.book.user.entity.User;
import com.xvn.common.multidb.base.BaseEntt;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Book extends BaseEntt {

    private String title;
    private String authorName;
    private String isbn;
    private String synopsis;
    private String bookCover;
    private boolean archived;
    private boolean shareable;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @ToString.Exclude
    @OneToMany(mappedBy = "book")
    private List<Feedback> feedbacks;

    @ToString.Exclude
    @OneToMany(mappedBy = "book")
    private List<BookTxnHis> histories;

    @Transient
    public double getRate() {
        if (feedbacks == null || feedbacks.isEmpty()) {
            return 0.0;
        }
        var rate = this.feedbacks.stream()
                .mapToDouble(Feedback::getNote)
                .average()
                .orElse(0.0);
        double rndRate = Math.round(rate * 10.0) / 10.0;
        return rndRate;
    }

}
