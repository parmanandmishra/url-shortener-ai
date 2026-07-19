package com.pm.urlshortener.service;

import com.pm.urlshortener.dto.UrlRequestDto;
import com.pm.urlshortener.dto.UrlResponseDto;
import com.pm.urlshortener.entity.UrlMapping;
import com.pm.urlshortener.exception.InvalidUrlException;
import com.pm.urlshortener.exception.UrlNotFoundException;
import com.pm.urlshortener.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    @Mock
    private UrlRepository urlRepository;

    @InjectMocks
    private UrlService urlService;

    private UrlRequestDto urlRequest;
    private UrlMapping urlMapping;

    @BeforeEach
    void setUp() {
        urlRequest = UrlRequestDto.builder()
                .originalUrl("https://www.example.com")
                .build();

        urlMapping = UrlMapping.builder()
                .id(1L)
                .originalUrl("https://www.example.com")
                .shortCode("abc123")
                .createdDate(LocalDateTime.now())
                .clickCount(0L)
                .build();
    }

    @Test
    void testCreateShortUrl_Success() {
        when(urlRepository.existsByShortCode(anyString())).thenReturn(false);
        when(urlRepository.save(any(UrlMapping.class))).thenReturn(urlMapping);

        UrlResponseDto response = urlService.createShortUrl(urlRequest);

        assertNotNull(response);
        assertEquals(urlMapping.getId(), response.getId());
        assertEquals(urlMapping.getOriginalUrl(), response.getOriginalUrl());
        verify(urlRepository, times(1)).save(any(UrlMapping.class));
    }

    @Test
    void testCreateShortUrl_InvalidUrl_NoProtocol() {
        urlRequest.setOriginalUrl("example.com");

        assertThrows(InvalidUrlException.class, () -> urlService.createShortUrl(urlRequest));
    }

    @Test
    void testCreateShortUrl_InvalidUrl_Empty() {
        urlRequest.setOriginalUrl("");

        assertThrows(InvalidUrlException.class, () -> urlService.createShortUrl(urlRequest));
    }

    @Test
    void testCreateShortUrl_InvalidUrl_Null() {
        urlRequest.setOriginalUrl(null);

        assertThrows(InvalidUrlException.class, () -> urlService.createShortUrl(urlRequest));
    }

    @Test
    void testCreateShortUrl_InvalidUrl_TooLong() {
        urlRequest.setOriginalUrl("https://" + "a".repeat(2100));

        assertThrows(InvalidUrlException.class, () -> urlService.createShortUrl(urlRequest));
    }

    @Test
    void testGetUrlByShortCode_Success() {
        when(urlRepository.findByShortCode("abc123")).thenReturn(Optional.of(urlMapping));

        UrlResponseDto response = urlService.getUrlByShortCode("abc123");

        assertNotNull(response);
        assertEquals(urlMapping.getShortCode(), response.getShortCode());
        assertEquals(urlMapping.getOriginalUrl(), response.getOriginalUrl());
    }

    @Test
    void testGetUrlByShortCode_NotFound() {
        when(urlRepository.findByShortCode("invalid")).thenReturn(Optional.empty());

        assertThrows(UrlNotFoundException.class, () -> urlService.getUrlByShortCode("invalid"));
    }

    @Test
    void testGetUrlByShortCode_Expired() {
        urlMapping.setExpiryDate(LocalDateTime.now().minusDays(1));
        when(urlRepository.findByShortCode("abc123")).thenReturn(Optional.of(urlMapping));

        assertThrows(UrlNotFoundException.class, () -> urlService.getUrlByShortCode("abc123"));
    }

    @Test
    void testGetOriginalUrl_Success() {
        when(urlRepository.findByShortCode("abc123")).thenReturn(Optional.of(urlMapping));
        doNothing().when(urlRepository).incrementClickCount(anyLong());

        String originalUrl = urlService.getOriginalUrl("abc123");

        assertEquals(urlMapping.getOriginalUrl(), originalUrl);
        verify(urlRepository, times(1)).incrementClickCount(anyLong());
    }

    @Test
    void testUpdateUrl_Success() {
        when(urlRepository.findById(1L)).thenReturn(Optional.of(urlMapping));
        when(urlRepository.save(any(UrlMapping.class))).thenReturn(urlMapping);

        UrlResponseDto response = urlService.updateUrl(1L, urlRequest);

        assertNotNull(response);
        assertEquals(urlRequest.getOriginalUrl(), response.getOriginalUrl());
        verify(urlRepository, times(1)).save(any(UrlMapping.class));
    }

    @Test
    void testUpdateUrl_NotFound() {
        when(urlRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(UrlNotFoundException.class, () -> urlService.updateUrl(999L, urlRequest));
    }

    @Test
    void testDeleteUrl_Success() {
        when(urlRepository.findById(1L)).thenReturn(Optional.of(urlMapping));

        assertDoesNotThrow(() -> urlService.deleteUrl(1L));
        verify(urlRepository, times(1)).delete(urlMapping);
    }

    @Test
    void testDeleteUrl_NotFound() {
        when(urlRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(UrlNotFoundException.class, () -> urlService.deleteUrl(999L));
    }
}
