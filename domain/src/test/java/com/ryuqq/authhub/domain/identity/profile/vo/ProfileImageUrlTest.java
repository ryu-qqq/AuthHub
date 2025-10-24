package com.ryuqq.authhub.domain.identity.profile.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * {@link ProfileImageUrl} Value Object에 대한 Unit Test.
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@DisplayName("ProfileImageUrl Value Object 테스트")
class ProfileImageUrlTest {

    @Nested
    @DisplayName("생성 성공 테스트")
    class CreateSuccessTest {

        @Test
        @DisplayName("유효한 HTTPS URL로 생성 성공")
        void createWithValidHttpsUrl() {
            // given
            String validUrl = "https://example.com/profile.png";

            // when
            ProfileImageUrl imageUrl = new ProfileImageUrl(validUrl);

            // then
            assertThat(imageUrl.value()).isEqualTo(validUrl);
            assertThat(imageUrl.getValue()).isEqualTo(validUrl);
            assertThat(imageUrl.hasValue()).isTrue();
            assertThat(imageUrl.isDefault()).isFalse();
        }

        @Test
        @DisplayName("유효한 HTTP URL로 생성 성공")
        void createWithValidHttpUrl() {
            // given
            String validUrl = "http://example.com/profile.png";

            // when
            ProfileImageUrl imageUrl = new ProfileImageUrl(validUrl);

            // then
            assertThat(imageUrl.value()).isEqualTo(validUrl);
            assertThat(imageUrl.hasValue()).isTrue();
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "  ", "\t"})
        @DisplayName("null 또는 빈 문자열로 생성 시 기본 이미지 사용")
        void createWithNullOrEmpty(String emptyUrl) {
            // when
            ProfileImageUrl imageUrl = new ProfileImageUrl(emptyUrl);

            // then
            assertThat(imageUrl.hasValue()).isFalse();
            assertThat(imageUrl.getValue()).isNotEmpty(); // 기본 이미지 URL 반환
        }

        @Test
        @DisplayName("defaultImage() 팩토리 메서드로 기본 이미지 생성")
        void createDefaultImage() {
            // when
            ProfileImageUrl imageUrl = ProfileImageUrl.defaultImage();

            // then
            assertThat(imageUrl.isDefault()).isTrue();
            assertThat(imageUrl.hasValue()).isTrue();
        }
    }

    @Nested
    @DisplayName("생성 실패 테스트 - URL 형식")
    class CreateFailureUrlFormatTest {

        @ParameterizedTest
        @ValueSource(strings = {"not-a-url", "invalid url", "example.com"})
        @DisplayName("유효하지 않은 URL 형식으로 생성 시 예외 발생")
        void createWithInvalidUrlFormat(String invalidUrl) {
            // when & then
            assertThatThrownBy(() -> new ProfileImageUrl(invalidUrl))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Invalid URL format");
        }
    }

    @Nested
    @DisplayName("생성 실패 테스트 - 프로토콜 제약")
    class CreateFailureProtocolTest {

        @ParameterizedTest
        @ValueSource(strings = {"ftp://example.com/profile.png", "file:///home/user/profile.png"})
        @DisplayName("HTTP/HTTPS가 아닌 프로토콜로 생성 시 예외 발생")
        void createWithInvalidProtocol(String invalidProtocolUrl) {
            // when & then
            assertThatThrownBy(() -> new ProfileImageUrl(invalidProtocolUrl))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("must use HTTP or HTTPS protocol");
        }
    }

    @Nested
    @DisplayName("isValid() 정적 메서드 테스트")
    class IsValidTest {

        @Test
        @DisplayName("유효한 HTTPS URL 검증 - true 반환")
        void isValidWithValidHttpsUrl() {
            // given
            String validUrl = "https://example.com/profile.png";

            // when
            boolean result = ProfileImageUrl.isValid(validUrl);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("유효한 HTTP URL 검증 - true 반환")
        void isValidWithValidHttpUrl() {
            // given
            String validUrl = "http://example.com/profile.png";

            // when
            boolean result = ProfileImageUrl.isValid(validUrl);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("null URL 검증 - true 반환 (기본 이미지로 처리)")
        void isValidWithNullUrl() {
            // when
            boolean result = ProfileImageUrl.isValid(null);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("빈 문자열 URL 검증 - true 반환 (기본 이미지로 처리)")
        void isValidWithEmptyUrl() {
            // when
            boolean result = ProfileImageUrl.isValid("");

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("유효하지 않은 URL 형식 검증 - false 반환")
        void isValidWithInvalidUrlFormat() {
            // when
            boolean result = ProfileImageUrl.isValid("not-a-url");

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("HTTP/HTTPS가 아닌 프로토콜 검증 - false 반환")
        void isValidWithInvalidProtocol() {
            // when
            boolean result = ProfileImageUrl.isValid("ftp://example.com/profile.png");

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("동등성 비교 테스트")
    class EqualsTest {

        @Test
        @DisplayName("같은 URL을 가진 두 객체는 동등하다")
        void equalsWithSameUrl() {
            // given
            String url = "https://example.com/profile.png";
            ProfileImageUrl imageUrl1 = new ProfileImageUrl(url);
            ProfileImageUrl imageUrl2 = new ProfileImageUrl(url);

            // when & then
            assertThat(imageUrl1).isEqualTo(imageUrl2);
            assertThat(imageUrl1.hashCode()).isEqualTo(imageUrl2.hashCode());
        }

        @Test
        @DisplayName("다른 URL을 가진 두 객체는 동등하지 않다")
        void notEqualsWithDifferentUrl() {
            // given
            ProfileImageUrl imageUrl1 = new ProfileImageUrl("https://example.com/profile1.png");
            ProfileImageUrl imageUrl2 = new ProfileImageUrl("https://example.com/profile2.png");

            // when & then
            assertThat(imageUrl1).isNotEqualTo(imageUrl2);
        }
    }
}
