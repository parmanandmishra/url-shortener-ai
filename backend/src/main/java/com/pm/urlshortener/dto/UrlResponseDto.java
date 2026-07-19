package com.pm.urlshortener.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UrlResponseDto {
    private Long id;
    private String originalUrl;
    private String shortCode;
    private LocalDateTime createdDate;
    private LocalDateTime expiryDate;
    private Long clickCount;
}
