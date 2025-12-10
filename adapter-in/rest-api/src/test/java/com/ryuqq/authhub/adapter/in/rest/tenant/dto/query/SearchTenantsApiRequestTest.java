package com.ryuqq.authhub.adapter.in.rest.tenant.dto.query;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
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
            SearchTenantsApiRequest request = new SearchTenantsApiRequest("Test", "ACTIVE", 0, 20);

            // Then
            assertThat(request.name()).isEqualTo("Test");
            assertThat(request.status()).isEqualTo("ACTIVE");
            assertThat(request.page()).isEqualTo(0);
            assertThat(request.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("[생성] null 필드로 생성 시 정상 생성")
        void create_withNullFields_shouldCreateSuccessfully() {
            // Given & When
            SearchTenantsApiRequest request = new SearchTenantsApiRequest(null, null, null, null);

            // Then
            assertThat(request.name()).isNull();
            assertThat(request.status()).isNull();
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
            SearchTenantsApiRequest request = new SearchTenantsApiRequest("Test", "ACTIVE", 0, 20);

            // When
            Set<ConstraintViolation<SearchTenantsApiRequest>> violations =
                    validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("[Validation] 모든 필드가 null이어도 위반 없음 (선택적 필드)")
        void validate_withNullFields_shouldHaveNoViolations() {
            // Given
            SearchTenantsApiRequest request = new SearchTenantsApiRequest(null, null, null, null);

            // When
            Set<ConstraintViolation<SearchTenantsApiRequest>> violations =
                    validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("[Validation] 페이지 번호가 음수면 위반 발생")
        void validate_withNegativePage_shouldHaveViolation() {
            // Given
            SearchTenantsApiRequest request = new SearchTenantsApiRequest(null, null, -1, 20);

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
            SearchTenantsApiRequest request = new SearchTenantsApiRequest(null, null, 0, 20);

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
            SearchTenantsApiRequest request = new SearchTenantsApiRequest(null, null, 0, 0);

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
            SearchTenantsApiRequest request = new SearchTenantsApiRequest(null, null, 0, 1);

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
            SearchTenantsApiRequest request = new SearchTenantsApiRequest(null, null, 0, 100);

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
            SearchTenantsApiRequest request = new SearchTenantsApiRequest(null, null, 0, 101);

            // When
            Set<ConstraintViolation<SearchTenantsApiRequest>> violations =
                    validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("페이지 크기는 100 이하여야 합니다");
        }
    }
}
