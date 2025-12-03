package com.ryuqq.authhub.adapter.in.rest.user.dto.command;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

/**
 * UpdateUserApiRequest 단위 테스트
 *
 * <p>검증 범위:
 * <ul>
 *   <li>Record 불변성 검증</li>
 *   <li>필드 접근 검증</li>
 *   <li>선택 필드 null 허용 검증</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@Tag("adapter-rest")
@Tag("dto")
@DisplayName("UpdateUserApiRequest 테스트")
class UpdateUserApiRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Nested
    @DisplayName("Record 기본 동작 테스트")
    class RecordBasicTest {

        @Test
        @DisplayName("모든 필드로 인스턴스 생성")
        void givenAllFields_whenCreate_thenSuccess() {
            // given
            String name = "홍길동";
            String phoneNumber = "010-1234-5678";

            // when
            UpdateUserApiRequest request = new UpdateUserApiRequest(name, phoneNumber);

            // then
            assertThat(request.name()).isEqualTo(name);
            assertThat(request.phoneNumber()).isEqualTo(phoneNumber);
        }

        @Test
        @DisplayName("name만으로 인스턴스 생성")
        void givenOnlyName_whenCreate_thenSuccess() {
            // given
            String name = "홍길동";

            // when
            UpdateUserApiRequest request = new UpdateUserApiRequest(name, null);

            // then
            assertThat(request.name()).isEqualTo(name);
            assertThat(request.phoneNumber()).isNull();
        }

        @Test
        @DisplayName("phoneNumber만으로 인스턴스 생성")
        void givenOnlyPhoneNumber_whenCreate_thenSuccess() {
            // given
            String phoneNumber = "010-1234-5678";

            // when
            UpdateUserApiRequest request = new UpdateUserApiRequest(null, phoneNumber);

            // then
            assertThat(request.name()).isNull();
            assertThat(request.phoneNumber()).isEqualTo(phoneNumber);
        }
    }

    @Nested
    @DisplayName("Validation 테스트")
    class ValidationTest {

        @Test
        @DisplayName("유효한 요청 - 검증 통과")
        void givenValidRequest_whenValidate_thenNoViolations() {
            // given
            UpdateUserApiRequest request = new UpdateUserApiRequest("홍길동", "010-1234-5678");

            // when
            Set<ConstraintViolation<UpdateUserApiRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("모든 필드 null - 검증 통과 (선택 필드)")
        void givenAllNull_whenValidate_thenNoViolations() {
            // given
            UpdateUserApiRequest request = new UpdateUserApiRequest(null, null);

            // when
            Set<ConstraintViolation<UpdateUserApiRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isEmpty();
        }
    }
}
