package com.xvn.book.common.specification;

import com.xvn.book.book.entity.Book;
import com.xvn.book.book.entity.Book_;
import com.xvn.book.user.entity.User_;
import com.xvn.common.core.specification.AbstrSpec;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ObjectUtils;

public class BookSpec extends AbstrSpec {

    public static Specification<Book> withOwnerId(Integer ownerId) {
        return (root, query, criteriaBuilder) -> ObjectUtils.isEmpty(ownerId) ? null :
                criteriaBuilder.equal(root.get(Book_.owner).get(User_.id), ownerId);
    }

}
