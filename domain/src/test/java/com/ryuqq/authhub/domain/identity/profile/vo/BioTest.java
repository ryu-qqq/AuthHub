package com.ryuqq.authhub.domain.identity.profile.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * {@link Bio} Value Object에 대한 Unit Test.
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@DisplayName("Bio Value Object 테스트")
class BioTest {

    @Nested
    @DisplayName("생성 성공 테스트")
    class CreateSuccessTest {

        @Test
        @DisplayName("유효한 자기소개로 생성 성공")
        void createWithValidBio() {
            // given
            String validBio = "안녕하세요, AuthHub 개발자입니다.";

            // when
            Bio bio = new Bio(validBio);

            // then
            assertThat(bio.value()).isEqualTo(validBio);
            assertThat(bio.getValue()).isEqualTo(validBio);
            assertThat(bio.hasValue()).isTrue();
            assertThat(bio.isEmpty()).isFalse();
            assertThat(bio.getLength()).isEqualTo(validBio.length());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("null 또는 빈 문자열로 생성 성공 (자기소개 없음)")
        void createWithNullOrEmpty(String emptyBio) {
            // when
            Bio bio = new Bio(emptyBio);

            // then
            assertThat(bio.isEmpty()).isTrue();
            assertThat(bio.hasValue()).isFalse();
            assertThat(bio.getValue()).isEmpty();
            assertThat(bio.getLength()).isZero();
        }

        @Test
        @DisplayName("empty() 팩토리 메서드로 빈 Bio 생성")
        void createEmptyBio() {
            // when
            Bio bio = Bio.empty();

            // then
            assertThat(bio.isEmpty()).isTrue();
            assertThat(bio.hasValue()).isFalse();
        }

        @Test
        @DisplayName("최대 길이(500자) 자기소개로 생성 성공")
        void createWithMaxLengthBio() {
            // given
            String maxLengthBio = "a".repeat(500); // 500자

            // when
            Bio bio = new Bio(maxLengthBio);

            // then
            assertThat(bio.getLength()).isEqualTo(500);
            assertThat(bio.hasValue()).isTrue();
        }
    }

    @Nested
    @DisplayName("생성 실패 테스트")
    class CreateFailureTest {

        @Test
        @DisplayName("최대 길이(500자) 초과 자기소개로 생성 시 예외 발생")
        void createWithTooLongBio() {
            // given
            String tooLongBio = "a".repeat(501); // 501자

            // when & then
            assertThatThrownBy(() -> new Bio(tooLongBio))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Bio length must not exceed 500 characters");
        }
    }

    @Nested
    @DisplayName("isValid() 정적 메서드 테스트")
    class IsValidTest {

        @Test
        @DisplayName("유효한 자기소개 검증 - true 반환")
        void isValidWithValidBio() {
            // given
            String validBio = "안녕하세요, AuthHub 개발자입니다.";

            // when
            boolean result = Bio.isValid(validBio);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("null 자기소개 검증 - true 반환 (빈 자기소개로 처리)")
        void isValidWithNullBio() {
            // when
            boolean result = Bio.isValid(null);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("빈 문자열 자기소개 검증 - true 반환 (빈 자기소개로 처리)")
        void isValidWithEmptyBio() {
            // when
            boolean result = Bio.isValid("");

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("최대 길이 자기소개 검증 - true 반환")
        void isValidWithMaxLengthBio() {
            // given
            String maxLengthBio = "a".repeat(500);

            // when
            boolean result = Bio.isValid(maxLengthBio);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("최대 길이 초과 자기소개 검증 - false 반환")
        void isValidWithTooLongBio() {
            // given
            String tooLongBio = "a".repeat(501);

            // when
            boolean result = Bio.isValid(tooLongBio);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("동등성 비교 테스트")
    class EqualsTest {

        @Test
        @DisplayName("같은 자기소개를 가진 두 객체는 동등하다")
        void equalsWithSameBio() {
            // given
            String bioValue = "안녕하세요";
            Bio bio1 = new Bio(bioValue);
            Bio bio2 = new Bio(bioValue);

            // when & then
            assertThat(bio1).isEqualTo(bio2);
            assertThat(bio1.hashCode()).isEqualTo(bio2.hashCode());
        }

        @Test
        @DisplayName("다른 자기소개를 가진 두 객체는 동등하지 않다")
        void notEqualsWithDifferentBio() {
            // given
            Bio bio1 = new Bio("안녕하세요");
            Bio bio2 = new Bio("반갑습니다");

            // when & then
            assertThat(bio1).isNotEqualTo(bio2);
        }

        @Test
        @DisplayName("null과 빈 문자열 Bio는 동등하지 않다")
        void nullAndEmptyBioNotEquals() {
            // given
            Bio nullBio = new Bio(null);
            Bio emptyBio = new Bio("");

            // when & then
            // Record의 equals는 null과 ""를 구분하므로 동등하지 않음
            assertThat(nullBio).isNotEqualTo(emptyBio);
        }
    }
}
