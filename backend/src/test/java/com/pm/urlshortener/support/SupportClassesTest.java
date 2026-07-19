package com.pm.urlshortener.support;

import com.pm.urlshortener.BackendApplication;
import com.pm.urlshortener.config.SwaggerConfig;
import com.pm.urlshortener.dto.UrlAnalyticsDto;
import com.pm.urlshortener.dto.UrlRequestDto;
import com.pm.urlshortener.dto.UrlResponseDto;
import com.pm.urlshortener.entity.UrlMapping;
import com.pm.urlshortener.exception.ErrorResponse;
import com.pm.urlshortener.exception.InvalidUrlException;
import com.pm.urlshortener.exception.UrlNotFoundException;
import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;
import org.springframework.boot.CommandLineRunner;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SupportClassesTest {

    @Test
    void dtoAndEntityGettersSettersAndBuilders_shouldWork() {
        UrlRequestDto request = new UrlRequestDto();
        request.setOriginalUrl("https://example.com");
        assertEquals("https://example.com", request.getOriginalUrl());
        assertEquals("https://example.com", UrlRequestDto.builder().originalUrl("https://example.com").build().getOriginalUrl());

        UrlResponseDto response = new UrlResponseDto();
        response.setId(10L);
        response.setOriginalUrl("https://example.com");
        response.setShortCode("abc123");
        response.setCreatedDate(LocalDateTime.of(2026, 1, 1, 10, 0));
        response.setExpiryDate(LocalDateTime.of(2026, 2, 1, 10, 0));
        response.setClickCount(7L);

        assertAll(
                () -> assertEquals(10L, response.getId()),
                () -> assertEquals("https://example.com", response.getOriginalUrl()),
                () -> assertEquals("abc123", response.getShortCode()),
                () -> assertEquals(7L, response.getClickCount()),
                () -> assertNotNull(UrlResponseDto.builder()
                        .id(11L)
                        .originalUrl("https://builder.example")
                        .shortCode("xyz789")
                        .createdDate(LocalDateTime.now())
                        .clickCount(2L)
                        .build())
        );

        UrlAnalyticsDto analytics = new UrlAnalyticsDto();
        analytics.setOriginalUrl("https://example.com");
        analytics.setShortCode("abc123");
        analytics.setClickCount(15L);
        analytics.setCreatedDate(LocalDateTime.of(2026, 1, 1, 10, 0));
        analytics.setExpiryDate(LocalDateTime.of(2026, 2, 1, 10, 0));

        assertAll(
                () -> assertEquals("https://example.com", analytics.getOriginalUrl()),
                () -> assertEquals("abc123", analytics.getShortCode()),
                () -> assertEquals(15L, analytics.getClickCount()),
                () -> assertNotNull(UrlAnalyticsDto.builder()
                        .originalUrl("https://builder.example")
                        .shortCode("b12345")
                        .clickCount(3L)
                        .createdDate(LocalDateTime.now())
                        .expiryDate(LocalDateTime.now().plusDays(1))
                        .build())
        );

        UrlMapping mapping = new UrlMapping();
        mapping.setId(42L);
        mapping.setOriginalUrl("https://example.com/path");
        mapping.setShortCode("code42");
        mapping.setCreatedDate(LocalDateTime.of(2026, 1, 1, 10, 0));
        mapping.setExpiryDate(LocalDateTime.of(2026, 2, 1, 10, 0));
        mapping.setClickCount(99L);

        assertAll(
                () -> assertEquals(42L, mapping.getId()),
                () -> assertEquals("https://example.com/path", mapping.getOriginalUrl()),
                () -> assertEquals("code42", mapping.getShortCode()),
                () -> assertEquals(99L, mapping.getClickCount()),
                () -> assertNotNull(UrlMapping.builder()
                        .id(1L)
                        .originalUrl("https://builder.example")
                        .shortCode("builder")
                        .createdDate(LocalDateTime.now())
                        .expiryDate(LocalDateTime.now().plusDays(1))
                        .clickCount(5L)
                        .build())
        );

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(400);
        errorResponse.setMessage("bad request");
        errorResponse.setTimestamp(LocalDateTime.of(2026, 1, 1, 10, 0));
        errorResponse.setPath("/api/urls/shorten");

        assertAll(
                () -> assertEquals(400, errorResponse.getStatus()),
                () -> assertEquals("bad request", errorResponse.getMessage()),
                () -> assertEquals("/api/urls/shorten", errorResponse.getPath()),
                () -> assertNotNull(ErrorResponse.builder()
                        .status(404)
                        .message("not found")
                        .timestamp(LocalDateTime.now())
                        .path("/api/urls/missing")
                        .build())
        );
    }

    @Test
    void swaggerConfig_shouldBuildExpectedOpenApiMetadata() {
        OpenAPI openAPI = new SwaggerConfig().customOpenAPI();

        assertNotNull(openAPI);
        assertEquals("URL Shortener API", openAPI.getInfo().getTitle());
        assertEquals("1.0.0", openAPI.getInfo().getVersion());
        assertEquals("URL Shortener Team", openAPI.getInfo().getContact().getName());
        assertEquals("Apache 2.0", openAPI.getInfo().getLicense().getName());
    }

    @Test
    void exceptionConstructors_shouldPreserveMessageAndCause() {
        RuntimeException cause = new RuntimeException("root cause");

        InvalidUrlException invalid = new InvalidUrlException("invalid url", cause);
        UrlNotFoundException notFound = new UrlNotFoundException("missing url", cause);

        assertAll(
                () -> assertEquals("invalid url", invalid.getMessage()),
                () -> assertSame(cause, invalid.getCause()),
                () -> assertEquals("missing url", notFound.getMessage()),
                () -> assertSame(cause, notFound.getCause())
        );
    }

    @Test
    void backendApplicationCommandLineRunner_shouldPrintStartupMessages() throws Exception {
        BackendApplication application = new BackendApplication();
        CommandLineRunner runner = application.run();

        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(output));
            runner.run();
        } finally {
            System.setOut(originalOut);
        }

        String text = output.toString();
        assertTrue(text.contains("URL Shortener Application Started"));
        assertTrue(text.contains("API Documentation"));
        assertTrue(text.contains("URL Shortener is Ready to Accept Requests"));
    }
}
