package com.pm.urlshortener.repository;

import com.pm.urlshortener.entity.UrlMapping;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UrlRepositoryTest {

    @Autowired
    private UrlRepository urlRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void saveAndFindByShortCode_shouldPersistUrlMapping() {
        UrlMapping saved = urlRepository.save(UrlMapping.builder()
                .originalUrl("https://example.com")
                .shortCode("abc123")
                .createdDate(LocalDateTime.now())
                .clickCount(0L)
                .build());

        entityManager.flush();
        entityManager.clear();

        UrlMapping found = urlRepository.findByShortCode("abc123").orElseThrow();

        assertNotNull(found.getId());
        assertEquals(saved.getOriginalUrl(), found.getOriginalUrl());
        assertEquals(saved.getShortCode(), found.getShortCode());
        assertEquals(0L, found.getClickCount());
    }

    @Test
    void existsByShortCode_shouldReturnTrueForPersistedCode() {
        urlRepository.save(UrlMapping.builder()
                .originalUrl("https://example.com/exists")
                .shortCode("xyz789")
                .createdDate(LocalDateTime.now())
                .clickCount(0L)
                .build());

        entityManager.flush();
        entityManager.clear();

        assertTrue(urlRepository.existsByShortCode("xyz789"));
        assertFalse(urlRepository.existsByShortCode("missing1"));
    }

    @Test
    void incrementClickCount_shouldIncreasePersistedCounter() {
        UrlMapping saved = urlRepository.save(UrlMapping.builder()
                .originalUrl("https://example.com/click")
                .shortCode("clk123")
                .createdDate(LocalDateTime.now())
                .clickCount(0L)
                .build());

        entityManager.flush();

        urlRepository.incrementClickCount(saved.getId());
        entityManager.flush();
        entityManager.clear();

        UrlMapping updated = urlRepository.findById(saved.getId()).orElseThrow();
        assertEquals(1L, updated.getClickCount());
    }
}
