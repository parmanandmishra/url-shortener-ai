package com.pm.urlshortener.dto;

import java.time.LocalDateTime;

public class UrlAnalyticsDto {

    private String originalUrl;
    private String shortCode;
    private Long clickCount;
    private LocalDateTime createdDate;
    private LocalDateTime expiryDate;

    public UrlAnalyticsDto() {
    }

    public UrlAnalyticsDto(String originalUrl, String shortCode, Long clickCount,
                           LocalDateTime createdDate, LocalDateTime expiryDate) {
        this.originalUrl = originalUrl;
        this.shortCode = shortCode;
        this.clickCount = clickCount;
        this.createdDate = createdDate;
        this.expiryDate = expiryDate;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public Long getClickCount() {
        return clickCount;
    }

    public void setClickCount(Long clickCount) {
        this.clickCount = clickCount;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
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
        private String shortCode;
        private Long clickCount;
        private LocalDateTime createdDate;
        private LocalDateTime expiryDate;

        private Builder() {
        }

        public Builder originalUrl(String originalUrl) {
            this.originalUrl = originalUrl;
            return this;
        }

        public Builder shortCode(String shortCode) {
            this.shortCode = shortCode;
            return this;
        }

        public Builder clickCount(Long clickCount) {
            this.clickCount = clickCount;
            return this;
        }

        public Builder createdDate(LocalDateTime createdDate) {
            this.createdDate = createdDate;
            return this;
        }

        public Builder expiryDate(LocalDateTime expiryDate) {
            this.expiryDate = expiryDate;
            return this;
        }

        public UrlAnalyticsDto build() {
            return new UrlAnalyticsDto(originalUrl, shortCode, clickCount, createdDate, expiryDate);
        }
    }
}
