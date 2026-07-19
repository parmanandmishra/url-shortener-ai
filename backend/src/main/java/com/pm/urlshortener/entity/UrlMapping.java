package com.pm.urlshortener.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "urls", indexes = {
    @Index(name = "idx_short_code", columnList = "shortCode", unique = true),
    @Index(name = "idx_created_date", columnList = "createdDate"),
    @Index(name = "idx_expiry_date", columnList = "expiryDate")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UrlMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String originalUrl;

    @Column(nullable = false, unique = true)
    private String shortCode;

    @Column(nullable = false)
    private LocalDateTime createdDate;

    @Column
    private LocalDateTime expiryDate;

    @Column(nullable = false)
    private Long clickCount = 0L;
}
