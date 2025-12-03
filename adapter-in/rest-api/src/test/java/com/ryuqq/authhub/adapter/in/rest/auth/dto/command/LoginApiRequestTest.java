package com.ryuqq.authhub.adapter.in.rest.auth.dto.command;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

/**
 * LoginApiRequest 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@Tag("adapter-rest")
@Tag("dto")
@DisplayName("LoginApiRequest 테스트")
class LoginApiRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Nested
    @DisplayName("생성 테스트")
    class CreateTest {

        @Test
        @DisplayName("유효한 데이터로 생성 성공")
        void givenValidData_whenCreate_thenSuccess() {
            // given
            Long tenantId = 1L;
            String identifier = "user@example.com";
            String password = "password123";

            // when
            LoginApiRequest request = new LoginApiRequest(tenantId, identifier, password);

            // then
            assertThat(request.tenantId()).isEqualTo(tenantId);
            assertThat(request.identifier()).isEqualTo(identifier);
            assertThat(request.password()).isEqualTo(password);
        }
    }

    @Nested
    @DisplayName("Bean Validation 테스트")
    class ValidationTest {

        @Test
        @DisplayName("tenantId가 null이면 위반")
        void givenNullTenantId_whenValidate_thenViolation() {
            // given
            LoginApiRequest request = new LoginApiRequest(null, "user@example.com", "password123");

            // when
            Set<ConstraintViolation<LoginApiRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .isEqualTo("테넌트 ID는 필수입니다");
        }

        @Test
        @DisplayName("identifier가 null이면 위반")
        void givenNullIdentifier_whenValidate_thenViolation() {
            // given
            LoginApiRequest request = new LoginApiRequest(1L, null, "password123");

            // when
            Set<ConstraintViolation<LoginApiRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .isEqualTo("식별자는 필수입니다");
        }

        @Test
        @DisplayName("identifier가 빈 문자열이면 위반")
        void givenEmptyIdentifier_whenValidate_thenViolation() {
            // given
            LoginApiRequest request = new LoginApiRequest(1L, "", "password123");

            // when
            Set<ConstraintViolation<LoginApiRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isNotEmpty();
        }

        @Test
        @DisplayName("password가 null이면 위반")
        void givenNullPassword_whenValidate_thenViolation() {
            // given
            LoginApiRequest request = new LoginApiRequest(1L, "user@example.com", null);

            // when
            Set<ConstraintViolation<LoginApiRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .isEqualTo("비밀번호는 필수입니다");
        }

        @Test
        @DisplayName("password가 빈 문자열이면 위반")
        void givenEmptyPassword_whenValidate_thenViolation() {
            // given
            LoginApiRequest request = new LoginApiRequest(1L, "user@example.com", "");

            // when
            Set<ConstraintViolation<LoginApiRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isNotEmpty();
        }

        @Test
        @DisplayName("유효한 데이터면 위반 없음")
        void givenValidData_whenValidate_thenNoViolation() {
            // given
            LoginApiRequest request = new LoginApiRequest(1L, "user@example.com", "password123");

            // when
            Set<ConstraintViolation<LoginApiRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isEmpty();
        }
    }
}
