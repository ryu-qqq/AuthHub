package com.ryuqq.authhub.domain.tenant.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import com.ryuqq.authhub.domain.tenant.vo.TenantStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * InvalidTenantStateException 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("InvalidTenantStateException 테스트")
class InvalidTenantStateExceptionTest {

    @Nested
    @DisplayName("상태 전이 생성자")
    class StateTransitionConstructorTest {

        @Test
        @DisplayName("현재 상태와 대상 상태로 예외를 생성한다")
        void shouldCreateExceptionWithStateTransition() {
            // given
            TenantStatus current = TenantStatus.DELETED;
            TenantStatus target = TenantStatus.ACTIVE;

            // when
            InvalidTenantStateException exception =
                    new InvalidTenantStateException(current, target);

            // then
            assertThat(exception).isNotNull();
            assertThat(exception.code()).isEqualTo("TENANT-002");
            assertThat(exception.args()).containsEntry("currentStatus", "DELETED");
            assertThat(exception.args()).containsEntry("targetStatus", "ACTIVE");
        }
    }

    @Nested
    @DisplayName("이유 포함 생성자")
    class ReasonConstructorTest {

        @Test
        @DisplayName("현재 상태와 이유로 예외를 생성한다")
        void shouldCreateExceptionWithReason() {
            // given
            TenantStatus current = TenantStatus.DELETED;
            String reason = "삭제된 테넌트는 수정할 수 없습니다";

            // when
            InvalidTenantStateException exception =
                    new InvalidTenantStateException(current, reason);

            // then
            assertThat(exception).isNotNull();
            assertThat(exception.code()).isEqualTo("TENANT-002");
            assertThat(exception.args()).containsEntry("currentStatus", "DELETED");
            assertThat(exception.args()).containsEntry("reason", reason);
        }
    }

    @Nested
    @DisplayName("상속 관계")
    class InheritanceTest {

        @Test
        @DisplayName("DomainException을 상속한다")
        void shouldExtendDomainException() {
            // given
            InvalidTenantStateException exception =
                    new InvalidTenantStateException(TenantStatus.DELETED, TenantStatus.ACTIVE);

            // then
            assertThat(exception).isInstanceOf(DomainException.class);
            assertThat(exception).isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("올바른 에러 코드를 사용한다")
        void shouldUseCorrectErrorCode() {
            // given
            InvalidTenantStateException exception =
                    new InvalidTenantStateException(TenantStatus.DELETED, TenantStatus.ACTIVE);

            // then
            assertThat(exception.code()).isEqualTo(TenantErrorCode.INVALID_TENANT_STATE.getCode());
            assertThat(exception.getMessage())
                    .isEqualTo(TenantErrorCode.INVALID_TENANT_STATE.getMessage());
        }
    }
}
