package com.ryuqq.authhub.domain.auth.token;

import com.ryuqq.authhub.domain.auth.user.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

/**
 * Token Aggregate Root 단위 테스트.
 *
 * <p>Token 도메인 객체의 생성, 유효성 검증, 도메인 규칙을 검증합니다.</p>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@DisplayName("Token Aggregate Root 테스트")
class TokenTest {

    @Nested
    @DisplayName("토큰 생성")
    class CreateTokenTest {

        @Test
        @DisplayName("새로운 토큰을 생성하면 유효한 토큰이 반환된다")
        void create_ShouldReturnValidToken() {
            // given
            UserId userId = UserId.newId();
            TokenType type = TokenType.ACCESS;
            JwtToken jwtToken = JwtToken.from("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.signature");
            Duration validity = Duration.ofMinutes(15);

            // when
            Token token = Token.create(userId, type, jwtToken, validity);

            // then
            assertThat(token).isNotNull();
            assertThat(token.getId()).isNotNull();
            assertThat(token.getUserId()).isEqualTo(userId);
            assertThat(token.getType()).isEqualTo(type);
            assertThat(token.getJwtToken()).isEqualTo(jwtToken);
            assertThat(token.isValid()).isTrue();
        }

        @Test
        @DisplayName("토큰 생성 시 발급/만료 시각이 올바르게 설정된다")
        void create_ShouldSetCorrectTimestamps() {
            // given
            UserId userId = UserId.newId();
            JwtToken jwtToken = JwtToken.from("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.signature");
            Duration validity = Duration.ofHours(1);

            // when
            Instant beforeCreate = Instant.now();
            Token token = Token.create(userId, TokenType.ACCESS, jwtToken, validity);
            Instant afterCreate = Instant.now();

            // then
            assertThat(token.getIssuedAt().value()).isBetween(beforeCreate, afterCreate);
            assertThat(token.getExpiresAt().value())
                    .isAfter(token.getIssuedAt().value())
                    .isBefore(Instant.now().plus(validity).plusSeconds(1)); // 약간의 오차 허용
        }

        @Test
        @DisplayName("null 인자로 토큰 생성 시 예외가 발생한다")
        void create_WithNullArgs_ShouldThrowException() {
            // given
            UserId userId = UserId.newId();
            JwtToken jwtToken = JwtToken.from("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.signature");
            Duration validity = Duration.ofMinutes(15);

            // when & then
            assertThatThrownBy(() -> Token.create(null, TokenType.ACCESS, jwtToken, validity))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("UserId cannot be null");

            assertThatThrownBy(() -> Token.create(userId, null, jwtToken, validity))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("TokenType cannot be null");

            assertThatThrownBy(() -> Token.create(userId, TokenType.ACCESS, null, validity))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("JwtToken cannot be null");
        }
    }

    @Nested
    @DisplayName("토큰 재구성")
    class ReconstructTokenTest {

        @Test
        @DisplayName("기존 데이터로 Token을 재구성할 수 있다")
        void reconstruct_ShouldRestoreToken() {
            // given
            TokenId tokenId = TokenId.newId();
            UserId userId = UserId.newId();
            TokenType type = TokenType.REFRESH;
            JwtToken jwtToken = JwtToken.from("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.signature");
            IssuedAt issuedAt = IssuedAt.now();
            ExpiresAt expiresAt = issuedAt.calculateExpiresAt(Duration.ofDays(7));

            // when
            Token token = Token.reconstruct(tokenId, userId, type, jwtToken, issuedAt, expiresAt);

            // then
            assertThat(token.getId()).isEqualTo(tokenId);
            assertThat(token.getUserId()).isEqualTo(userId);
            assertThat(token.getType()).isEqualTo(type);
            assertThat(token.getJwtToken()).isEqualTo(jwtToken);
            assertThat(token.getIssuedAt()).isEqualTo(issuedAt);
            assertThat(token.getExpiresAt()).isEqualTo(expiresAt);
        }

        @Test
        @DisplayName("발급 시각이 만료 시각보다 이후면 예외가 발생한다")
        void reconstruct_WithInvalidTimestamps_ShouldThrowException() {
            // given
            TokenId tokenId = TokenId.newId();
            UserId userId = UserId.newId();
            JwtToken jwtToken = JwtToken.from("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.signature");
            IssuedAt issuedAt = IssuedAt.from(Instant.now());
            ExpiresAt expiresAt = ExpiresAt.from(Instant.now().minusSeconds(3600)); // 발급 시각보다 이전

            // when & then
            assertThatThrownBy(() -> Token.reconstruct(
                    tokenId, userId, TokenType.ACCESS, jwtToken, issuedAt, expiresAt
            ))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("IssuedAt must be before ExpiresAt");
        }
    }

    @Nested
    @DisplayName("토큰 유효성 검증")
    class TokenValidationTest {

        @Test
        @DisplayName("유효 기간 내 토큰은 유효하다")
        void isValid_WithinValidity_ShouldReturnTrue() {
            // given
            UserId userId = UserId.newId();
            JwtToken jwtToken = JwtToken.from("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.signature");
            Duration validity = Duration.ofHours(1);
            Token token = Token.create(userId, TokenType.ACCESS, jwtToken, validity);

            // when & then
            assertThat(token.isValid()).isTrue();
            assertThat(token.isExpired()).isFalse();
        }

        @Test
        @DisplayName("만료된 토큰은 유효하지 않다")
        void isValid_ExpiredToken_ShouldReturnFalse() {
            // given
            TokenId tokenId = TokenId.newId();
            UserId userId = UserId.newId();
            JwtToken jwtToken = JwtToken.from("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.signature");
            IssuedAt issuedAt = IssuedAt.from(Instant.now().minusSeconds(7200)); // 2시간 전
            ExpiresAt expiresAt = ExpiresAt.from(Instant.now().minusSeconds(3600)); // 1시간 전 (만료됨)

            Token token = Token.reconstruct(
                    tokenId, userId, TokenType.ACCESS, jwtToken, issuedAt, expiresAt
            );

            // when & then
            assertThat(token.isValid()).isFalse();
            assertThat(token.isExpired()).isTrue();
        }
    }

    @Nested
    @DisplayName("토큰 타입 검증")
    class TokenTypeTest {

        @Test
        @DisplayName("액세스 토큰 타입을 올바르게 확인한다")
        void isAccessToken_ShouldReturnCorrectly() {
            // given
            UserId userId = UserId.newId();
            JwtToken jwtToken = JwtToken.from("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.signature");

            Token accessToken = Token.create(userId, TokenType.ACCESS, jwtToken, Duration.ofMinutes(15));
            Token refreshToken = Token.create(userId, TokenType.REFRESH, jwtToken, Duration.ofDays(7));

            // when & then
            assertThat(accessToken.isAccessToken()).isTrue();
            assertThat(accessToken.isRefreshToken()).isFalse();
            assertThat(refreshToken.isAccessToken()).isFalse();
            assertThat(refreshToken.isRefreshToken()).isTrue();
        }
    }

    @Nested
    @DisplayName("사용자 소유 확인")
    class BelongsToTest {

        @Test
        @DisplayName("동일한 사용자의 토큰이면 true를 반환한다")
        void belongsTo_SameUser_ShouldReturnTrue() {
            // given
            UserId userId = UserId.newId();
            JwtToken jwtToken = JwtToken.from("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.signature");
            Token token = Token.create(userId, TokenType.ACCESS, jwtToken, Duration.ofMinutes(15));

            // when & then
            assertThat(token.belongsTo(userId)).isTrue();
        }

        @Test
        @DisplayName("다른 사용자의 토큰이면 false를 반환한다")
        void belongsTo_DifferentUser_ShouldReturnFalse() {
            // given
            UserId userId1 = UserId.newId();
            UserId userId2 = UserId.newId();
            JwtToken jwtToken = JwtToken.from("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.signature");
            Token token = Token.create(userId1, TokenType.ACCESS, jwtToken, Duration.ofMinutes(15));

            // when & then
            assertThat(token.belongsTo(userId2)).isFalse();
        }
    }

    @Nested
    @DisplayName("토큰 시간 계산")
    class TokenTimeCalculationTest {

        @Test
        @DisplayName("토큰의 남은 유효 시간을 계산한다")
        void remainingValidity_ShouldCalculateCorrectly() {
            // given
            UserId userId = UserId.newId();
            JwtToken jwtToken = JwtToken.from("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.signature");
            Duration validity = Duration.ofMinutes(15);
            Token token = Token.create(userId, TokenType.ACCESS, jwtToken, validity);

            // when
            Duration remaining = token.remainingValidity();

            // then
            assertThat(remaining).isLessThanOrEqualTo(validity);
            assertThat(remaining).isGreaterThan(validity.minusSeconds(5)); // 실행 시간 고려한 오차
        }

        @Test
        @DisplayName("토큰의 age를 계산한다")
        void age_ShouldCalculateCorrectly() throws InterruptedException {
            // given
            UserId userId = UserId.newId();
            JwtToken jwtToken = JwtToken.from("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.signature");
            Token token = Token.create(userId, TokenType.ACCESS, jwtToken, Duration.ofMinutes(15));

            // 약간의 시간 경과
            Thread.sleep(100);

            // when
            Duration age = token.age();

            // then
            assertThat(age).isGreaterThan(Duration.ZERO);
            assertThat(age).isLessThan(Duration.ofSeconds(1)); // 테스트 실행 시간은 1초 미만
        }
    }

    @Nested
    @DisplayName("동등성 비교")
    class EqualsAndHashCodeTest {

        @Test
        @DisplayName("동일한 TokenId를 가진 토큰은 동등하다")
        void equals_SameTokenId_ShouldBeEqual() {
            // given
            TokenId tokenId = TokenId.newId();
            UserId userId = UserId.newId();
            JwtToken jwtToken = JwtToken.from("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.signature");
            IssuedAt issuedAt = IssuedAt.now();
            ExpiresAt expiresAt = issuedAt.calculateExpiresAt(Duration.ofMinutes(15));

            Token token1 = Token.reconstruct(tokenId, userId, TokenType.ACCESS, jwtToken, issuedAt, expiresAt);
            Token token2 = Token.reconstruct(tokenId, userId, TokenType.ACCESS, jwtToken, issuedAt, expiresAt);

            // when & then
            assertThat(token1).isEqualTo(token2);
            assertThat(token1.hashCode()).isEqualTo(token2.hashCode());
        }

        @Test
        @DisplayName("다른 TokenId를 가진 토큰은 동등하지 않다")
        void equals_DifferentTokenId_ShouldNotBeEqual() {
            // given
            UserId userId = UserId.newId();
            JwtToken jwtToken = JwtToken.from("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.signature");

            Token token1 = Token.create(userId, TokenType.ACCESS, jwtToken, Duration.ofMinutes(15));
            Token token2 = Token.create(userId, TokenType.ACCESS, jwtToken, Duration.ofMinutes(15));

            // when & then
            assertThat(token1).isNotEqualTo(token2);
        }
    }
}
