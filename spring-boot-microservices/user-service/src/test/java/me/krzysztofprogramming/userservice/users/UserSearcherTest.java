package me.krzysztofprogramming.userservice.users;

import me.krzysztofprogramming.userservice.DatabaseStorageUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserSearcherTest {

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    @Transactional
    public void initDatabase() {
        DatabaseStorageUtil.initDatabase(entityManager);
    }

    @Test
    public void shouldFindUsersByKeyword() {

    }
}
