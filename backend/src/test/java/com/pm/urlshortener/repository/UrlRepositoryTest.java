package com.pm.urlshortener.repository;

import com.pm.urlshortener.entity.UrlMapping;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;

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

    @Test
    void findByExpiryDateBefore_shouldReturnOnlyExpiredUrls() {
        UrlMapping expired = urlRepository.save(UrlMapping.builder()
                .originalUrl("https://expired.example.com")
                .shortCode("exp111")
                .createdDate(LocalDateTime.now().minusDays(5))
                .expiryDate(LocalDateTime.now().minusDays(1))
                .clickCount(0L)
                .build());
        urlRepository.save(UrlMapping.builder()
                .originalUrl("https://active.example.com")
                .shortCode("act111")
                .createdDate(LocalDateTime.now())
                .expiryDate(LocalDateTime.now().plusDays(5))
                .clickCount(0L)
                .build());

        entityManager.flush();
        entityManager.clear();

        List<UrlMapping> expiredUrls = urlRepository.findByExpiryDateBefore(LocalDateTime.now());
        assertEquals(1, expiredUrls.size());
        assertEquals(expired.getShortCode(), expiredUrls.get(0).getShortCode());
    }

    @Test
    void deleteByExpiryDateBefore_shouldDeleteOnlyExpiredUrls() {
        urlRepository.save(UrlMapping.builder()
                .originalUrl("https://expired-1.example.com")
                .shortCode("exp001")
                .createdDate(LocalDateTime.now().minusDays(2))
                .expiryDate(LocalDateTime.now().minusHours(1))
                .clickCount(0L)
                .build());
        urlRepository.save(UrlMapping.builder()
                .originalUrl("https://active-1.example.com")
                .shortCode("act001")
                .createdDate(LocalDateTime.now())
                .expiryDate(LocalDateTime.now().plusHours(1))
                .clickCount(0L)
                .build());

        entityManager.flush();

        long deleted = urlRepository.deleteByExpiryDateBefore(LocalDateTime.now());
        entityManager.flush();
        entityManager.clear();

        assertEquals(1, deleted);
        assertFalse(urlRepository.findByShortCode("exp001").isPresent());
        assertTrue(urlRepository.findByShortCode("act001").isPresent());
    }
}
