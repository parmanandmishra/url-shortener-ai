package com.pm.urlshortener.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public class UrlRequestDto {
    @NotBlank(message = "URL cannot be blank")
    @Length(min = 10, max = 2048, message = "URL must be between 10 and 2048 characters")
    private String originalUrl;

    public UrlRequestDto() {
    }

    public UrlRequestDto(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String originalUrl;

        private Builder() {
        }

        public Builder originalUrl(String originalUrl) {
            this.originalUrl = originalUrl;
            return this;
        }

        public UrlRequestDto build() {
            return new UrlRequestDto(originalUrl);
        }
    }
}
