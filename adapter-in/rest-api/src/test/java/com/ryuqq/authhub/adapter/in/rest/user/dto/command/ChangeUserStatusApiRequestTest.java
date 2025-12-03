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
 * ChangeUserStatusApiRequest 단위 테스트
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
@DisplayName("ChangeUserStatusApiRequest 테스트")
class ChangeUserStatusApiRequestTest {

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
        @DisplayName("모든 필드로 인스턴스 생성")
        void givenAllFields_whenCreate_thenSuccess() {
            // given
            String targetStatus = "INACTIVE";
            String reason = "휴면 계정 전환";

            // when
            ChangeUserStatusApiRequest request = new ChangeUserStatusApiRequest(targetStatus, reason);

            // then
            assertThat(request.targetStatus()).isEqualTo(targetStatus);
            assertThat(request.reason()).isEqualTo(reason);
        }

        @Test
        @DisplayName("reason null - 인스턴스 생성")
        void givenNullReason_whenCreate_thenSuccess() {
            // given
            String targetStatus = "SUSPENDED";

            // when
            ChangeUserStatusApiRequest request = new ChangeUserStatusApiRequest(targetStatus, null);

            // then
            assertThat(request.targetStatus()).isEqualTo(targetStatus);
            assertThat(request.reason()).isNull();
        }
    }

    @Nested
    @DisplayName("Validation 테스트")
    class ValidationTest {

        @Test
        @DisplayName("유효한 요청 - 검증 통과")
        void givenValidRequest_whenValidate_thenNoViolations() {
            // given
            ChangeUserStatusApiRequest request = new ChangeUserStatusApiRequest(
                    "INACTIVE", "휴면 계정 전환");

            // when
            Set<ConstraintViolation<ChangeUserStatusApiRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("reason null - 검증 통과 (선택 필드)")
        void givenNullReason_whenValidate_thenNoViolations() {
            // given
            ChangeUserStatusApiRequest request = new ChangeUserStatusApiRequest("INACTIVE", null);

            // when
            Set<ConstraintViolation<ChangeUserStatusApiRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("targetStatus blank - 검증 실패")
        void givenBlankTargetStatus_whenValidate_thenViolation() {
            // given
            ChangeUserStatusApiRequest request = new ChangeUserStatusApiRequest("", "reason");

            // when
            Set<ConstraintViolation<ChangeUserStatusApiRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .contains("대상 상태는 필수입니다");
        }
    }
}
