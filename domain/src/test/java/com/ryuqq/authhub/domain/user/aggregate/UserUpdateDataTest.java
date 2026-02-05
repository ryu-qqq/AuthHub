package com.ryuqq.authhub.domain.user.aggregate;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.authhub.domain.user.vo.PhoneNumber;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * UserUpdateData Value Object 단위 테스트
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("UserUpdateData 테스트")
class UserUpdateDataTest {

    @Nested
    @DisplayName("UserUpdateData 생성 테스트")
    class CreateTests {

        @Test
        @DisplayName("of() 팩토리 메서드로 생성한다")
        void shouldCreateViaFactoryMethod() {
            // given
            PhoneNumber phoneNumber = PhoneNumber.of("010-1234-5678");

            // when
            UserUpdateData updateData = UserUpdateData.of(phoneNumber);

            // then
            assertThat(updateData.phoneNumber()).isEqualTo(phoneNumber);
        }

        @Test
        @DisplayName("null phoneNumber로 생성할 수 있다")
        void shouldCreateWithNullPhoneNumber() {
            // when
            UserUpdateData updateData = UserUpdateData.of(null);

            // then
            assertThat(updateData.phoneNumber()).isNull();
        }
    }

    @Nested
    @DisplayName("UserUpdateData Query 메서드 테스트")
    class QueryMethodTests {

        @Test
        @DisplayName("hasPhoneNumber()은 phoneNumber가 null이 아니면 true를 반환한다")
        void hasPhoneNumberShouldReturnTrueWhenPhoneNumberIsNotNull() {
            // given
            UserUpdateData updateData = UserUpdateData.of(PhoneNumber.of("010-1234-5678"));

            // then
            assertThat(updateData.hasPhoneNumber()).isTrue();
        }

        @Test
        @DisplayName("hasPhoneNumber()은 phoneNumber가 null이면 false를 반환한다")
        void hasPhoneNumberShouldReturnFalseWhenPhoneNumberIsNull() {
            // given
            UserUpdateData updateData = UserUpdateData.of(null);

            // then
            assertThat(updateData.hasPhoneNumber()).isFalse();
        }

        @Test
        @DisplayName("hasAnyUpdate()은 phoneNumber가 있으면 true를 반환한다")
        void hasAnyUpdateShouldReturnTrueWhenPhoneNumberExists() {
            // given
            UserUpdateData updateData = UserUpdateData.of(PhoneNumber.of("010-1234-5678"));

            // then
            assertThat(updateData.hasAnyUpdate()).isTrue();
        }

        @Test
        @DisplayName("hasAnyUpdate()은 phoneNumber가 null이면 false를 반환한다")
        void hasAnyUpdateShouldReturnFalseWhenPhoneNumberIsNull() {
            // given
            UserUpdateData updateData = UserUpdateData.of(null);

            // then
            assertThat(updateData.hasAnyUpdate()).isFalse();
        }
    }

    @Nested
    @DisplayName("UserUpdateData equals/hashCode 테스트")
    class EqualsHashCodeTests {

        @Test
        @DisplayName("같은 phoneNumber를 가진 UserUpdateData는 동등하다")
        void shouldBeEqualWhenSamePhoneNumber() {
            // given
            PhoneNumber phoneNumber = PhoneNumber.of("010-1234-5678");
            UserUpdateData updateData1 = UserUpdateData.of(phoneNumber);
            UserUpdateData updateData2 = UserUpdateData.of(phoneNumber);

            // then
            assertThat(updateData1).isEqualTo(updateData2);
            assertThat(updateData1.hashCode()).isEqualTo(updateData2.hashCode());
        }

        @Test
        @DisplayName("null phoneNumber를 가진 UserUpdateData는 동등하다")
        void shouldBeEqualWhenBothHaveNullPhoneNumber() {
            // given
            UserUpdateData updateData1 = UserUpdateData.of(null);
            UserUpdateData updateData2 = UserUpdateData.of(null);

            // then
            assertThat(updateData1).isEqualTo(updateData2);
        }

        @Test
        @DisplayName("다른 phoneNumber를 가진 UserUpdateData는 동등하지 않다")
        void shouldNotBeEqualWhenDifferentPhoneNumber() {
            // given
            UserUpdateData updateData1 = UserUpdateData.of(PhoneNumber.of("010-1234-5678"));
            UserUpdateData updateData2 = UserUpdateData.of(PhoneNumber.of("010-9999-8888"));

            // then
            assertThat(updateData1).isNotEqualTo(updateData2);
        }
    }
}
