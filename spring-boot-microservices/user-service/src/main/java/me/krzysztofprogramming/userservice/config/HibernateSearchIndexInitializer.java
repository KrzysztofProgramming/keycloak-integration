package me.krzysztofprogramming.userservice.config;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

@Configuration
@AllArgsConstructor
@Slf4j
public class HibernateSearchIndexInitializer implements ApplicationListener<ApplicationReadyEvent> {

    private final EntityManager entityManager;

    @Override
    @Transactional
    @EventListener
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.debug("Starting indexing");

        SearchSession searchSession = Search.session(entityManager);
        try {
            searchSession.massIndexer().startAndWait();
        } catch (InterruptedException e) {
            log.warn("Indexing was interrupted by {}", e.getMessage());
        }

        log.debug("Indexing finished");
    }
}
