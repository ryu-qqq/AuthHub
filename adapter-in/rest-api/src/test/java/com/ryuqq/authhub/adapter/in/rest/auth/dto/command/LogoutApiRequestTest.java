package com.ryuqq.authhub.adapter.in.rest.auth.dto.command;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.UUID;

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
 * LogoutApiRequest 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@Tag("adapter-rest")
@Tag("dto")
@DisplayName("LogoutApiRequest 테스트")
class LogoutApiRequestTest {

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
            UUID userId = UUID.randomUUID();

            // when
            LogoutApiRequest request = new LogoutApiRequest(userId);

            // then
            assertThat(request.userId()).isEqualTo(userId);
        }
    }

    @Nested
    @DisplayName("Bean Validation 테스트")
    class ValidationTest {

        @Test
        @DisplayName("userId가 null이면 위반")
        void givenNullUserId_whenValidate_thenViolation() {
            // given
            LogoutApiRequest request = new LogoutApiRequest(null);

            // when
            Set<ConstraintViolation<LogoutApiRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .isEqualTo("사용자 ID는 필수입니다");
        }

        @Test
        @DisplayName("유효한 데이터면 위반 없음")
        void givenValidData_whenValidate_thenNoViolation() {
            // given
            LogoutApiRequest request = new LogoutApiRequest(UUID.randomUUID());

            // when
            Set<ConstraintViolation<LogoutApiRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isEmpty();
        }
    }
}
