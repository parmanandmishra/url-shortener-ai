package com.pm.urlshortener.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Future;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

public class UrlRequestDto {
    @Schema(
            description = "Original URL to shorten",
            example = "https://example.com/articles/ai-testing")
    @NotBlank(message = "URL cannot be blank")
    @Length(min = 10, max = 2048, message = "URL must be between 10 and 2048 characters")
    private String originalUrl;

    @Schema(
            description = "Optional expiry timestamp in ISO-8601 format. Must be in the future. Null means never expires.",
            example = "2026-12-31T23:59:59")
    @Future(message = "expiryDate must be a future date-time")
    private LocalDateTime expiryDate;

    public UrlRequestDto() {
    }

    public UrlRequestDto(String originalUrl, LocalDateTime expiryDate) {
        this.originalUrl = originalUrl;
        this.expiryDate = expiryDate;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String originalUrl;
        private LocalDateTime expiryDate;

        private Builder() {
        }

        public Builder originalUrl(String originalUrl) {
            this.originalUrl = originalUrl;
            return this;
        }

        public Builder expiryDate(LocalDateTime expiryDate) {
            this.expiryDate = expiryDate;
            return this;
        }

        public UrlRequestDto build() {
            return new UrlRequestDto(originalUrl, expiryDate);
        }
    }
}
