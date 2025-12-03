package com.ryuqq.authhub.adapter.in.rest.organization.dto.command;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

/**
 * CreateOrganizationApiRequest 단위 테스트
 *
 * <p>검증 범위:
 * <ul>
 *   <li>Record 생성 및 접근자</li>
 *   <li>Bean Validation 규칙</li>
 *   <li>equals/hashCode</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@Tag("adapter-rest")
@Tag("dto")
@DisplayName("CreateOrganizationApiRequest 테스트")
class CreateOrganizationApiRequestTest {

    private final Validator validator;

    CreateOrganizationApiRequestTest() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            this.validator = factory.getValidator();
        }
    }

    @Nested
    @DisplayName("Record 기본 동작 테스트")
    class RecordBasicBehaviorTest {

        @Test
        @DisplayName("모든 필드가 주어지면 Record가 정상 생성된다")
        void givenAllFields_whenCreate_thenRecordCreated() {
            // given
            Long tenantId = 1L;
            String name = "Engineering Team";

            // when
            CreateOrganizationApiRequest request = new CreateOrganizationApiRequest(tenantId, name);

            // then
            assertThat(request.tenantId()).isEqualTo(tenantId);
            assertThat(request.name()).isEqualTo(name);
        }

        @Test
        @DisplayName("동일한 값을 가진 두 Record는 동등하다")
        void givenSameValues_whenCompare_thenEqual() {
            // given
            CreateOrganizationApiRequest request1 = new CreateOrganizationApiRequest(1L, "Team A");
            CreateOrganizationApiRequest request2 = new CreateOrganizationApiRequest(1L, "Team A");

            // then
            assertThat(request1).isEqualTo(request2);
            assertThat(request1.hashCode()).isEqualTo(request2.hashCode());
        }
    }

    @Nested
    @DisplayName("Validation 테스트")
    class ValidationTest {

        @Test
        @DisplayName("유효한 요청은 검증을 통과한다")
        void givenValidRequest_whenValidate_thenNoViolations() {
            // given
            CreateOrganizationApiRequest request = new CreateOrganizationApiRequest(1L, "Development");

            // when
            Set<ConstraintViolation<CreateOrganizationApiRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("tenantId가 null이면 검증 실패")
        void givenNullTenantId_whenValidate_thenViolation() {
            // given
            CreateOrganizationApiRequest request = new CreateOrganizationApiRequest(null, "Development");

            // when
            Set<ConstraintViolation<CreateOrganizationApiRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).contains("테넌트 ID");
        }

        @Test
        @DisplayName("name이 null이면 검증 실패")
        void givenNullName_whenValidate_thenViolation() {
            // given
            CreateOrganizationApiRequest request = new CreateOrganizationApiRequest(1L, null);

            // when
            Set<ConstraintViolation<CreateOrganizationApiRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).contains("조직 이름");
        }

        @Test
        @DisplayName("name이 빈 문자열이면 검증 실패")
        void givenBlankName_whenValidate_thenViolation() {
            // given
            CreateOrganizationApiRequest request = new CreateOrganizationApiRequest(1L, "   ");

            // when
            Set<ConstraintViolation<CreateOrganizationApiRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isNotEmpty();
        }
    }
}
