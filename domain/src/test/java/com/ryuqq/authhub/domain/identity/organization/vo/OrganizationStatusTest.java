package com.ryuqq.authhub.domain.identity.organization.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * OrganizationStatus Enum 단위 테스트.
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@DisplayName("OrganizationStatus 단위 테스트")
class OrganizationStatusTest {

    @Test
    @DisplayName("ACTIVE 상태를 가져올 수 있다")
    void testActiveStatus() {
        // when
        OrganizationStatus status = OrganizationStatus.ACTIVE;

        // then
        assertThat(status).isNotNull();
        assertThat(status.getDescription()).isEqualTo("활성");
        assertThat(status.isActive()).isTrue();
        assertThat(status.isSuspended()).isFalse();
        assertThat(status.isDeleted()).isFalse();
        assertThat(status.isOperational()).isTrue();
    }

    @Test
    @DisplayName("SUSPENDED 상태를 가져올 수 있다")
    void testSuspendedStatus() {
        // when
        OrganizationStatus status = OrganizationStatus.SUSPENDED;

        // then
        assertThat(status).isNotNull();
        assertThat(status.getDescription()).isEqualTo("정지");
        assertThat(status.isActive()).isFalse();
        assertThat(status.isSuspended()).isTrue();
        assertThat(status.isDeleted()).isFalse();
        assertThat(status.isOperational()).isFalse();
    }

    @Test
    @DisplayName("DELETED 상태를 가져올 수 있다")
    void testDeletedStatus() {
        // when
        OrganizationStatus status = OrganizationStatus.DELETED;

        // then
        assertThat(status).isNotNull();
        assertThat(status.getDescription()).isEqualTo("삭제");
        assertThat(status.isActive()).isFalse();
        assertThat(status.isSuspended()).isFalse();
        assertThat(status.isDeleted()).isTrue();
        assertThat(status.isOperational()).isFalse();
    }

    @Test
    @DisplayName("fromString()으로 문자열로부터 OrganizationStatus를 생성할 수 있다")
    void testFromString() {
        // when
        OrganizationStatus active = OrganizationStatus.fromString("ACTIVE");
        OrganizationStatus suspended = OrganizationStatus.fromString("SUSPENDED");
        OrganizationStatus deleted = OrganizationStatus.fromString("DELETED");

        // then
        assertThat(active).isEqualTo(OrganizationStatus.ACTIVE);
        assertThat(suspended).isEqualTo(OrganizationStatus.SUSPENDED);
        assertThat(deleted).isEqualTo(OrganizationStatus.DELETED);
    }

    @Test
    @DisplayName("fromString()에 null을 전달하면 예외가 발생한다")
    void testFromStringWithNull() {
        // when & then
        assertThatThrownBy(() -> OrganizationStatus.fromString(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be null");
    }

    @Test
    @DisplayName("fromString()에 잘못된 상태를 전달하면 예외가 발생한다")
    void testFromStringWithInvalidStatus() {
        // when & then
        assertThatThrownBy(() -> OrganizationStatus.fromString("INVALID"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid organization status");
    }

    @Test
    @DisplayName("isValid()로 유효한 상태 문자열을 검증할 수 있다")
    void testIsValidWithValidStatus() {
        // when & then
        assertThat(OrganizationStatus.isValid("ACTIVE")).isTrue();
        assertThat(OrganizationStatus.isValid("SUSPENDED")).isTrue();
        assertThat(OrganizationStatus.isValid("DELETED")).isTrue();
    }

    @Test
    @DisplayName("isValid()로 잘못된 상태 문자열을 검증할 수 있다")
    void testIsValidWithInvalidStatus() {
        // when & then
        assertThat(OrganizationStatus.isValid(null)).isFalse();
        assertThat(OrganizationStatus.isValid("INVALID")).isFalse();
    }
}
