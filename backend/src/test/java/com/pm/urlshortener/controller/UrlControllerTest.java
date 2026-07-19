package com.pm.urlshortener.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pm.urlshortener.dto.UrlAnalyticsDto;
import com.pm.urlshortener.dto.UrlRequestDto;
import com.pm.urlshortener.dto.UrlResponseDto;
import com.pm.urlshortener.exception.GlobalExceptionHandler;
import com.pm.urlshortener.exception.UrlNotFoundException;
import com.pm.urlshortener.repository.UrlRepository;
import com.pm.urlshortener.service.UrlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
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
        mockMvc.perform(post("/api/urls/shorten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(UrlRequestDto.builder()
                                .originalUrl("https://www.example.com")
                                .build())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.originalUrl").value("https://www.example.com"))
                .andExpect(jsonPath("$.shortCode").value("abc123"));
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

    private static class FakeUrlService extends UrlService {
        private boolean throwShortCodeNotFound;
        private boolean throwRedirectNotFound;
        private boolean throwUpdateNotFound;
        private boolean throwDeleteNotFound;
        private boolean throwAnalyticsNotFound;

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
            return baseResponse();
        }

        @Override
        public String getOriginalUrl(String shortCode) {
            if (throwRedirectNotFound) {
                throw new UrlNotFoundException("Short URL not found");
            }
            return "https://www.example.com";
        }

        @Override
        public UrlAnalyticsDto getAnalytics(String shortCode) {
            if (throwAnalyticsNotFound) {
                throw new UrlNotFoundException("Short URL not found");
            }
            return UrlAnalyticsDto.builder()
                    .originalUrl("https://www.example.com")
                    .shortCode("abc123")
                    .clickCount(15L)
                    .createdDate(LocalDateTime.now())
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
                    .clickCount(0L)
                    .build();
            ReflectionTestUtils.setField(response, "expiryDate", null);
            return response;
        }
    }
}
