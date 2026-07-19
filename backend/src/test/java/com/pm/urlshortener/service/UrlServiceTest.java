package com.pm.urlshortener.service;

import com.pm.urlshortener.dto.UrlAnalyticsDto;
import com.pm.urlshortener.dto.UrlRequestDto;
import com.pm.urlshortener.dto.UrlResponseDto;
import com.pm.urlshortener.entity.UrlMapping;
import com.pm.urlshortener.exception.InvalidUrlException;
import com.pm.urlshortener.exception.UrlExpiredException;
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
    void testCreateShortUrl_WithExpiryDate_Success() {
        LocalDateTime futureExpiry = LocalDateTime.now().plusDays(3);
        urlRequest.setExpiryDate(futureExpiry);
        when(urlRepository.existsByShortCode(anyString())).thenReturn(false);
        UrlMapping mappingWithExpiry = UrlMapping.builder()
                .id(1L)
                .originalUrl("https://www.example.com")
                .shortCode("abc123")
                .createdDate(LocalDateTime.now())
                .expiryDate(futureExpiry)
                .clickCount(0L)
                .build();
        when(urlRepository.save(any(UrlMapping.class))).thenReturn(mappingWithExpiry);

        UrlResponseDto response = urlService.createShortUrl(urlRequest);

        assertNotNull(response.getExpiryDate());
        assertEquals(futureExpiry, response.getExpiryDate());
    }

    @Test
    void testCreateShortUrl_WithPastExpiryDate_Fails() {
        urlRequest.setExpiryDate(LocalDateTime.now().minusMinutes(1));
        InvalidUrlException ex = assertThrows(InvalidUrlException.class, () -> urlService.createShortUrl(urlRequest));
        assertEquals("expiryDate must be a future date-time", ex.getMessage());
    }

    @Test
    void testCreateShortUrl_WithCurrentTimeExpiryDate_Fails() {
        urlRequest.setExpiryDate(LocalDateTime.now());
        InvalidUrlException ex = assertThrows(InvalidUrlException.class, () -> urlService.createShortUrl(urlRequest));
        assertEquals("expiryDate must be a future date-time", ex.getMessage());
    }

    @Test
    void testCreateShortUrl_AutoNormalize_NoDomain() {
        // google.com → https://google.com — should succeed
        urlRequest.setOriginalUrl("google.com");
        when(urlRepository.existsByShortCode(anyString())).thenReturn(false);
        UrlMapping normalizedMapping = UrlMapping.builder()
                .id(1L).originalUrl("https://google.com").shortCode("abc123")
                .createdDate(LocalDateTime.now()).clickCount(0L).build();
        when(urlRepository.save(any(UrlMapping.class))).thenReturn(normalizedMapping);

        UrlResponseDto response = urlService.createShortUrl(urlRequest);

        assertNotNull(response);
        assertEquals("https://google.com", response.getOriginalUrl());
    }

    @Test
    void testCreateShortUrl_AutoNormalize_WithPath() {
        // example.com/path?q=1 → https://example.com/path?q=1
        urlRequest.setOriginalUrl("example.com/path?q=1");
        when(urlRepository.existsByShortCode(anyString())).thenReturn(false);
        UrlMapping normalizedMapping = UrlMapping.builder()
                .id(1L).originalUrl("https://example.com/path?q=1").shortCode("abc123")
                .createdDate(LocalDateTime.now()).clickCount(0L).build();
        when(urlRepository.save(any(UrlMapping.class))).thenReturn(normalizedMapping);

        UrlResponseDto response = urlService.createShortUrl(urlRequest);

        assertEquals("https://example.com/path?q=1", response.getOriginalUrl());
    }

    @Test
    void testCreateShortUrl_InvalidUrl_FtpProtocol() {
        // Explicit unsupported protocol is still rejected
        urlRequest.setOriginalUrl("ftp://example.com/file.txt");

        InvalidUrlException ex = assertThrows(InvalidUrlException.class, () -> urlService.createShortUrl(urlRequest));
        assertEquals("URL must start with http:// or https://", ex.getMessage());
    }

    @Test
    void testCreateShortUrl_InvalidUrl_FileProtocol() {
        // Security: file:// must never be accepted
        urlRequest.setOriginalUrl("file:///etc/passwd");

        InvalidUrlException ex = assertThrows(InvalidUrlException.class, () -> urlService.createShortUrl(urlRequest));
        assertEquals("URL must start with http:// or https://", ex.getMessage());
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
    void testCreateShortUrl_Boundary_MaxLength() {
        urlRequest.setOriginalUrl(buildUrlOfLength(2048));
        when(urlRepository.existsByShortCode(anyString())).thenReturn(false);
        when(urlRepository.save(any(UrlMapping.class))).thenReturn(urlMapping);

        UrlResponseDto response = urlService.createShortUrl(urlRequest);

        assertNotNull(response);
        verify(urlRepository, times(1)).save(any(UrlMapping.class));
    }

    @Test
    void testCreateShortUrl_RepositoryFailure() {
        when(urlRepository.existsByShortCode(anyString())).thenReturn(false);
        when(urlRepository.save(any(UrlMapping.class))).thenThrow(new RuntimeException("DB failure"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> urlService.createShortUrl(urlRequest));

        assertEquals("DB failure", exception.getMessage());
    }

    @Test
    void testGetUrlByShortCode_Success() {
        urlMapping.setExpiryDate(LocalDateTime.now().plusHours(3));
        when(urlRepository.findByShortCode("abc123")).thenReturn(Optional.of(urlMapping));

        UrlResponseDto response = urlService.getUrlByShortCode("abc123");

        assertNotNull(response);
        assertEquals(urlMapping.getShortCode(), response.getShortCode());
        assertEquals(urlMapping.getOriginalUrl(), response.getOriginalUrl());
    }

    @Test
    void testGetAnalytics_Success() {
        urlMapping.setClickCount(15L);
        when(urlRepository.findByShortCode("abc123")).thenReturn(Optional.of(urlMapping));

        UrlAnalyticsDto analytics = urlService.getAnalytics("abc123");

        assertNotNull(analytics);
        assertEquals("https://www.example.com", analytics.getOriginalUrl());
        assertEquals("abc123", analytics.getShortCode());
        assertEquals(15L, analytics.getClickCount());
        assertNotNull(analytics.getCreatedDate());
        assertNull(analytics.getExpiryDate());
    }

    @Test
    void testGetAnalytics_NotFound() {
        when(urlRepository.findByShortCode("missing")).thenReturn(Optional.empty());

        assertThrows(UrlNotFoundException.class, () -> urlService.getAnalytics("missing"));
    }

    @Test
    void testGetAnalytics_Expired() {
        urlMapping.setExpiryDate(LocalDateTime.now().minusDays(1));
        when(urlRepository.findByShortCode("abc123")).thenReturn(Optional.of(urlMapping));

        assertThrows(UrlExpiredException.class, () -> urlService.getAnalytics("abc123"));
    }

    @Test
    void testGetAnalytics_DoesNotIncrementClickCount() {
        urlMapping.setClickCount(5L);
        when(urlRepository.findByShortCode("abc123")).thenReturn(Optional.of(urlMapping));

        urlService.getAnalytics("abc123");

        verify(urlRepository, never()).incrementClickCount(anyLong());
    }

    @Test
    void testGetAnalytics_RepositoryFailure() {
        when(urlRepository.findByShortCode("abc123")).thenThrow(new RuntimeException("DB failure"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> urlService.getAnalytics("abc123"));

        assertEquals("DB failure", exception.getMessage());
    }

    @Test
    void testGetUrlByShortCode_NotFound() {
        when(urlRepository.findByShortCode("invalid")).thenReturn(Optional.empty());

        assertThrows(UrlNotFoundException.class, () -> urlService.getUrlByShortCode("invalid"));
    }

    @Test
    void testGetUrlByShortCode_RepositoryFailure() {
        when(urlRepository.findByShortCode("abc123")).thenThrow(new RuntimeException("DB failure"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> urlService.getUrlByShortCode("abc123"));

        assertEquals("DB failure", exception.getMessage());
    }

    @Test
    void testGetUrlByShortCode_Expired() {
        urlMapping.setExpiryDate(LocalDateTime.now().minusDays(1));
        when(urlRepository.findByShortCode("abc123")).thenReturn(Optional.of(urlMapping));

        assertThrows(UrlExpiredException.class, () -> urlService.getUrlByShortCode("abc123"));
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
    void testGetOriginalUrl_RepositoryFailure() {
        when(urlRepository.findByShortCode("abc123")).thenThrow(new RuntimeException("DB failure"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> urlService.getOriginalUrl("abc123"));

        assertEquals("DB failure", exception.getMessage());
    }

    @Test
    void testGetOriginalUrl_Expired() {
        urlMapping.setExpiryDate(LocalDateTime.now().minusMinutes(1));
        when(urlRepository.findByShortCode("abc123")).thenReturn(Optional.of(urlMapping));

        assertThrows(UrlExpiredException.class, () -> urlService.getOriginalUrl("abc123"));
        verify(urlRepository, never()).incrementClickCount(anyLong());
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
    void testUpdateUrl_WithExpiryDate_Success() {
        LocalDateTime futureExpiry = LocalDateTime.now().plusDays(5);
        urlRequest.setExpiryDate(futureExpiry);
        urlMapping.setExpiryDate(futureExpiry);
        when(urlRepository.findById(1L)).thenReturn(Optional.of(urlMapping));
        when(urlRepository.save(any(UrlMapping.class))).thenReturn(urlMapping);

        UrlResponseDto response = urlService.updateUrl(1L, urlRequest);

        assertEquals(futureExpiry, response.getExpiryDate());
    }

    @Test
    void testUpdateUrl_WithPastExpiryDate_Fails() {
        urlRequest.setExpiryDate(LocalDateTime.now().minusSeconds(1));
        when(urlRepository.findById(1L)).thenReturn(Optional.of(urlMapping));

        InvalidUrlException ex = assertThrows(InvalidUrlException.class, () -> urlService.updateUrl(1L, urlRequest));
        assertEquals("expiryDate must be a future date-time", ex.getMessage());
        verify(urlRepository, never()).save(any(UrlMapping.class));
    }

    @Test
    void testUpdateUrl_WithNullExpiryDate_Success() {
        urlRequest.setExpiryDate(null);
        urlMapping.setExpiryDate(null);
        when(urlRepository.findById(1L)).thenReturn(Optional.of(urlMapping));
        when(urlRepository.save(any(UrlMapping.class))).thenReturn(urlMapping);

        UrlResponseDto response = urlService.updateUrl(1L, urlRequest);

        assertNull(response.getExpiryDate());
    }

    @Test
    void testUpdateUrl_NotFound() {
        when(urlRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(UrlNotFoundException.class, () -> urlService.updateUrl(999L, urlRequest));
    }

    @Test
    void testUpdateUrl_RepositoryFailure() {
        when(urlRepository.findById(1L)).thenReturn(Optional.of(urlMapping));
        when(urlRepository.save(any(UrlMapping.class))).thenThrow(new RuntimeException("DB failure"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> urlService.updateUrl(1L, urlRequest));

        assertEquals("DB failure", exception.getMessage());
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

    @Test
    void testDeleteUrl_RepositoryFailure() {
        when(urlRepository.findById(1L)).thenReturn(Optional.of(urlMapping));
        doThrow(new RuntimeException("DB failure")).when(urlRepository).delete(urlMapping);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> urlService.deleteUrl(1L));

        assertEquals("DB failure", exception.getMessage());
    }

    private String buildUrlOfLength(int length) {
        String prefix = "https://example.com/";
        if (length <= prefix.length()) {
            return prefix.substring(0, length);
        }

        StringBuilder builder = new StringBuilder(prefix);
        while (builder.length() < length) {
            builder.append('a');
        }
        if (builder.length() > length) {
            builder.setLength(length);
        }
        return builder.toString();
    }
}
