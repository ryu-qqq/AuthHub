package com.ryuqq.authhub.application.tenant.dto.command;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * CreateTenantCommand DTO 설계 테스트
 *
 * <p>Kent Beck TDD - Red Phase: 실패하는 테스트 먼저 작성
 *
 * <p>설계 원칙:
 * <ul>
 *   <li>Pure Java Record (Lombok 금지)</li>
 *   <li>최소 필드 원칙: name만 포함</li>
 *   <li>불변성 보장</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@DisplayName("CreateTenantCommand DTO 설계")
class CreateTenantCommandTest {

    @Nested
    @DisplayName("Record 구조 검증")
    class RecordStructure {

        @Test
        @DisplayName("Record 타입이어야 한다")
        void shouldBeRecord() {
            assertThat(CreateTenantCommand.class.isRecord()).isTrue();
        }

        @Test
        @DisplayName("name 필드를 가져야 한다")
        void shouldHaveNameField() throws NoSuchMethodException {
            var method = CreateTenantCommand.class.getMethod("name");
            assertThat(method.getReturnType()).isEqualTo(String.class);
        }
    }

    @Nested
    @DisplayName("인스턴스 생성")
    class InstanceCreation {

        @Test
        @DisplayName("필수 필드로 생성할 수 있어야 한다")
        void shouldCreateWithRequiredFields() {
            var command = new CreateTenantCommand("테스트 테넌트");

            assertThat(command.name()).isEqualTo("테스트 테넌트");
        }
    }

    @Nested
    @DisplayName("최소 필드 원칙")
    class MinimalFields {

        @Test
        @DisplayName("name 필드만 가져야 한다 (최소 필드 원칙)")
        void shouldHaveOnlyNameField() {
            var components = CreateTenantCommand.class.getRecordComponents();

            assertThat(components).hasSize(1);
            assertThat(components[0].getName()).isEqualTo("name");
        }
    }
}
