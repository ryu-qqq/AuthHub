package com.ryuqq.authhub.domain.organization.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.organization.vo.OrganizationStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * InvalidOrganizationStateException 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("InvalidOrganizationStateException 테스트")
class InvalidOrganizationStateExceptionTest {

    @Nested
    @DisplayName("currentStatus + reason 생성자")
    class StatusReasonConstructorTest {

        @Test
        @DisplayName("현재 상태와 이유로 예외를 생성한다")
        void shouldCreateExceptionWithStatusAndReason() {
            // given
            OrganizationStatus currentStatus = OrganizationStatus.DELETED;
            String reason = "Cannot modify deleted organization";

            // when
            InvalidOrganizationStateException exception =
                    new InvalidOrganizationStateException(currentStatus, reason);

            // then
            assertThat(exception).isNotNull();
            assertThat(exception.code()).isEqualTo("ORG-002");
            assertThat(exception.args()).containsEntry("currentStatus", "DELETED");
            assertThat(exception.args()).containsEntry("reason", reason);
        }
    }

    @Nested
    @DisplayName("currentStatus + targetStatus 생성자")
    class StatusTransitionConstructorTest {

        @Test
        @DisplayName("현재 상태와 대상 상태로 예외를 생성한다")
        void shouldCreateExceptionWithStatusTransition() {
            // given
            OrganizationStatus currentStatus = OrganizationStatus.DELETED;
            OrganizationStatus targetStatus = OrganizationStatus.ACTIVE;

            // when
            InvalidOrganizationStateException exception =
                    new InvalidOrganizationStateException(currentStatus, targetStatus);

            // then
            assertThat(exception).isNotNull();
            assertThat(exception.code()).isEqualTo("ORG-002");
            assertThat(exception.args()).containsEntry("currentStatus", "DELETED");
            assertThat(exception.args()).containsEntry("targetStatus", "ACTIVE");
        }

        @Test
        @DisplayName("DELETED에서 INACTIVE로 전이 시도 시 예외 정보를 포함한다")
        void shouldContainTransitionInfoForDeletedToInactive() {
            // given
            OrganizationStatus currentStatus = OrganizationStatus.DELETED;
            OrganizationStatus targetStatus = OrganizationStatus.INACTIVE;

            // when
            InvalidOrganizationStateException exception =
                    new InvalidOrganizationStateException(currentStatus, targetStatus);

            // then
            assertThat(exception.args()).containsEntry("currentStatus", "DELETED");
            assertThat(exception.args()).containsEntry("targetStatus", "INACTIVE");
        }
    }

    @Nested
    @DisplayName("상속 관계")
    class InheritanceTest {

        @Test
        @DisplayName("DomainException을 상속한다")
        void shouldExtendDomainException() {
            // given
            InvalidOrganizationStateException exception =
                    new InvalidOrganizationStateException(OrganizationStatus.DELETED, "test");

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception).isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("올바른 에러 코드를 사용한다")
        void shouldUseCorrectErrorCode() {
            // given
            InvalidOrganizationStateException exception =
                    new InvalidOrganizationStateException(OrganizationStatus.DELETED, "test");

            // then
            assertThat(exception.code())
                    .isEqualTo(OrganizationErrorCode.INVALID_ORGANIZATION_STATE.getCode());
            assertThat(exception.getMessage())
                    .isEqualTo(OrganizationErrorCode.INVALID_ORGANIZATION_STATE.getMessage());
        }
    }
}
