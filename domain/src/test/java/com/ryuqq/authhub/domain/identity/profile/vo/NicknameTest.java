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
 * {@link Nickname} Value Object에 대한 Unit Test.
 *
 * <p>Nickname의 모든 validation 규칙과 비즈니스 로직을 검증합니다.</p>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@DisplayName("Nickname Value Object 테스트")
class NicknameTest {

    @Nested
    @DisplayName("생성 성공 테스트")
    class CreateSuccessTest {

        @Test
        @DisplayName("유효한 한글 닉네임으로 생성 성공")
        void createWithValidKoreanNickname() {
            // given
            String validKoreanNickname = "홍길동";

            // when
            Nickname nickname = new Nickname(validKoreanNickname);

            // then
            assertThat(nickname.getValue()).isEqualTo(validKoreanNickname);
            assertThat(nickname.getLength()).isEqualTo(3);
        }

        @Test
        @DisplayName("유효한 영문 닉네임으로 생성 성공")
        void createWithValidEnglishNickname() {
            // given
            String validEnglishNickname = "john_doe";

            // when
            Nickname nickname = new Nickname(validEnglishNickname);

            // then
            assertThat(nickname.getValue()).isEqualTo(validEnglishNickname);
            assertThat(nickname.getLength()).isEqualTo(8);
        }

        @Test
        @DisplayName("한글+영문+숫자 혼합 닉네임으로 생성 성공")
        void createWithMixedNickname() {
            // given
            String mixedNickname = "개발자123";

            // when
            Nickname nickname = new Nickname(mixedNickname);

            // then
            assertThat(nickname.getValue()).isEqualTo(mixedNickname);
        }

        @Test
        @DisplayName("언더스코어 포함 닉네임으로 생성 성공")
        void createWithUnderscoreNickname() {
            // given
            String nicknameWithUnderscore = "user_name_123";

            // when
            Nickname nickname = new Nickname(nicknameWithUnderscore);

            // then
            assertThat(nickname.getValue()).isEqualTo(nicknameWithUnderscore);
        }

        @Test
        @DisplayName("최소 길이(2자) 닉네임으로 생성 성공")
        void createWithMinLengthNickname() {
            // given
            String minLengthNickname = "ab";

            // when
            Nickname nickname = new Nickname(minLengthNickname);

            // then
            assertThat(nickname.getLength()).isEqualTo(2);
        }

        @Test
        @DisplayName("최대 길이(20자) 닉네임으로 생성 성공")
        void createWithMaxLengthNickname() {
            // given
            String maxLengthNickname = "12345678901234567890"; // 20자

            // when
            Nickname nickname = new Nickname(maxLengthNickname);

            // then
            assertThat(nickname.getLength()).isEqualTo(20);
        }
    }

    @Nested
    @DisplayName("생성 실패 테스트 - Null/Empty")
    class CreateFailureNullEmptyTest {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "  ", "\t", "\n"})
        @DisplayName("null 또는 빈 문자열로 생성 시 예외 발생")
        void createWithNullOrEmptyNickname(String invalidNickname) {
            // when & then
            assertThatThrownBy(() -> new Nickname(invalidNickname))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Nickname cannot be null or empty");
        }
    }

    @Nested
    @DisplayName("생성 실패 테스트 - 길이 제약")
    class CreateFailureLengthTest {

        @ParameterizedTest
        @ValueSource(strings = {"a", "1"})
        @DisplayName("최소 길이(2자) 미만 닉네임으로 생성 시 예외 발생")
        void createWithTooShortNickname(String tooShortNickname) {
            // when & then
            assertThatThrownBy(() -> new Nickname(tooShortNickname))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Nickname length must be between 2 and 20 characters");
        }

        @Test
        @DisplayName("최대 길이(20자) 초과 닉네임으로 생성 시 예외 발생")
        void createWithTooLongNickname() {
            // given
            String tooLongNickname = "123456789012345678901"; // 21자

            // when & then
            assertThatThrownBy(() -> new Nickname(tooLongNickname))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Nickname length must be between 2 and 20 characters");
        }
    }

    @Nested
    @DisplayName("생성 실패 테스트 - 패턴 제약")
    class CreateFailurePatternTest {

        @ParameterizedTest
        @ValueSource(strings = {"nick name", "홍 길동", "user name"})
        @DisplayName("공백 포함 닉네임으로 생성 시 예외 발생")
        void createWithSpaceNickname(String nicknameWithSpace) {
            // when & then
            assertThatThrownBy(() -> new Nickname(nicknameWithSpace))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Nickname can only contain Korean, English, numbers, and underscores");
        }

        @ParameterizedTest
        @ValueSource(strings = {"user@name", "user#123", "user!name", "user$name"})
        @DisplayName("특수문자(언더스코어 제외) 포함 닉네임으로 생성 시 예외 발생")
        void createWithSpecialCharactersNickname(String nicknameWithSpecialChars) {
            // when & then
            assertThatThrownBy(() -> new Nickname(nicknameWithSpecialChars))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Nickname can only contain Korean, English, numbers, and underscores");
        }

        @ParameterizedTest
        @ValueSource(strings = {"user-name", "user.name", "user+name"})
        @DisplayName("하이픈, 점, 플러스 기호 포함 닉네임으로 생성 시 예외 발생")
        void createWithHyphenOrDotNickname(String invalidNickname) {
            // when & then
            assertThatThrownBy(() -> new Nickname(invalidNickname))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Nickname can only contain Korean, English, numbers, and underscores");
        }
    }

    @Nested
    @DisplayName("isValid() 정적 메서드 테스트")
    class IsValidTest {

        @Test
        @DisplayName("유효한 닉네임 검증 - true 반환")
        void isValidWithValidNickname() {
            // given
            String validNickname = "홍길동123";

            // when
            boolean result = Nickname.isValid(validNickname);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("null 닉네임 검증 - false 반환")
        void isValidWithNullNickname() {
            // when
            boolean result = Nickname.isValid(null);

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("빈 문자열 닉네임 검증 - false 반환")
        void isValidWithEmptyNickname() {
            // when
            boolean result = Nickname.isValid("");

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("너무 짧은 닉네임 검증 - false 반환")
        void isValidWithTooShortNickname() {
            // when
            boolean result = Nickname.isValid("a");

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("너무 긴 닉네임 검증 - false 반환")
        void isValidWithTooLongNickname() {
            // when
            boolean result = Nickname.isValid("123456789012345678901"); // 21자

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("특수문자 포함 닉네임 검증 - false 반환")
        void isValidWithSpecialCharactersNickname() {
            // when
            boolean result = Nickname.isValid("user@name");

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("동등성 비교 테스트")
    class EqualsTest {

        @Test
        @DisplayName("같은 닉네임 값을 가진 두 객체는 동등하다")
        void equalsWithSameNickname() {
            // given
            Nickname nickname1 = new Nickname("홍길동");
            Nickname nickname2 = new Nickname("홍길동");

            // when & then
            assertThat(nickname1).isEqualTo(nickname2);
            assertThat(nickname1.hashCode()).isEqualTo(nickname2.hashCode());
        }

        @Test
        @DisplayName("다른 닉네임 값을 가진 두 객체는 동등하지 않다")
        void notEqualsWithDifferentNickname() {
            // given
            Nickname nickname1 = new Nickname("홍길동");
            Nickname nickname2 = new Nickname("김철수");

            // when & then
            assertThat(nickname1).isNotEqualTo(nickname2);
        }
    }

    @Nested
    @DisplayName("toString() 테스트")
    class ToStringTest {

        @Test
        @DisplayName("toString()이 Record 기본 형식을 따른다")
        void toStringReturnsRecordFormat() {
            // given
            Nickname nickname = new Nickname("홍길동");

            // when
            String result = nickname.toString();

            // then
            assertThat(result).contains("Nickname");
            assertThat(result).contains("홍길동");
        }
    }
}
