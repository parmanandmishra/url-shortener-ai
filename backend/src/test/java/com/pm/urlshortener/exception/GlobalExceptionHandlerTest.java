package com.pm.urlshortener.exception;

import com.pm.urlshortener.dto.UrlRequestDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Mock
    private WebRequest webRequest;

    @Test
    void handleUrlNotFoundException_shouldReturn404Response() {
        when(webRequest.getDescription(false)).thenReturn("uri=/api/urls/abc123");

        ResponseEntityAssert<ErrorResponse> result = ResponseEntityAssert.from(
                handler.handleUrlNotFoundException(new UrlNotFoundException("Short URL not found: abc123"), webRequest));

        assertEquals(HttpStatus.NOT_FOUND, result.status());
        assertEquals(404, result.body().getStatus());
        assertEquals("Short URL not found: abc123", result.body().getMessage());
        assertEquals("/api/urls/abc123", result.body().getPath());
        assertNotNull(result.body().getTimestamp());
    }

    @Test
    void handleInvalidUrlException_shouldReturn400Response() {
        when(webRequest.getDescription(false)).thenReturn("uri=/api/urls/shorten");

        ResponseEntityAssert<ErrorResponse> result = ResponseEntityAssert.from(
                handler.handleInvalidUrlException(new InvalidUrlException("URL must start with http:// or https://"), webRequest));

        assertEquals(HttpStatus.BAD_REQUEST, result.status());
        assertEquals(400, result.body().getStatus());
        assertEquals("URL must start with http:// or https://", result.body().getMessage());
        assertEquals("/api/urls/shorten", result.body().getPath());
        assertNotNull(result.body().getTimestamp());
    }

    @Test
    void handleUrlExpiredException_shouldReturn410Response() {
        when(webRequest.getDescription(false)).thenReturn("uri=/api/urls/expired1");

        ResponseEntityAssert<ErrorResponse> result = ResponseEntityAssert.from(
                handler.handleUrlExpiredException(new UrlExpiredException("Short URL has expired: expired1"), webRequest));

        assertEquals(HttpStatus.GONE, result.status());
        assertEquals(410, result.body().getStatus());
        assertEquals("Short URL has expired: expired1", result.body().getMessage());
        assertEquals("/api/urls/expired1", result.body().getPath());
    }

    @Test
    void handleValidationException_shouldReturnFirstFieldErrorMessage() throws Exception {
        when(webRequest.getDescription(false)).thenReturn("uri=/api/urls/shorten");

        Method method = DummyHandler.class.getDeclaredMethod("handle", UrlRequestDto.class);
        MethodParameter methodParameter = new MethodParameter(method, 0);
        BindingResult bindingResult = new BeanPropertyBindingResult(new UrlRequestDto(), "urlRequestDto");
        bindingResult.addError(new FieldError("urlRequestDto", "originalUrl", "URL cannot be blank"));
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(methodParameter, bindingResult);

        ResponseEntityAssert<ErrorResponse> result = ResponseEntityAssert.from(
                handler.handleValidationException(exception, webRequest));

        assertEquals(HttpStatus.BAD_REQUEST, result.status());
        assertEquals(400, result.body().getStatus());
        assertEquals("URL cannot be blank", result.body().getMessage());
        assertEquals("/api/urls/shorten", result.body().getPath());
    }

    @Test
    void handleValidationException_shouldFallbackWhenNoFieldErrorExists() throws Exception {
        when(webRequest.getDescription(false)).thenReturn("uri=/api/urls/shorten");

        Method method = DummyHandler.class.getDeclaredMethod("handle", UrlRequestDto.class);
        MethodParameter methodParameter = new MethodParameter(method, 0);
        BindingResult bindingResult = new BeanPropertyBindingResult(new UrlRequestDto(), "urlRequestDto");
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(methodParameter, bindingResult);

        ResponseEntityAssert<ErrorResponse> result = ResponseEntityAssert.from(
                handler.handleValidationException(exception, webRequest));

        assertEquals("Validation failed", result.body().getMessage());
    }

    @Test
    void handleGlobalException_shouldReturn500Response() {
        when(webRequest.getDescription(false)).thenReturn("uri=/api/urls/shorten");

        ResponseEntityAssert<ErrorResponse> result = ResponseEntityAssert.from(
                handler.handleGlobalException(new IllegalStateException("boom"), webRequest));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.status());
        assertEquals(500, result.body().getStatus());
        assertTrue(result.body().getMessage().contains("boom"));
        assertEquals("/api/urls/shorten", result.body().getPath());
    }

    @Test
    void handleMessageNotReadable_shouldReturn400Response() {
        when(webRequest.getDescription(false)).thenReturn("uri=/api/urls/shorten");
        ResponseEntityAssert<ErrorResponse> result = ResponseEntityAssert.from(
                handler.handleMessageNotReadableException(new HttpMessageNotReadableException("bad json"), webRequest));

        assertEquals(HttpStatus.BAD_REQUEST, result.status());
        assertEquals(400, result.body().getStatus());
        assertEquals("Malformed JSON request", result.body().getMessage());
    }

    @Test
    void handleMethodArgumentTypeMismatch_shouldReturn400Response() {
        when(webRequest.getDescription(false)).thenReturn("uri=/api/urls/notanid");
        MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException(
                "abc", Long.class, "id", null, new IllegalArgumentException("type mismatch"));

        ResponseEntityAssert<ErrorResponse> result = ResponseEntityAssert.from(
                handler.handleMethodArgumentTypeMismatch(ex, webRequest));

        assertEquals(HttpStatus.BAD_REQUEST, result.status());
        assertEquals(400, result.body().getStatus());
        assertEquals("Invalid path parameter type", result.body().getMessage());
    }

    @Test
    void handleMethodNotSupported_shouldReturn405Response() {
        when(webRequest.getDescription(false)).thenReturn("uri=/api/urls/redirect/abc123");
        HttpRequestMethodNotSupportedException ex = new HttpRequestMethodNotSupportedException("POST");

        ResponseEntityAssert<ErrorResponse> result = ResponseEntityAssert.from(
                handler.handleMethodNotSupported(ex, webRequest));

        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, result.status());
        assertEquals(405, result.body().getStatus());
        assertEquals("Request method 'POST' is not supported", result.body().getMessage());
    }

    @Test
    void handleMediaTypeNotSupported_shouldReturn415Response() {
        when(webRequest.getDescription(false)).thenReturn("uri=/api/urls/shorten");
        HttpMediaTypeNotSupportedException ex = new HttpMediaTypeNotSupportedException("Content type not supported");

        ResponseEntityAssert<ErrorResponse> result = ResponseEntityAssert.from(
                handler.handleMediaTypeNotSupported(ex, webRequest));

        assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, result.status());
        assertEquals(415, result.body().getStatus());
        assertTrue(result.body().getMessage().contains("Unsupported Content-Type"));
    }

    @Test
    void handleMediaTypeNotAcceptable_shouldReturn406Response() {
        when(webRequest.getDescription(false)).thenReturn("uri=/api/urls/shorten");
        HttpMediaTypeNotAcceptableException ex = new HttpMediaTypeNotAcceptableException("No acceptable representation");

        ResponseEntityAssert<ErrorResponse> result = ResponseEntityAssert.from(
                handler.handleMediaTypeNotAcceptable(ex, webRequest));

        assertEquals(HttpStatus.NOT_ACCEPTABLE, result.status());
        assertEquals(406, result.body().getStatus());
        assertEquals("Requested response media type is not acceptable", result.body().getMessage());
    }

    @Test
    void handleNoResourceFound_shouldReturn404Response() {
        when(webRequest.getDescription(false)).thenReturn("uri=/api/unknownpath");
        NoResourceFoundException ex = new NoResourceFoundException(HttpMethod.GET, "/api/unknownpath");

        ResponseEntityAssert<ErrorResponse> result = ResponseEntityAssert.from(
                handler.handleNoResourceFound(ex, webRequest));

        assertEquals(HttpStatus.NOT_FOUND, result.status());
        assertEquals(404, result.body().getStatus());
        assertEquals("Resource not found", result.body().getMessage());
    }

    private static class DummyHandler {
        @SuppressWarnings("unused")
        void handle(UrlRequestDto request) {
        }
    }

    private record ResponseEntityAssert<T>(HttpStatusCode status, T body) {
        static <T> ResponseEntityAssert<T> from(org.springframework.http.ResponseEntity<T> response) {
            return new ResponseEntityAssert<>(response.getStatusCode(), response.getBody());
        }
    }
}
