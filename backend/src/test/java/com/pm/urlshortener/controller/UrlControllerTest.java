package com.pm.urlshortener.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pm.urlshortener.dto.UrlRequestDto;
import com.pm.urlshortener.dto.UrlResponseDto;
import com.pm.urlshortener.exception.UrlNotFoundException;
import com.pm.urlshortener.service.UrlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UrlService urlService;

    private UrlRequestDto urlRequest;
    private UrlResponseDto urlResponse;

    @BeforeEach
    void setUp() {
        urlRequest = UrlRequestDto.builder()
                .originalUrl("https://www.example.com")
                .build();

        urlResponse = UrlResponseDto.builder()
                .id(1L)
                .originalUrl("https://www.example.com")
                .shortCode("abc123")
                .createdDate(LocalDateTime.now())
                .clickCount(0L)
                .build();
    }

    @Test
    void testCreateShortUrl_Success() throws Exception {
        when(urlService.createShortUrl(any(UrlRequestDto.class))).thenReturn(urlResponse);

        mockMvc.perform(post("/api/urls/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(urlRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.originalUrl").value("https://www.example.com"))
                .andExpect(jsonPath("$.shortCode").value("abc123"));

        verify(urlService, times(1)).createShortUrl(any(UrlRequestDto.class));
    }

    @Test
    void testCreateShortUrl_InvalidRequest() throws Exception {
        UrlRequestDto invalidRequest = UrlRequestDto.builder()
                .originalUrl("")
                .build();

        mockMvc.perform(post("/api/urls/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetUrlByShortCode_Success() throws Exception {
        when(urlService.getUrlByShortCode("abc123")).thenReturn(urlResponse);

        mockMvc.perform(get("/api/urls/abc123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shortCode").value("abc123"))
                .andExpect(jsonPath("$.originalUrl").value("https://www.example.com"));

        verify(urlService, times(1)).getUrlByShortCode("abc123");
    }

    @Test
    void testGetUrlByShortCode_NotFound() throws Exception {
        when(urlService.getUrlByShortCode(anyString())).thenThrow(
                new UrlNotFoundException("Short URL not found")
        );

        mockMvc.perform(get("/api/urls/invalid")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(urlService, times(1)).getUrlByShortCode("invalid");
    }

    @Test
    void testRedirectToOriginalUrl_Success() throws Exception {
        when(urlService.getOriginalUrl("abc123")).thenReturn("https://www.example.com");

        mockMvc.perform(get("/api/urls/redirect/abc123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(urlService, times(1)).getOriginalUrl("abc123");
    }

    @Test
    void testRedirectToOriginalUrl_NotFound() throws Exception {
        when(urlService.getOriginalUrl(anyString())).thenThrow(
                new UrlNotFoundException("Short URL not found")
        );

        mockMvc.perform(get("/api/urls/redirect/invalid")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(urlService, times(1)).getOriginalUrl("invalid");
    }

    @Test
    void testUpdateUrl_Success() throws Exception {
        when(urlService.updateUrl(anyLong(), any(UrlRequestDto.class))).thenReturn(urlResponse);

        mockMvc.perform(put("/api/urls/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(urlRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.originalUrl").value("https://www.example.com"));

        verify(urlService, times(1)).updateUrl(anyLong(), any(UrlRequestDto.class));
    }

    @Test
    void testUpdateUrl_NotFound() throws Exception {
        when(urlService.updateUrl(anyLong(), any(UrlRequestDto.class))).thenThrow(
                new UrlNotFoundException("URL not found")
        );

        mockMvc.perform(put("/api/urls/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(urlRequest)))
                .andExpect(status().isNotFound());

        verify(urlService, times(1)).updateUrl(anyLong(), any(UrlRequestDto.class));
    }

    @Test
    void testDeleteUrl_Success() throws Exception {
        doNothing().when(urlService).deleteUrl(anyLong());

        mockMvc.perform(delete("/api/urls/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(urlService, times(1)).deleteUrl(anyLong());
    }

    @Test
    void testDeleteUrl_NotFound() throws Exception {
        doThrow(new UrlNotFoundException("URL not found")).when(urlService).deleteUrl(anyLong());

        mockMvc.perform(delete("/api/urls/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(urlService, times(1)).deleteUrl(anyLong());
    }
}
