package com.ryuqq.authhub.adapter.in.rest.common.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.ryuqq.authhub.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.authhub.adapter.in.rest.common.mapper.ErrorMapper;
import com.ryuqq.authhub.domain.common.exception.DomainException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ExtendWith(MockitoExtension.class)
@DisplayName("GlobalExceptionHandler 테스트")
class GlobalExceptionHandlerTest {

    @Mock private ErrorMapperRegistry errorMapperRegistry;

    @Mock private HttpServletRequest request;

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler(errorMapperRegistry);

        // 기본 request mock 설정
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getQueryString()).thenReturn(null);
    }

    @Nested
    @DisplayName("Validation 예외 처리 테스트")
    class ValidationExceptionTests {

        @Test
        @DisplayName("handleValidationException - MethodArgumentNotValidException 처리")
        void handleValidationException_success() {
            // Given
            MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
            org.springframework.validation.BindingResult bindingResult =
                    mock(org.springframework.validation.BindingResult.class);

            FieldError fieldError1 = new FieldError("user", "name", "이름은 필수입니다");
            FieldError fieldError2 = new FieldError("user", "email", "이메일 형식이 올바르지 않습니다");

            when(exception.getBindingResult()).thenReturn(bindingResult);
            when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));

            // When
            ResponseEntity<ProblemDetail> response =
                    globalExceptionHandler.handleValidationException(exception, request);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getTitle()).isEqualTo("Bad Request");
            assertThat(response.getBody().getDetail()).isEqualTo("Validation failed for request");
            assertThat(response.getBody().getInstance()).isEqualTo(URI.create("/api/test"));

            @SuppressWarnings("unchecked")
            Map<String, String> errors =
                    (Map<String, String>) response.getBody().getProperties().get("errors");
            assertThat(errors).containsEntry("name", "이름은 필수입니다");
            assertThat(errors).containsEntry("email", "이메일 형식이 올바르지 않습니다");
        }

        @Test
        @DisplayName("handleBindException - BindException 처리")
        void handleBindException_success() {
            // Given
            BindException exception = mock(BindException.class);
            org.springframework.validation.BindingResult bindingResult =
                    mock(org.springframework.validation.BindingResult.class);
            FieldError fieldError = new FieldError("form", "field1", "필드 검증 실패");

            when(exception.getBindingResult()).thenReturn(bindingResult);
            when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

            // When
            ResponseEntity<ProblemDetail> response =
                    globalExceptionHandler.handleBindException(exception, request);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getDetail()).isEqualTo("Validation failed for request");

            @SuppressWarnings("unchecked")
            Map<String, String> errors =
                    (Map<String, String>) response.getBody().getProperties().get("errors");
            assertThat(errors).containsEntry("field1", "필드 검증 실패");
        }

        @Test
        @DisplayName("handleConstraintViolation - ConstraintViolationException 처리")
        void handleConstraintViolation_success() {
            // Given
            ConstraintViolation<?> violation1 = mock(ConstraintViolation.class);
            ConstraintViolation<?> violation2 = mock(ConstraintViolation.class);
            Path path1 = mock(Path.class);
            Path path2 = mock(Path.class);

            when(violation1.getPropertyPath()).thenReturn(path1);
            when(violation1.getMessage()).thenReturn("값이 null일 수 없습니다");
            when(path1.toString()).thenReturn("param1");

            when(violation2.getPropertyPath()).thenReturn(path2);
            when(violation2.getMessage()).thenReturn("값이 범위를 벗어났습니다");
            when(path2.toString()).thenReturn("param2");

            ConstraintViolationException exception =
                    new ConstraintViolationException(
                            "Validation failed", Set.of(violation1, violation2));

            // When
            ResponseEntity<ProblemDetail> response =
                    globalExceptionHandler.handleConstraintViolation(exception, request);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();

            @SuppressWarnings("unchecked")
            Map<String, String> errors =
                    (Map<String, String>) response.getBody().getProperties().get("errors");
            assertThat(errors).containsEntry("param1", "값이 null일 수 없습니다");
            assertThat(errors).containsEntry("param2", "값이 범위를 벗어났습니다");
        }
    }

    @Nested
    @DisplayName("클라이언트 오류 예외 처리 테스트")
    class ClientErrorExceptionTests {

        @Test
        @DisplayName("handleIllegalArgumentException - IllegalArgumentException 처리")
        void handleIllegalArgumentException_success() {
            // Given
            IllegalArgumentException exception = new IllegalArgumentException("잘못된 인자입니다");

            // When
            ResponseEntity<ProblemDetail> response =
                    globalExceptionHandler.handleIllegalArgumentException(exception, request);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getTitle()).isEqualTo("Bad Request");
            assertThat(response.getBody().getDetail()).isEqualTo("잘못된 인자입니다");
        }

        @Test
        @DisplayName("handleIllegalArgumentException - 메시지가 null인 경우")
        void handleIllegalArgumentException_nullMessage() {
            // Given
            IllegalArgumentException exception = new IllegalArgumentException();

            // When
            ResponseEntity<ProblemDetail> response =
                    globalExceptionHandler.handleIllegalArgumentException(exception, request);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getDetail()).isEqualTo("Invalid argument");
        }

        @Test
        @DisplayName("handleHttpMessageNotReadable - HttpMessageNotReadableException 처리")
        void handleHttpMessageNotReadable_success() {
            // Given
            HttpMessageNotReadableException exception = mock(HttpMessageNotReadableException.class);
            Throwable cause = new RuntimeException("JSON parse error");
            when(exception.getMostSpecificCause()).thenReturn(cause);

            // When
            ResponseEntity<ProblemDetail> response =
                    globalExceptionHandler.handleHttpMessageNotReadable(exception, request);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getDetail()).isEqualTo("잘못된 요청 형식입니다. JSON 형식을 확인해주세요.");
        }

        @Test
        @DisplayName("handleTypeMismatch - MethodArgumentTypeMismatchException 처리")
        void handleTypeMismatch_success() {
            // Given
            MethodParameter parameter = mock(MethodParameter.class);
            MethodArgumentTypeMismatchException exception =
                    new MethodArgumentTypeMismatchException(
                            "invalid-id", Long.class, "userId", parameter, new RuntimeException());

            // When
            ResponseEntity<ProblemDetail> response =
                    globalExceptionHandler.handleTypeMismatch(exception, request);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getDetail())
                    .contains("파라미터 'userId'의 값 'invalid-id'는 Long 타입으로 변환할 수 없습니다");
        }

        @Test
        @DisplayName("handleMissingParam - MissingServletRequestParameterException 처리")
        void handleMissingParam_success() {
            // Given
            MissingServletRequestParameterException exception =
                    new MissingServletRequestParameterException("page", "int");

            // When
            ResponseEntity<ProblemDetail> response =
                    globalExceptionHandler.handleMissingParam(exception, request);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getDetail()).isEqualTo("필수 파라미터 'page'가 누락되었습니다");
        }
    }

    @Nested
    @DisplayName("리소스 및 메서드 오류 예외 처리 테스트")
    class ResourceAndMethodErrorTests {

        @Test
        @DisplayName("handleNoResource - NoResourceFoundException 처리")
        void handleNoResource_success() {
            // Given
            NoResourceFoundException exception = mock(NoResourceFoundException.class);
            when(exception.getResourcePath()).thenReturn("/api/nonexistent");

            // When
            ResponseEntity<ProblemDetail> response =
                    globalExceptionHandler.handleNoResource(exception, request);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getTitle()).isEqualTo("Not Found");
            assertThat(response.getBody().getDetail()).isEqualTo("요청한 리소스를 찾을 수 없습니다");
        }

        @Test
        @DisplayName("handleMethodNotAllowed - HttpRequestMethodNotSupportedException 처리")
        void handleMethodNotAllowed_success() {
            // Given
            HttpRequestMethodNotSupportedException exception =
                    new HttpRequestMethodNotSupportedException("DELETE", List.of("GET", "POST"));

            // When
            ResponseEntity<ProblemDetail> response =
                    globalExceptionHandler.handleMethodNotAllowed(exception, request);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getTitle()).isEqualTo("Method Not Allowed");
            assertThat(response.getBody().getDetail()).contains("DELETE 메서드는 지원하지 않습니다");
            assertThat(response.getHeaders().getAllow())
                    .containsExactlyInAnyOrder(HttpMethod.GET, HttpMethod.POST);
        }

        @Test
        @DisplayName("handleMethodNotAllowed - 지원 메서드가 없는 경우")
        void handleMethodNotAllowed_noSupportedMethods() {
            // Given
            HttpRequestMethodNotSupportedException exception =
                    new HttpRequestMethodNotSupportedException("PATCH");

            // When
            ResponseEntity<ProblemDetail> response =
                    globalExceptionHandler.handleMethodNotAllowed(exception, request);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getDetail())
                    .contains("PATCH 메서드는 지원하지 않습니다. 지원되는 메서드: 없음");
        }
    }

    @Nested
    @DisplayName("상태 및 서버 오류 예외 처리 테스트")
    class StateAndServerErrorTests {

        @Test
        @DisplayName("handleIllegalState - IllegalStateException 처리")
        void handleIllegalState_success() {
            // Given
            IllegalStateException exception = new IllegalStateException("상태 충돌이 발생했습니다");

            // When
            ResponseEntity<ProblemDetail> response =
                    globalExceptionHandler.handleIllegalState(exception, request);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getTitle()).isEqualTo("Conflict");
            assertThat(response.getBody().getDetail()).isEqualTo("상태 충돌이 발생했습니다");
        }

        @Test
        @DisplayName("handleIllegalState - 메시지가 null인 경우")
        void handleIllegalState_nullMessage() {
            // Given
            IllegalStateException exception = new IllegalStateException();

            // When
            ResponseEntity<ProblemDetail> response =
                    globalExceptionHandler.handleIllegalState(exception, request);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getDetail()).isEqualTo("State conflict");
        }

        @Test
        @DisplayName("handleGlobal - 예상치 못한 Exception 처리")
        void handleGlobal_success() {
            // Given
            RuntimeException exception = new RuntimeException("예상치 못한 오류");

            // When
            ResponseEntity<ProblemDetail> response =
                    globalExceptionHandler.handleGlobal(exception, request);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getTitle()).isEqualTo("Internal Server Error");
            assertThat(response.getBody().getDetail()).isEqualTo("서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
        }
    }

    @Nested
    @DisplayName("도메인 예외 처리 테스트")
    class DomainExceptionTests {

        @Test
        @DisplayName("handleDomain - DomainException 처리 (매핑 성공)")
        void handleDomain_withMapping_success() {
            // Given
            DomainException domainException =
                    new TestDomainException("TEST-001", "테스트 에러", Map.of("id", "123"));
            Locale locale = Locale.KOREAN;

            ErrorMapper.MappedError mapping =
                    new ErrorMapper.MappedError(
                            HttpStatus.BAD_REQUEST,
                            "테스트 에러",
                            "테스트 상세 메시지",
                            URI.create("https://api.example.com/problems/test-error"));

            when(errorMapperRegistry.map(eq(domainException), eq(locale)))
                    .thenReturn(Optional.of(mapping));

            // When
            ResponseEntity<ProblemDetail> response =
                    globalExceptionHandler.handleDomain(domainException, request, locale);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getTitle()).isEqualTo("테스트 에러");
            assertThat(response.getBody().getDetail()).isEqualTo("테스트 상세 메시지");
            assertThat(response.getBody().getType())
                    .isEqualTo(URI.create("https://api.example.com/problems/test-error"));
            assertThat(response.getBody().getProperties().get("code")).isEqualTo("TEST-001");

            @SuppressWarnings("unchecked")
            Map<String, Object> args =
                    (Map<String, Object>) response.getBody().getProperties().get("args");
            assertThat(args).containsEntry("id", "123");
        }

        @Test
        @DisplayName("handleDomain - DomainException 처리 (기본 매핑)")
        void handleDomain_withDefaultMapping_success() {
            // Given
            DomainException domainException =
                    new TestDomainException("TEST-002", "기본 에러", Map.of());
            Locale locale = Locale.ENGLISH;

            ErrorMapper.MappedError defaultMapping =
                    new ErrorMapper.MappedError(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            "Internal Error",
                            "기본 매핑 메시지",
                            URI.create("about:blank"));

            when(errorMapperRegistry.map(eq(domainException), eq(locale)))
                    .thenReturn(Optional.empty());
            when(errorMapperRegistry.defaultMapping(eq(domainException)))
                    .thenReturn(defaultMapping);

            // When
            ResponseEntity<ProblemDetail> response =
                    globalExceptionHandler.handleDomain(domainException, request, locale);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getTitle()).isEqualTo("Internal Error");
            assertThat(response.getBody().getDetail()).isEqualTo("기본 매핑 메시지");
            assertThat(response.getBody().getProperties().get("code")).isEqualTo("TEST-002");
        }

        @Test
        @DisplayName("handleDomain - DomainException 처리 (args 없음)")
        void handleDomain_noArgs_success() {
            // Given
            DomainException domainException =
                    new TestDomainException("TEST-003", "인자 없는 에러", Map.of());
            Locale locale = Locale.KOREAN;

            ErrorMapper.MappedError mapping =
                    new ErrorMapper.MappedError(
                            HttpStatus.NOT_FOUND,
                            "Not Found",
                            "리소스를 찾을 수 없습니다",
                            URI.create("about:blank"));

            when(errorMapperRegistry.map(eq(domainException), eq(locale)))
                    .thenReturn(Optional.of(mapping));

            // When
            ResponseEntity<ProblemDetail> response =
                    globalExceptionHandler.handleDomain(domainException, request, locale);

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getProperties().get("args")).isNull();
        }
    }

    @Nested
    @DisplayName("공통 빌더 메서드 테스트")
    class CommonBuilderTests {

        @Test
        @DisplayName("ProblemDetail 응답에 공통 필드 포함 확인")
        void problemDetail_containsCommonFields() {
            // Given
            IllegalArgumentException exception = new IllegalArgumentException("테스트 에러");

            // When
            ResponseEntity<ProblemDetail> response =
                    globalExceptionHandler.handleIllegalArgumentException(exception, request);

            // Then
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getType()).isEqualTo(URI.create("about:blank"));
            assertThat(response.getBody().getInstance()).isEqualTo(URI.create("/api/test"));
            assertThat(response.getBody().getProperties().get("timestamp")).isNotNull();
        }

        @Test
        @DisplayName("쿼리 스트링이 있는 요청 URI 처리")
        void requestUri_withQueryString() {
            // Given
            when(request.getRequestURI()).thenReturn("/api/users");
            when(request.getQueryString()).thenReturn("page=1&size=10");
            IllegalArgumentException exception = new IllegalArgumentException("테스트 에러");

            // When
            ResponseEntity<ProblemDetail> response =
                    globalExceptionHandler.handleIllegalArgumentException(exception, request);

            // Then
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getInstance())
                    .isEqualTo(URI.create("/api/users?page=1&size=10"));
        }

        @Test
        @DisplayName("빈 쿼리 스트링 처리")
        void requestUri_withEmptyQueryString() {
            // Given
            when(request.getRequestURI()).thenReturn("/api/users");
            when(request.getQueryString()).thenReturn("");
            IllegalArgumentException exception = new IllegalArgumentException("테스트 에러");

            // When
            ResponseEntity<ProblemDetail> response =
                    globalExceptionHandler.handleIllegalArgumentException(exception, request);

            // Then
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getInstance()).isEqualTo(URI.create("/api/users"));
        }
    }

    // 테스트용 DomainException 구현
    @SuppressWarnings("PMD.TestClassWithoutTestCases")
    private static class TestDomainException extends DomainException {
        public TestDomainException(String code, String message, Map<String, Object> args) {
            super(code, message, args);
        }
    }
}
