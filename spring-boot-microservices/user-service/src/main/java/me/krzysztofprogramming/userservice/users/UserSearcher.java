package me.krzysztofprogramming.userservice.users;

import lombok.AllArgsConstructor;
import me.krzysztofprogramming.userservice.users.models.UserEntity;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

@Service
@AllArgsConstructor
public class UserSearcher {

    private final EntityManager entityManager;

    @Transactional
    public Page<UserEntity> searchUserByKeyWord(String phrase, Pageable pageable) {
        SearchSession searchSession = Search.session(entityManager);

        SearchResult<UserEntity> result = searchSession.search(UserEntity.class)
                .where(f -> f.match()
                        .fields("username", "email", "firstname", "lastname")
                        .matching(phrase.toLowerCase())
                )
                .sort(f -> f.field("username").asc())
                .fetch(Math.toIntExact(pageable.getOffset()), pageable.getPageSize());

        return new PageImpl<>(result.hits(), pageable, result.total().hitCount());
    }
}
