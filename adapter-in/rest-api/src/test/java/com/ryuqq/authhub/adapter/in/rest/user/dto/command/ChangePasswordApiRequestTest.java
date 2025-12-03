package com.ryuqq.authhub.adapter.in.rest.user.dto.command;

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
 * ChangePasswordApiRequest 단위 테스트
 *
 * <p>검증 범위:
 * <ul>
 *   <li>Record 불변성 검증</li>
 *   <li>필드 접근 검증</li>
 *   <li>jakarta.validation 검증</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@Tag("adapter-rest")
@Tag("dto")
@DisplayName("ChangePasswordApiRequest 테스트")
class ChangePasswordApiRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Nested
    @DisplayName("Record 기본 동작 테스트")
    class RecordBasicTest {

        @Test
        @DisplayName("일반 비밀번호 변경 - 모든 필드로 인스턴스 생성")
        void givenAllFields_whenCreate_thenSuccess() {
            // given
            String currentPassword = "oldPassword123";
            String newPassword = "newPassword456";

            // when
            ChangePasswordApiRequest request = new ChangePasswordApiRequest(
                    currentPassword, newPassword, false);

            // then
            assertThat(request.currentPassword()).isEqualTo(currentPassword);
            assertThat(request.newPassword()).isEqualTo(newPassword);
            assertThat(request.verified()).isFalse();
        }

        @Test
        @DisplayName("비밀번호 재설정 - verified=true, currentPassword null")
        void givenResetCase_whenCreate_thenSuccess() {
            // given
            String newPassword = "newPassword456";

            // when
            ChangePasswordApiRequest request = new ChangePasswordApiRequest(
                    null, newPassword, true);

            // then
            assertThat(request.currentPassword()).isNull();
            assertThat(request.newPassword()).isEqualTo(newPassword);
            assertThat(request.verified()).isTrue();
        }
    }

    @Nested
    @DisplayName("Validation 테스트")
    class ValidationTest {

        @Test
        @DisplayName("유효한 요청 (일반 변경) - 검증 통과")
        void givenValidChangeRequest_whenValidate_thenNoViolations() {
            // given
            ChangePasswordApiRequest request = new ChangePasswordApiRequest(
                    "oldPassword123", "newPassword456", false);

            // when
            Set<ConstraintViolation<ChangePasswordApiRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("유효한 요청 (비밀번호 재설정) - 검증 통과")
        void givenValidResetRequest_whenValidate_thenNoViolations() {
            // given
            ChangePasswordApiRequest request = new ChangePasswordApiRequest(
                    null, "newPassword456", true);

            // when
            Set<ConstraintViolation<ChangePasswordApiRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("newPassword blank - 검증 실패")
        void givenBlankNewPassword_whenValidate_thenViolation() {
            // given
            ChangePasswordApiRequest request = new ChangePasswordApiRequest(
                    "oldPassword123", "", false);

            // when
            Set<ConstraintViolation<ChangePasswordApiRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .contains("새 비밀번호는 필수입니다");
        }
    }
}
