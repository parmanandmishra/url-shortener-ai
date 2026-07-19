package com.pm.urlshortener.controller;

import com.pm.urlshortener.dto.UrlAnalyticsDto;
import com.pm.urlshortener.dto.UrlRequestDto;
import com.pm.urlshortener.dto.UrlResponseDto;
import com.pm.urlshortener.service.UrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/urls")
@Tag(name = "URL Shortener", description = "API endpoints for URL shortening and retrieval")
public class UrlController {

    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping("/shorten")
    @Operation(summary = "Create a short URL", description = "Convert a long URL to a short URL")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Short URL created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UrlResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid URL provided"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<UrlResponseDto> createShortUrl(@Valid @RequestBody UrlRequestDto request) {
        UrlResponseDto response = urlService.createShortUrl(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{shortCode}")
    @Operation(summary = "Get URL details by short code", description = "Retrieve the original URL details associated with a short code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "URL found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UrlResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Short URL not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<UrlResponseDto> getUrlByShortCode(
            @PathVariable String shortCode) {
        UrlResponseDto response = urlService.getUrlByShortCode(shortCode);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/redirect/{shortCode}")
    @Operation(summary = "Redirect to original URL", description = "Increment click count and return the original URL")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Original URL retrieved",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Short URL not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<String> redirectToOriginalUrl(@PathVariable String shortCode) {
        String originalUrl = urlService.getOriginalUrl(shortCode);
        return ResponseEntity.ok(originalUrl);
    }

    @GetMapping("/analytics/{shortCode}")
    @Operation(summary = "Get URL analytics", description = "Retrieve click count and metadata for a short URL without incrementing the counter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Analytics retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UrlAnalyticsDto.class))),
            @ApiResponse(responseCode = "404", description = "Short URL not found"),
            @ApiResponse(responseCode = "410", description = "Short URL has expired"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<UrlAnalyticsDto> getAnalytics(@PathVariable String shortCode) {
        UrlAnalyticsDto analytics = urlService.getAnalytics(shortCode);
        return ResponseEntity.ok(analytics);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update URL", description = "Update the original URL associated with a given ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "URL updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UrlResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid URL provided"),
            @ApiResponse(responseCode = "404", description = "URL not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<UrlResponseDto> updateUrl(
            @PathVariable Long id,
            @Valid @RequestBody UrlRequestDto request) {
        UrlResponseDto response = urlService.updateUrl(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete URL", description = "Delete a URL record by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "URL deleted successfully"),
            @ApiResponse(responseCode = "404", description = "URL not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteUrl(@PathVariable Long id) {
        urlService.deleteUrl(id);
        return ResponseEntity.noContent().build();
    }
}
