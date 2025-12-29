package com.ryuqq.authhub.adapter.in.rest.tenant.dto.query;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * SearchTenantsApiRequest 단위 테스트
 *
 * <p>검증 범위:
 *
 * <ul>
 *   <li>Record 생성 검증
 *   <li>Bean Validation 규칙 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@DisplayName("SearchTenantsApiRequest 단위 테스트")
@Tag("unit")
@Tag("adapter-rest")
class SearchTenantsApiRequestTest {

    private static Validator validator;
    private static final Instant NOW = Instant.now();
    private static final Instant ONE_MONTH_AGO = NOW.minus(30, ChronoUnit.DAYS);

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Nested
    @DisplayName("Record 생성 테스트")
    class RecordCreationTest {

        @Test
        @DisplayName("[생성] 모든 필드로 생성 시 정상 생성")
        void create_withAllFields_shouldCreateSuccessfully() {
            // Given & When
            SearchTenantsApiRequest request =
                    new SearchTenantsApiRequest(
                            "Test", List.of("ACTIVE", "INACTIVE"), ONE_MONTH_AGO, NOW, 0, 20);

            // Then
            assertThat(request.name()).isEqualTo("Test");
            assertThat(request.statuses()).containsExactly("ACTIVE", "INACTIVE");
            assertThat(request.createdFrom()).isEqualTo(ONE_MONTH_AGO);
            assertThat(request.createdTo()).isEqualTo(NOW);
            assertThat(request.page()).isEqualTo(0);
            assertThat(request.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("[생성] 단일 상태로 생성 시 정상 생성")
        void create_withSingleStatus_shouldCreateSuccessfully() {
            // Given & When
            SearchTenantsApiRequest request =
                    new SearchTenantsApiRequest(
                            "Test", List.of("ACTIVE"), ONE_MONTH_AGO, NOW, 0, 20);

            // Then
            assertThat(request.statuses()).containsExactly("ACTIVE");
        }

        @Test
        @DisplayName("[생성] 선택적 필드가 null인 경우 정상 생성")
        void create_withNullOptionalFields_shouldCreateSuccessfully() {
            // Given & When
            SearchTenantsApiRequest request =
                    new SearchTenantsApiRequest(null, null, ONE_MONTH_AGO, NOW, null, null);

            // Then
            assertThat(request.name()).isNull();
            assertThat(request.statuses()).isNull();
            assertThat(request.createdFrom()).isEqualTo(ONE_MONTH_AGO);
            assertThat(request.createdTo()).isEqualTo(NOW);
            assertThat(request.page()).isNull();
            assertThat(request.size()).isNull();
        }
    }

    @Nested
    @DisplayName("Bean Validation 테스트")
    class ValidationTest {

        @Test
        @DisplayName("[Validation] 모든 필드가 유효하면 위반 없음")
        void validate_withValidFields_shouldHaveNoViolations() {
            // Given
            SearchTenantsApiRequest request =
                    new SearchTenantsApiRequest(
                            "Test", List.of("ACTIVE"), ONE_MONTH_AGO, NOW, 0, 20);

            // When
            Set<ConstraintViolation<SearchTenantsApiRequest>> violations =
                    validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("[Validation] createdFrom이 null이면 위반 발생")
        void validate_withNullCreatedFrom_shouldHaveViolation() {
            // Given
            SearchTenantsApiRequest request =
                    new SearchTenantsApiRequest("Test", List.of("ACTIVE"), null, NOW, 0, 20);

            // When
            Set<ConstraintViolation<SearchTenantsApiRequest>> violations =
                    validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("생성일시 시작은 필수입니다");
        }

        @Test
        @DisplayName("[Validation] createdTo가 null이면 위반 발생")
        void validate_withNullCreatedTo_shouldHaveViolation() {
            // Given
            SearchTenantsApiRequest request =
                    new SearchTenantsApiRequest(
                            "Test", List.of("ACTIVE"), ONE_MONTH_AGO, null, 0, 20);

            // When
            Set<ConstraintViolation<SearchTenantsApiRequest>> violations =
                    validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("생성일시 종료는 필수입니다");
        }

        @Test
        @DisplayName("[Validation] 페이지 번호가 음수면 위반 발생")
        void validate_withNegativePage_shouldHaveViolation() {
            // Given
            SearchTenantsApiRequest request =
                    new SearchTenantsApiRequest(null, null, ONE_MONTH_AGO, NOW, -1, 20);

            // When
            Set<ConstraintViolation<SearchTenantsApiRequest>> violations =
                    validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("페이지 번호는 0 이상이어야 합니다");
        }

        @Test
        @DisplayName("[Validation] 페이지 번호가 0이면 위반 없음")
        void validate_withZeroPage_shouldHaveNoViolations() {
            // Given
            SearchTenantsApiRequest request =
                    new SearchTenantsApiRequest(null, null, ONE_MONTH_AGO, NOW, 0, 20);

            // When
            Set<ConstraintViolation<SearchTenantsApiRequest>> violations =
                    validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("[Validation] 페이지 크기가 0이면 위반 발생")
        void validate_withZeroSize_shouldHaveViolation() {
            // Given
            SearchTenantsApiRequest request =
                    new SearchTenantsApiRequest(null, null, ONE_MONTH_AGO, NOW, 0, 0);

            // When
            Set<ConstraintViolation<SearchTenantsApiRequest>> violations =
                    validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("페이지 크기는 1 이상이어야 합니다");
        }

        @Test
        @DisplayName("[Validation] 페이지 크기가 1이면 위반 없음")
        void validate_withMinSize_shouldHaveNoViolations() {
            // Given
            SearchTenantsApiRequest request =
                    new SearchTenantsApiRequest(null, null, ONE_MONTH_AGO, NOW, 0, 1);

            // When
            Set<ConstraintViolation<SearchTenantsApiRequest>> violations =
                    validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("[Validation] 페이지 크기가 100이면 위반 없음")
        void validate_withMaxSize_shouldHaveNoViolations() {
            // Given
            SearchTenantsApiRequest request =
                    new SearchTenantsApiRequest(null, null, ONE_MONTH_AGO, NOW, 0, 100);

            // When
            Set<ConstraintViolation<SearchTenantsApiRequest>> violations =
                    validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("[Validation] 페이지 크기가 101이면 위반 발생")
        void validate_withTooLargeSize_shouldHaveViolation() {
            // Given
            SearchTenantsApiRequest request =
                    new SearchTenantsApiRequest(null, null, ONE_MONTH_AGO, NOW, 0, 101);

            // When
            Set<ConstraintViolation<SearchTenantsApiRequest>> violations =
                    validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("페이지 크기는 100 이하여야 합니다");
        }
    }

    @Nested
    @DisplayName("기본값 메서드 테스트")
    class DefaultMethodTest {

        @Test
        @DisplayName("[pageOrDefault] page가 null이면 기본값 반환")
        void pageOrDefault_withNullPage_shouldReturnDefault() {
            // Given
            SearchTenantsApiRequest request =
                    new SearchTenantsApiRequest(null, null, ONE_MONTH_AGO, NOW, null, null);

            // When & Then
            assertThat(request.pageOrDefault()).isEqualTo(SearchTenantsApiRequest.DEFAULT_PAGE);
        }

        @Test
        @DisplayName("[sizeOrDefault] size가 null이면 기본값 반환")
        void sizeOrDefault_withNullSize_shouldReturnDefault() {
            // Given
            SearchTenantsApiRequest request =
                    new SearchTenantsApiRequest(null, null, ONE_MONTH_AGO, NOW, null, null);

            // When & Then
            assertThat(request.sizeOrDefault())
                    .isEqualTo(SearchTenantsApiRequest.DEFAULT_PAGE_SIZE);
        }
    }
}
