package com.ryuqq.authhub.domain.security.blacklist.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * BlacklistReason 단위 테스트.
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@DisplayName("BlacklistReason 단위 테스트")
class BlacklistReasonTest {

    @Test
    @DisplayName("LOGOUT 사유는 정상 로그아웃으로 식별된다")
    void logout_ShouldBeIdentifiedAsLogout() {
        // given
        final BlacklistReason reason = BlacklistReason.LOGOUT;

        // when & then
        assertThat(reason.isLogout()).isTrue();
        assertThat(reason.isForceLogout()).isFalse();
        assertThat(reason.isSecurityBreach()).isFalse();
        assertThat(reason.isPasswordChange()).isFalse();
    }

    @Test
    @DisplayName("FORCE_LOGOUT 사유는 강제 로그아웃으로 식별된다")
    void forceLogout_ShouldBeIdentifiedAsForceLogout() {
        // given
        final BlacklistReason reason = BlacklistReason.FORCE_LOGOUT;

        // when & then
        assertThat(reason.isLogout()).isFalse();
        assertThat(reason.isForceLogout()).isTrue();
        assertThat(reason.isSecurityBreach()).isFalse();
        assertThat(reason.isPasswordChange()).isFalse();
    }

    @Test
    @DisplayName("SECURITY_BREACH 사유는 보안 침해로 식별된다")
    void securityBreach_ShouldBeIdentifiedAsSecurityBreach() {
        // given
        final BlacklistReason reason = BlacklistReason.SECURITY_BREACH;

        // when & then
        assertThat(reason.isLogout()).isFalse();
        assertThat(reason.isForceLogout()).isFalse();
        assertThat(reason.isSecurityBreach()).isTrue();
        assertThat(reason.isPasswordChange()).isFalse();
    }

    @Test
    @DisplayName("PASSWORD_CHANGE 사유는 비밀번호 변경으로 식별된다")
    void passwordChange_ShouldBeIdentifiedAsPasswordChange() {
        // given
        final BlacklistReason reason = BlacklistReason.PASSWORD_CHANGE;

        // when & then
        assertThat(reason.isLogout()).isFalse();
        assertThat(reason.isForceLogout()).isFalse();
        assertThat(reason.isSecurityBreach()).isFalse();
        assertThat(reason.isPasswordChange()).isTrue();
    }

    @Test
    @DisplayName("SECURITY_BREACH는 보안 조치가 필요한 사유로 식별된다")
    void securityBreach_ShouldRequireSecurityAction() {
        // given
        final BlacklistReason reason = BlacklistReason.SECURITY_BREACH;

        // when & then
        assertThat(reason.requiresSecurityAction()).isTrue();
    }

    @Test
    @DisplayName("FORCE_LOGOUT는 보안 조치가 필요한 사유로 식별된다")
    void forceLogout_ShouldRequireSecurityAction() {
        // given
        final BlacklistReason reason = BlacklistReason.FORCE_LOGOUT;

        // when & then
        assertThat(reason.requiresSecurityAction()).isTrue();
    }

    @Test
    @DisplayName("LOGOUT는 보안 조치가 필요하지 않은 사유로 식별된다")
    void logout_ShouldNotRequireSecurityAction() {
        // given
        final BlacklistReason reason = BlacklistReason.LOGOUT;

        // when & then
        assertThat(reason.requiresSecurityAction()).isFalse();
    }

    @Test
    @DisplayName("PASSWORD_CHANGE는 보안 조치가 필요하지 않은 사유로 식별된다")
    void passwordChange_ShouldNotRequireSecurityAction() {
        // given
        final BlacklistReason reason = BlacklistReason.PASSWORD_CHANGE;

        // when & then
        assertThat(reason.requiresSecurityAction()).isFalse();
    }

    @Test
    @DisplayName("각 사유는 고유한 표시 이름을 가진다")
    void allReasons_ShouldHaveUniqueDisplayNames() {
        // when & then
        assertThat(BlacklistReason.LOGOUT.getDisplayName()).isEqualTo("정상 로그아웃");
        assertThat(BlacklistReason.FORCE_LOGOUT.getDisplayName()).isEqualTo("강제 로그아웃");
        assertThat(BlacklistReason.SECURITY_BREACH.getDisplayName()).isEqualTo("보안 침해");
        assertThat(BlacklistReason.PASSWORD_CHANGE.getDisplayName()).isEqualTo("비밀번호 변경");
    }

    @Test
    @DisplayName("각 사유는 상세 설명을 가진다")
    void allReasons_ShouldHaveDescriptions() {
        // when & then
        assertThat(BlacklistReason.LOGOUT.getDescription()).isNotBlank();
        assertThat(BlacklistReason.FORCE_LOGOUT.getDescription()).isNotBlank();
        assertThat(BlacklistReason.SECURITY_BREACH.getDescription()).isNotBlank();
        assertThat(BlacklistReason.PASSWORD_CHANGE.getDescription()).isNotBlank();
    }

    @Test
    @DisplayName("모든 사유가 정의되어 있다")
    void values_ShouldReturnAllReasons() {
        // when
        final BlacklistReason[] reasons = BlacklistReason.values();

        // then
        assertThat(reasons).hasSize(4);
        assertThat(reasons).containsExactlyInAnyOrder(
                BlacklistReason.LOGOUT,
                BlacklistReason.FORCE_LOGOUT,
                BlacklistReason.SECURITY_BREACH,
                BlacklistReason.PASSWORD_CHANGE
        );
    }

    @Test
    @DisplayName("valueOf()로 이름으로부터 사유를 찾을 수 있다")
    void valueOf_ShouldFindReasonByName() {
        // when
        final BlacklistReason reason = BlacklistReason.valueOf("LOGOUT");

        // then
        assertThat(reason).isEqualTo(BlacklistReason.LOGOUT);
    }
}
