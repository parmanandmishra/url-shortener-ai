package com.pm.urlshortener.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pm.urlshortener.dto.UrlAnalyticsDto;
import com.pm.urlshortener.dto.UrlRequestDto;
import com.pm.urlshortener.dto.UrlResponseDto;
import com.pm.urlshortener.exception.GlobalExceptionHandler;
import com.pm.urlshortener.exception.UrlExpiredException;
import com.pm.urlshortener.exception.UrlNotFoundException;
import com.pm.urlshortener.repository.UrlRepository;
import com.pm.urlshortener.service.UrlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UrlControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private FakeUrlService fakeUrlService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        fakeUrlService = new FakeUrlService();

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(new UrlController(fakeUrlService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void testCreateShortUrl_Success() throws Exception {
        LocalDateTime futureExpiry = LocalDateTime.now().plusDays(2);
        mockMvc.perform(post("/api/urls/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(UrlRequestDto.builder()
                                .originalUrl("https://www.example.com")
                                .expiryDate(futureExpiry)
                                .build())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.originalUrl").value("https://www.example.com"))
                .andExpect(jsonPath("$.shortCode").value("abc123"))
                .andExpect(jsonPath("$.expiryDate").exists());
    }

    @Test
    void testCreateShortUrl_InvalidRequest() throws Exception {
        mockMvc.perform(post("/api/urls/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(UrlRequestDto.builder()
                                .originalUrl("")
                                .build())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetUrlByShortCode_Success() throws Exception {
        mockMvc.perform(get("/api/urls/abc123")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shortCode").value("abc123"))
                .andExpect(jsonPath("$.originalUrl").value("https://www.example.com"));
    }

    @Test
    void testGetUrlByShortCode_NotFound() throws Exception {
        fakeUrlService.throwShortCodeNotFound = true;

        mockMvc.perform(get("/api/urls/invalid")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetUrlByShortCode_Expired() throws Exception {
        fakeUrlService.throwShortCodeExpired = true;

        mockMvc.perform(get("/api/urls/expired1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isGone());
    }

    @Test
    void testRedirectToOriginalUrl_Success() throws Exception {
        mockMvc.perform(get("/api/urls/redirect/abc123")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("https://www.example.com"));
    }

    @Test
    void testRedirectToOriginalUrl_NotFound() throws Exception {
        fakeUrlService.throwRedirectNotFound = true;

        mockMvc.perform(get("/api/urls/redirect/invalid")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testRedirectToOriginalUrl_Expired() throws Exception {
        fakeUrlService.throwRedirectExpired = true;

        mockMvc.perform(get("/api/urls/redirect/expired1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isGone());
    }

    @Test
    void testUpdateUrl_Success() throws Exception {
        mockMvc.perform(put("/api/urls/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(UrlRequestDto.builder()
                                .originalUrl("https://www.example.com")
                                .build())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.originalUrl").value("https://www.example.com"));
    }

    @Test
    void testUpdateUrl_NotFound() throws Exception {
        fakeUrlService.throwUpdateNotFound = true;

        mockMvc.perform(put("/api/urls/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(UrlRequestDto.builder()
                                .originalUrl("https://www.example.com")
                                .build())))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateShortUrl_WithPastExpiryDate_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/urls/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(UrlRequestDto.builder()
                                .originalUrl("https://www.example.com")
                                .expiryDate(LocalDateTime.now().minusMinutes(1))
                                .build())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("expiryDate must be a future date-time"));
    }

 /*   @Test
    void testGetShortenRoute_ReturnsMethodNotAllowed() throws Exception {
        mockMvc.perform(get("/api/urls/shorten")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed());
    } */

    @Test
    void testUpdateUrl_WithPastExpiryDate_ReturnsBadRequest() throws Exception {
        mockMvc.perform(put("/api/urls/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(UrlRequestDto.builder()
                                .originalUrl("https://www.example.com/updated")
                                .expiryDate(LocalDateTime.now().minusMinutes(5))
                                .build())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("expiryDate must be a future date-time"));
    }

    @Test
    void testDeleteUrl_Success() throws Exception {
        mockMvc.perform(delete("/api/urls/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteUrl_NotFound() throws Exception {
        fakeUrlService.throwDeleteNotFound = true;

        mockMvc.perform(delete("/api/urls/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAnalytics_Success() throws Exception {
        mockMvc.perform(get("/api/urls/analytics/abc123")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.originalUrl").value("https://www.example.com"))
                .andExpect(jsonPath("$.shortCode").value("abc123"))
                .andExpect(jsonPath("$.clickCount").value(15));
    }

    @Test
    void testGetAnalytics_NotFound() throws Exception {
        fakeUrlService.throwAnalyticsNotFound = true;

        mockMvc.perform(get("/api/urls/analytics/missing")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAnalytics_Expired() throws Exception {
        fakeUrlService.throwAnalyticsExpired = true;

        mockMvc.perform(get("/api/urls/analytics/expired1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isGone());
    }

    private static class FakeUrlService extends UrlService {
        private boolean throwShortCodeNotFound;
        private boolean throwShortCodeExpired;
        private boolean throwRedirectNotFound;
        private boolean throwRedirectExpired;
        private boolean throwUpdateNotFound;
        private boolean throwDeleteNotFound;
        private boolean throwAnalyticsNotFound;
        private boolean throwAnalyticsExpired;

        FakeUrlService() {
            super((UrlRepository) null);
        }

        @Override
        public UrlResponseDto createShortUrl(UrlRequestDto request) {
            return baseResponse();
        }

        @Override
        public UrlResponseDto getUrlByShortCode(String shortCode) {
            if (throwShortCodeNotFound) {
                throw new UrlNotFoundException("Short URL not found");
            }
            if (throwShortCodeExpired) {
                throw new UrlExpiredException("Short URL has expired");
            }
            return baseResponse();
        }

        @Override
        public String getOriginalUrl(String shortCode) {
            if (throwRedirectNotFound) {
                throw new UrlNotFoundException("Short URL not found");
            }
            if (throwRedirectExpired) {
                throw new UrlExpiredException("Short URL has expired");
            }
            return "https://www.example.com";
        }

        @Override
        public UrlAnalyticsDto getAnalytics(String shortCode) {
            if (throwAnalyticsNotFound) {
                throw new UrlNotFoundException("Short URL not found");
            }
            if (throwAnalyticsExpired) {
                throw new UrlExpiredException("Short URL has expired");
            }
            return UrlAnalyticsDto.builder()
                    .originalUrl("https://www.example.com")
                    .shortCode("abc123")
                    .clickCount(15L)
                    .createdDate(LocalDateTime.now())
                    .expiryDate(LocalDateTime.now().plusDays(2))
                    .build();
        }

        @Override
        public UrlResponseDto updateUrl(Long id, UrlRequestDto request) {
            if (throwUpdateNotFound) {
                throw new UrlNotFoundException("URL not found");
            }
            return baseResponse();
        }

        @Override
        public void deleteUrl(Long id) {
            if (throwDeleteNotFound) {
                throw new UrlNotFoundException("URL not found");
            }
        }

        private UrlResponseDto baseResponse() {
            UrlResponseDto response = UrlResponseDto.builder()
                    .id(1L)
                    .originalUrl("https://www.example.com")
                    .shortCode("abc123")
                    .createdDate(LocalDateTime.now())
                    .expiryDate(LocalDateTime.now().plusDays(2))
                    .clickCount(0L)
                    .build();
            return response;
        }
    }
}
