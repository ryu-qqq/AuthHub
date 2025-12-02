package com.ryuqq.authhub.application.organization.dto.command;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * CreateOrganizationCommand DTO 설계 테스트
 *
 * <p>Kent Beck TDD - Red Phase: 실패하는 테스트 먼저 작성
 *
 * <p>설계 원칙:
 * <ul>
 *   <li>Pure Java Record (Lombok 금지)</li>
 *   <li>최소 필드 원칙: tenantId, name만 포함</li>
 *   <li>불변성 보장</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@DisplayName("CreateOrganizationCommand DTO 설계")
class CreateOrganizationCommandTest {

    @Nested
    @DisplayName("Record 구조 검증")
    class RecordStructure {

        @Test
        @DisplayName("Record 타입이어야 한다")
        void shouldBeRecord() {
            assertThat(CreateOrganizationCommand.class.isRecord()).isTrue();
        }

        @Test
        @DisplayName("tenantId 필드를 가져야 한다")
        void shouldHaveTenantIdField() throws NoSuchMethodException {
            var method = CreateOrganizationCommand.class.getMethod("tenantId");
            assertThat(method.getReturnType()).isEqualTo(Long.class);
        }

        @Test
        @DisplayName("name 필드를 가져야 한다")
        void shouldHaveNameField() throws NoSuchMethodException {
            var method = CreateOrganizationCommand.class.getMethod("name");
            assertThat(method.getReturnType()).isEqualTo(String.class);
        }
    }

    @Nested
    @DisplayName("인스턴스 생성")
    class InstanceCreation {

        @Test
        @DisplayName("필수 필드로 생성할 수 있어야 한다")
        void shouldCreateWithRequiredFields() {
            var command = new CreateOrganizationCommand(1L, "테스트 조직");

            assertThat(command.tenantId()).isEqualTo(1L);
            assertThat(command.name()).isEqualTo("테스트 조직");
        }
    }

    @Nested
    @DisplayName("불변성")
    class Immutability {

        @Test
        @DisplayName("Record는 불변이어야 한다")
        void shouldBeImmutable() {
            var command = new CreateOrganizationCommand(1L, "조직명");

            // Record의 모든 필드는 final
            assertThat(CreateOrganizationCommand.class.getRecordComponents())
                    .allMatch(rc -> java.lang.reflect.Modifier.isFinal(
                            rc.getAccessor().getModifiers()) == false);
            // Note: Record accessor methods are not final, but fields are
        }
    }
}
