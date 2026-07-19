package com.pm.urlshortener.service;

import com.pm.urlshortener.dto.UrlRequestDto;
import com.pm.urlshortener.dto.UrlResponseDto;
import com.pm.urlshortener.entity.UrlMapping;
import com.pm.urlshortener.exception.InvalidUrlException;
import com.pm.urlshortener.exception.UrlNotFoundException;
import com.pm.urlshortener.repository.UrlRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class UrlService {

    private static final Logger logger = LoggerFactory.getLogger(UrlService.class);
    private static final String SHORT_CODE_ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int SHORT_CODE_LENGTH = 6;
    private static final int MAX_URL_LENGTH = 2048;
    private static final int MAX_RETRIES = 5;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final UrlRepository urlRepository;

    public UrlService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    public UrlResponseDto createShortUrl(UrlRequestDto request) {
        validateUrl(request.getOriginalUrl());

        String shortCode = generateUniqueShortCode();

        UrlMapping urlMapping = UrlMapping.builder()
                .originalUrl(request.getOriginalUrl())
                .shortCode(shortCode)
                .createdDate(LocalDateTime.now())
                .clickCount(0L)
                .build();

        UrlMapping savedUrl = urlRepository.save(urlMapping);
        logger.info("Created short URL with code: {} for URL: {}", shortCode, maskUrl(request.getOriginalUrl()));
        return mapToResponseDto(savedUrl);
    }

    @Transactional(readOnly = true)
    public UrlResponseDto getUrlByShortCode(String shortCode) {
        UrlMapping urlMapping = getAndValidateUrlByShortCode(shortCode);
        return mapToResponseDto(urlMapping);
    }

    @Transactional
    public String getOriginalUrl(String shortCode) {
        UrlMapping urlMapping = getAndValidateUrlByShortCode(shortCode);
        
        urlRepository.incrementClickCount(urlMapping.getId());
        logger.info("Redirecting short code: {} click count incremented", shortCode);
        
        return urlMapping.getOriginalUrl();
    }

    public UrlResponseDto updateUrl(Long id, UrlRequestDto request) {
        UrlMapping urlMapping = urlRepository.findById(id)
                .orElseThrow(() -> new UrlNotFoundException("URL not found with id: " + id));

        validateUrl(request.getOriginalUrl());
        urlMapping.setOriginalUrl(request.getOriginalUrl());

        UrlMapping updatedUrl = urlRepository.save(urlMapping);
        logger.info("Updated URL with id: {}", id);
        return mapToResponseDto(updatedUrl);
    }

    public void deleteUrl(Long id) {
        UrlMapping urlMapping = urlRepository.findById(id)
                .orElseThrow(() -> new UrlNotFoundException("URL not found with id: " + id));
        urlRepository.delete(urlMapping);
        logger.info("Deleted URL with id: {}", id);
    }

    private UrlMapping getAndValidateUrlByShortCode(String shortCode) {
        UrlMapping urlMapping = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("Short URL not found: " + shortCode));

        if (urlMapping.getExpiryDate() != null && urlMapping.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new UrlNotFoundException("Short URL has expired: " + shortCode);
        }

        return urlMapping;
    }

    private String generateUniqueShortCode() {
        int attempts = 0;
        String shortCode;

        while (attempts < MAX_RETRIES) {
            shortCode = generateRandomShortCode();
            if (!urlRepository.existsByShortCode(shortCode)) {
                logger.debug("Generated unique short code on attempt {}", attempts + 1);
                return shortCode;
            }
            attempts++;
            if (attempts < MAX_RETRIES) {
                try {
                    long backoffMillis = 10 * (1L << attempts);
                    Thread.sleep(backoffMillis);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted while generating short code", e);
                }
            }
        }

        throw new RuntimeException("Failed to generate unique short code after " + MAX_RETRIES + " attempts");
    }

    private String generateRandomShortCode() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < SHORT_CODE_LENGTH; i++) {
            int randomIndex = SECURE_RANDOM.nextInt(SHORT_CODE_ALPHABET.length());
            sb.append(SHORT_CODE_ALPHABET.charAt(randomIndex));
        }
        return sb.toString();
    }

    private void validateUrl(String urlString) {
        if (urlString == null || urlString.isBlank()) {
            throw new InvalidUrlException("URL cannot be null or empty");
        }

        if (urlString.length() > MAX_URL_LENGTH) {
            throw new InvalidUrlException("URL exceeds maximum length of " + MAX_URL_LENGTH + " characters");
        }

        try {
            new URL(urlString).toURI();
            if (!urlString.toLowerCase().startsWith("http://") && !urlString.toLowerCase().startsWith("https://")) {
                throw new InvalidUrlException("URL must start with http:// or https://");
            }
        } catch (MalformedURLException | IllegalArgumentException | URISyntaxException e) {
            throw new InvalidUrlException("Invalid URL format: " + e.getMessage());
        }
    }

    private UrlResponseDto mapToResponseDto(UrlMapping urlMapping) {
        return UrlResponseDto.builder()
                .id(urlMapping.getId())
                .originalUrl(urlMapping.getOriginalUrl())
                .shortCode(urlMapping.getShortCode())
                .createdDate(urlMapping.getCreatedDate())
                .expiryDate(urlMapping.getExpiryDate())
                .clickCount(urlMapping.getClickCount())
                .build();
    }

    private String maskUrl(String url) {
        if (url == null || url.length() < 10) {
            return "***";
        }
        return url.substring(0, 5) + "****" + url.substring(url.length() - 4);
    }
}
