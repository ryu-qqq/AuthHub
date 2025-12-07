package com.ryuqq.authhub.adapter.in.rest.tenant.dto.command;

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
 * CreateTenantApiRequest 단위 테스트
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
@DisplayName("CreateTenantApiRequest 단위 테스트")
@Tag("unit")
@Tag("adapter-rest")
class CreateTenantApiRequestTest {

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
        @DisplayName("[생성] 유효한 이름으로 생성 시 정상 생성")
        void create_withValidName_shouldCreateSuccessfully() {
            // Given
            String name = "TestTenant";

            // When
            CreateTenantApiRequest request = new CreateTenantApiRequest(name);

            // Then
            assertThat(request.name()).isEqualTo(name);
        }
    }

    @Nested
    @DisplayName("Bean Validation 테스트")
    class ValidationTest {

        @Test
        @DisplayName("[Validation] 유효한 이름이면 위반 없음")
        void validate_withValidName_shouldHaveNoViolations() {
            // Given
            CreateTenantApiRequest request = new CreateTenantApiRequest("TestTenant");

            // When
            Set<ConstraintViolation<CreateTenantApiRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("[Validation] 이름이 null이면 위반 발생")
        void validate_withNullName_shouldHaveViolation() {
            // Given
            CreateTenantApiRequest request = new CreateTenantApiRequest(null);

            // When
            Set<ConstraintViolation<CreateTenantApiRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("테넌트 이름은 필수입니다");
        }

        @Test
        @DisplayName("[Validation] 이름이 빈 문자열이면 위반 발생")
        void validate_withEmptyName_shouldHaveViolation() {
            // Given
            CreateTenantApiRequest request = new CreateTenantApiRequest("");

            // When
            Set<ConstraintViolation<CreateTenantApiRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isNotEmpty();
        }

        @Test
        @DisplayName("[Validation] 이름이 1자이면 위반 발생 (최소 2자)")
        void validate_withTooShortName_shouldHaveViolation() {
            // Given
            CreateTenantApiRequest request = new CreateTenantApiRequest("A");

            // When
            Set<ConstraintViolation<CreateTenantApiRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .isEqualTo("테넌트 이름은 2자 이상 100자 이하여야 합니다");
        }

        @Test
        @DisplayName("[Validation] 이름이 2자이면 위반 없음")
        void validate_withMinLengthName_shouldHaveNoViolations() {
            // Given
            CreateTenantApiRequest request = new CreateTenantApiRequest("AB");

            // When
            Set<ConstraintViolation<CreateTenantApiRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("[Validation] 이름이 100자이면 위반 없음")
        void validate_withMaxLengthName_shouldHaveNoViolations() {
            // Given
            String name = "A".repeat(100);
            CreateTenantApiRequest request = new CreateTenantApiRequest(name);

            // When
            Set<ConstraintViolation<CreateTenantApiRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("[Validation] 이름이 101자이면 위반 발생 (최대 100자)")
        void validate_withTooLongName_shouldHaveViolation() {
            // Given
            String name = "A".repeat(101);
            CreateTenantApiRequest request = new CreateTenantApiRequest(name);

            // When
            Set<ConstraintViolation<CreateTenantApiRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .isEqualTo("테넌트 이름은 2자 이상 100자 이하여야 합니다");
        }
    }
}
