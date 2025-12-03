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
 * CreateUserApiRequest 단위 테스트
 *
 * <p>검증 범위:
 * <ul>
 *   <li>Record 불변성 검증</li>
 *   <li>필드 접근 검증</li>
 *   <li>jakarta.validation 검증</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@Tag("adapter-rest")
@Tag("dto")
@DisplayName("CreateUserApiRequest 테스트")
class CreateUserApiRequestTest {

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
        @DisplayName("모든 필수 필드로 인스턴스 생성")
        void givenAllRequiredFields_whenCreate_thenSuccess() {
            // given
            Long tenantId = 1L;
            Long organizationId = 10L;
            String identifier = "user@example.com";
            String password = "password123";

            // when
            CreateUserApiRequest request = new CreateUserApiRequest(
                    tenantId, organizationId, identifier, password, null, null, null);

            // then
            assertThat(request.tenantId()).isEqualTo(tenantId);
            assertThat(request.organizationId()).isEqualTo(organizationId);
            assertThat(request.identifier()).isEqualTo(identifier);
            assertThat(request.password()).isEqualTo(password);
            assertThat(request.userType()).isNull();
            assertThat(request.name()).isNull();
            assertThat(request.phoneNumber()).isNull();
        }

        @Test
        @DisplayName("모든 필드로 인스턴스 생성")
        void givenAllFields_whenCreate_thenSuccess() {
            // given
            Long tenantId = 1L;
            Long organizationId = 10L;
            String identifier = "user@example.com";
            String password = "password123";
            String userType = "ADMIN";
            String name = "홍길동";
            String phoneNumber = "010-1234-5678";

            // when
            CreateUserApiRequest request = new CreateUserApiRequest(
                    tenantId, organizationId, identifier, password, userType, name, phoneNumber);

            // then
            assertThat(request.tenantId()).isEqualTo(tenantId);
            assertThat(request.organizationId()).isEqualTo(organizationId);
            assertThat(request.identifier()).isEqualTo(identifier);
            assertThat(request.password()).isEqualTo(password);
            assertThat(request.userType()).isEqualTo(userType);
            assertThat(request.name()).isEqualTo(name);
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
            CreateUserApiRequest request = new CreateUserApiRequest(
                    1L, 10L, "user@example.com", "password123", null, null, null);

            // when
            Set<ConstraintViolation<CreateUserApiRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("tenantId null - 검증 실패")
        void givenNullTenantId_whenValidate_thenViolation() {
            // given
            CreateUserApiRequest request = new CreateUserApiRequest(
                    null, 10L, "user@example.com", "password123", null, null, null);

            // when
            Set<ConstraintViolation<CreateUserApiRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .contains("테넌트 ID는 필수입니다");
        }

        @Test
        @DisplayName("organizationId null - 검증 실패")
        void givenNullOrganizationId_whenValidate_thenViolation() {
            // given
            CreateUserApiRequest request = new CreateUserApiRequest(
                    1L, null, "user@example.com", "password123", null, null, null);

            // when
            Set<ConstraintViolation<CreateUserApiRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .contains("조직 ID는 필수입니다");
        }

        @Test
        @DisplayName("identifier blank - 검증 실패")
        void givenBlankIdentifier_whenValidate_thenViolation() {
            // given
            CreateUserApiRequest request = new CreateUserApiRequest(
                    1L, 10L, "", "password123", null, null, null);

            // when
            Set<ConstraintViolation<CreateUserApiRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .contains("식별자는 필수입니다");
        }

        @Test
        @DisplayName("password blank - 검증 실패")
        void givenBlankPassword_whenValidate_thenViolation() {
            // given
            CreateUserApiRequest request = new CreateUserApiRequest(
                    1L, 10L, "user@example.com", "", null, null, null);

            // when
            Set<ConstraintViolation<CreateUserApiRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage())
                    .contains("비밀번호는 필수입니다");
        }
    }
}
