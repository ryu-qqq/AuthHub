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
 * RefreshTokenApiRequest 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@Tag("adapter-rest")
@Tag("dto")
@DisplayName("RefreshTokenApiRequest 테스트")
class RefreshTokenApiRequestTest {

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
            String refreshToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.refreshToken";

            // when
            RefreshTokenApiRequest request = new RefreshTokenApiRequest(refreshToken);

            // then
            assertThat(request.refreshToken()).isEqualTo(refreshToken);
        }
    }

    @Nested
    @DisplayName("Bean Validation 테스트")
    class ValidationTest {

        @Test
        @DisplayName("refreshToken이 null이면 위반")
        void givenNullRefreshToken_whenValidate_thenViolation() {
            // given
            RefreshTokenApiRequest request = new RefreshTokenApiRequest(null);

            // when
            Set<ConstraintViolation<RefreshTokenApiRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .isEqualTo("리프레시 토큰은 필수입니다");
        }

        @Test
        @DisplayName("refreshToken이 빈 문자열이면 위반")
        void givenEmptyRefreshToken_whenValidate_thenViolation() {
            // given
            RefreshTokenApiRequest request = new RefreshTokenApiRequest("");

            // when
            Set<ConstraintViolation<RefreshTokenApiRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isNotEmpty();
        }

        @Test
        @DisplayName("유효한 데이터면 위반 없음")
        void givenValidData_whenValidate_thenNoViolation() {
            // given
            RefreshTokenApiRequest request = new RefreshTokenApiRequest("validRefreshToken");

            // when
            Set<ConstraintViolation<RefreshTokenApiRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isEmpty();
        }
    }
}
