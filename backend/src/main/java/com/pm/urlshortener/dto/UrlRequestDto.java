package com.pm.urlshortener.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UrlRequestDto {
    @NotBlank(message = "URL cannot be blank")
    @Length(min = 10, max = 2048, message = "URL must be between 10 and 2048 characters")
    private String originalUrl;
}
