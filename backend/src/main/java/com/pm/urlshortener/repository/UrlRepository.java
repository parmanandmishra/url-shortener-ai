package com.pm.urlshortener.repository;

import com.pm.urlshortener.entity.UrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlRepository extends JpaRepository<UrlMapping, Long> {
    Optional<UrlMapping> findByShortCode(String shortCode);
    boolean existsByShortCode(String shortCode);
    
    @Modifying
    @Query("UPDATE UrlMapping SET clickCount = clickCount + 1 WHERE id = :id")
    void incrementClickCount(@Param("id") Long id);
}
