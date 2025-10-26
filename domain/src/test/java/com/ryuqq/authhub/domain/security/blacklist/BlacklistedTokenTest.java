package com.ryuqq.authhub.domain.security.blacklist;

import com.ryuqq.authhub.domain.security.blacklist.vo.BlacklistedTokenId;
import com.ryuqq.authhub.domain.security.blacklist.vo.BlacklistReason;
import com.ryuqq.authhub.domain.security.blacklist.vo.ExpiresAt;
import com.ryuqq.authhub.domain.security.blacklist.vo.Jti;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * BlacklistedToken Aggregate 단위 테스트.
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@DisplayName("BlacklistedToken Aggregate 단위 테스트")
class BlacklistedTokenTest {

    @Test
    @DisplayName("create()로 새로운 BlacklistedToken을 생성할 수 있다")
    void create_ShouldCreateNewBlacklistedToken() {
        // given
        final Jti jti = Jti.of("test-jti-123");
        final ExpiresAt expiresAt = ExpiresAt.of(Instant.now().plus(1, ChronoUnit.HOURS));
        final BlacklistReason reason = BlacklistReason.LOGOUT;

        // when
        final BlacklistedToken token = BlacklistedToken.create(jti, expiresAt, reason);

        // then
        assertThat(token).isNotNull();
        assertThat(token.getId()).isNotNull();
        assertThat(token.getJti()).isEqualTo(jti);
        assertThat(token.getExpiresAt()).isEqualTo(expiresAt);
        assertThat(token.getReason()).isEqualTo(reason);
        assertThat(token.getBlacklistedAt()).isNotNull();
    }

    @Test
    @DisplayName("create()는 null JTI를 거부한다")
    void create_ShouldRejectNullJti() {
        // given
        final ExpiresAt expiresAt = ExpiresAt.of(Instant.now());
        final BlacklistReason reason = BlacklistReason.LOGOUT;

        // when & then
        assertThatThrownBy(() -> BlacklistedToken.create(null, expiresAt, reason))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("JTI cannot be null");
    }

    @Test
    @DisplayName("create()는 null ExpiresAt을 거부한다")
    void create_ShouldRejectNullExpiresAt() {
        // given
        final Jti jti = Jti.of("test-jti");
        final BlacklistReason reason = BlacklistReason.LOGOUT;

        // when & then
        assertThatThrownBy(() -> BlacklistedToken.create(jti, null, reason))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("ExpiresAt cannot be null");
    }

    @Test
    @DisplayName("create()는 null BlacklistReason을 거부한다")
    void create_ShouldRejectNullReason() {
        // given
        final Jti jti = Jti.of("test-jti");
        final ExpiresAt expiresAt = ExpiresAt.of(Instant.now());

        // when & then
        assertThatThrownBy(() -> BlacklistedToken.create(jti, expiresAt, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("BlacklistReason cannot be null");
    }

    @Test
    @DisplayName("reconstruct()로 기존 데이터로부터 BlacklistedToken을 재구성할 수 있다")
    void reconstruct_ShouldRecreateBlacklistedTokenFromExistingData() {
        // given
        final BlacklistedTokenId id = BlacklistedTokenId.newId();
        final Jti jti = Jti.of("existing-jti");
        final ExpiresAt expiresAt = ExpiresAt.of(Instant.now().plus(1, ChronoUnit.HOURS));
        final BlacklistReason reason = BlacklistReason.SECURITY_BREACH;
        final Instant blacklistedAt = Instant.now().minus(10, ChronoUnit.MINUTES);

        // when
        final BlacklistedToken token = BlacklistedToken.reconstruct(
                id, jti, expiresAt, reason, blacklistedAt
        );

        // then
        assertThat(token).isNotNull();
        assertThat(token.getId()).isEqualTo(id);
        assertThat(token.getJti()).isEqualTo(jti);
        assertThat(token.getExpiresAt()).isEqualTo(expiresAt);
        assertThat(token.getReason()).isEqualTo(reason);
        assertThat(token.getBlacklistedAt()).isEqualTo(blacklistedAt);
    }

    @Test
    @DisplayName("reconstruct()는 null ID를 거부한다")
    void reconstruct_ShouldRejectNullId() {
        // given
        final Jti jti = Jti.of("test-jti");
        final ExpiresAt expiresAt = ExpiresAt.of(Instant.now());
        final BlacklistReason reason = BlacklistReason.LOGOUT;
        final Instant blacklistedAt = Instant.now();

        // when & then
        assertThatThrownBy(() -> BlacklistedToken.reconstruct(
                null, jti, expiresAt, reason, blacklistedAt
        ))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("BlacklistedToken ID cannot be null");
    }

    @Test
    @DisplayName("isExpired()는 만료된 토큰에 대해 true를 반환한다")
    void isExpired_ShouldReturnTrueForExpiredToken() {
        // given
        final Jti jti = Jti.of("expired-jti");
        final ExpiresAt expiresAt = ExpiresAt.of(Instant.now().minus(1, ChronoUnit.HOURS));
        final BlacklistReason reason = BlacklistReason.LOGOUT;
        final BlacklistedToken token = BlacklistedToken.create(jti, expiresAt, reason);

        // when
        final boolean expired = token.isExpired();

        // then
        assertThat(expired).isTrue();
    }

    @Test
    @DisplayName("isExpired()는 만료되지 않은 토큰에 대해 false를 반환한다")
    void isExpired_ShouldReturnFalseForNonExpiredToken() {
        // given
        final Jti jti = Jti.of("valid-jti");
        final ExpiresAt expiresAt = ExpiresAt.of(Instant.now().plus(1, ChronoUnit.HOURS));
        final BlacklistReason reason = BlacklistReason.LOGOUT;
        final BlacklistedToken token = BlacklistedToken.create(jti, expiresAt, reason);

        // when
        final boolean expired = token.isExpired();

        // then
        assertThat(expired).isFalse();
    }

    @Test
    @DisplayName("로그아웃 사유로 토큰을 블랙리스트에 등록할 수 있다")
    void create_ShouldRegisterTokenForLogout() {
        // given
        final Jti jti = Jti.of("logout-jti");
        final ExpiresAt expiresAt = ExpiresAt.fromEpochSeconds(1735689600L);
        final BlacklistReason reason = BlacklistReason.LOGOUT;

        // when
        final BlacklistedToken token = BlacklistedToken.create(jti, expiresAt, reason);

        // then
        assertThat(token.getReason()).isEqualTo(BlacklistReason.LOGOUT);
        assertThat(token.getReason().isLogout()).isTrue();
    }

    @Test
    @DisplayName("보안 침해 사유로 토큰을 블랙리스트에 등록할 수 있다")
    void create_ShouldRegisterTokenForSecurityBreach() {
        // given
        final Jti jti = Jti.of("breach-jti");
        final ExpiresAt expiresAt = ExpiresAt.fromEpochSeconds(1735689600L);
        final BlacklistReason reason = BlacklistReason.SECURITY_BREACH;

        // when
        final BlacklistedToken token = BlacklistedToken.create(jti, expiresAt, reason);

        // then
        assertThat(token.getReason()).isEqualTo(BlacklistReason.SECURITY_BREACH);
        assertThat(token.getReason().isSecurityBreach()).isTrue();
        assertThat(token.getReason().requiresSecurityAction()).isTrue();
    }

    @Test
    @DisplayName("비밀번호 변경 사유로 토큰을 블랙리스트에 등록할 수 있다")
    void create_ShouldRegisterTokenForPasswordChange() {
        // given
        final Jti jti = Jti.of("password-change-jti");
        final ExpiresAt expiresAt = ExpiresAt.fromEpochSeconds(1735689600L);
        final BlacklistReason reason = BlacklistReason.PASSWORD_CHANGE;

        // when
        final BlacklistedToken token = BlacklistedToken.create(jti, expiresAt, reason);

        // then
        assertThat(token.getReason()).isEqualTo(BlacklistReason.PASSWORD_CHANGE);
        assertThat(token.getReason().isPasswordChange()).isTrue();
    }

    @Test
    @DisplayName("동일한 ID를 가진 BlacklistedToken은 equals()로 같다고 판단된다")
    void equals_ShouldReturnTrueForSameId() {
        // given
        final BlacklistedTokenId id = BlacklistedTokenId.newId();
        final Jti jti = Jti.of("test-jti");
        final ExpiresAt expiresAt = ExpiresAt.of(Instant.now());
        final BlacklistReason reason = BlacklistReason.LOGOUT;
        final Instant blacklistedAt = Instant.now();

        final BlacklistedToken token1 = BlacklistedToken.reconstruct(
                id, jti, expiresAt, reason, blacklistedAt
        );
        final BlacklistedToken token2 = BlacklistedToken.reconstruct(
                id, jti, expiresAt, reason, blacklistedAt
        );

        // when & then
        assertThat(token1).isEqualTo(token2);
    }

    @Test
    @DisplayName("다른 ID를 가진 BlacklistedToken은 equals()로 다르다고 판단된다")
    void equals_ShouldReturnFalseForDifferentId() {
        // given
        final Jti jti = Jti.of("test-jti");
        final ExpiresAt expiresAt = ExpiresAt.of(Instant.now());
        final BlacklistReason reason = BlacklistReason.LOGOUT;

        final BlacklistedToken token1 = BlacklistedToken.create(jti, expiresAt, reason);
        final BlacklistedToken token2 = BlacklistedToken.create(jti, expiresAt, reason);

        // when & then
        assertThat(token1).isNotEqualTo(token2);
    }

    @Test
    @DisplayName("동일한 ID를 가진 BlacklistedToken은 같은 hashCode를 반환한다")
    void hashCode_ShouldReturnSameHashCodeForSameId() {
        // given
        final BlacklistedTokenId id = BlacklistedTokenId.newId();
        final Jti jti = Jti.of("test-jti");
        final ExpiresAt expiresAt = ExpiresAt.of(Instant.now());
        final BlacklistReason reason = BlacklistReason.LOGOUT;
        final Instant blacklistedAt = Instant.now();

        final BlacklistedToken token1 = BlacklistedToken.reconstruct(
                id, jti, expiresAt, reason, blacklistedAt
        );
        final BlacklistedToken token2 = BlacklistedToken.reconstruct(
                id, jti, expiresAt, reason, blacklistedAt
        );

        // when & then
        assertThat(token1.hashCode()).isEqualTo(token2.hashCode());
    }

    @Test
    @DisplayName("toString()은 모든 필드를 포함한 문자열 표현을 반환한다")
    void toString_ShouldIncludeAllFields() {
        // given
        final Jti jti = Jti.of("test-jti");
        final ExpiresAt expiresAt = ExpiresAt.of(Instant.now());
        final BlacklistReason reason = BlacklistReason.LOGOUT;
        final BlacklistedToken token = BlacklistedToken.create(jti, expiresAt, reason);

        // when
        final String result = token.toString();

        // then
        assertThat(result).contains("BlacklistedToken");
        assertThat(result).contains("id=");
        assertThat(result).contains("jti=");
        assertThat(result).contains("expiresAt=");
        assertThat(result).contains("reason=");
        assertThat(result).contains("blacklistedAt=");
    }

    @Test
    @DisplayName("Aggregate는 불변 객체여야 한다")
    void aggregate_ShouldBeImmutable() {
        // given
        final Jti jti = Jti.of("immutable-test");
        final ExpiresAt expiresAt = ExpiresAt.of(Instant.now());
        final BlacklistReason reason = BlacklistReason.LOGOUT;
        final BlacklistedToken token = BlacklistedToken.create(jti, expiresAt, reason);

        // when - 모든 getter 호출
        final BlacklistedTokenId id = token.getId();
        final Jti retrievedJti = token.getJti();
        final ExpiresAt retrievedExpiresAt = token.getExpiresAt();
        final BlacklistReason retrievedReason = token.getReason();
        final Instant retrievedBlacklistedAt = token.getBlacklistedAt();

        // then - 모든 필드가 동일한 객체를 반환
        assertThat(token.getId()).isEqualTo(id);
        assertThat(token.getJti()).isEqualTo(retrievedJti);
        assertThat(token.getExpiresAt()).isEqualTo(retrievedExpiresAt);
        assertThat(token.getReason()).isEqualTo(retrievedReason);
        assertThat(token.getBlacklistedAt()).isEqualTo(retrievedBlacklistedAt);
    }
}
