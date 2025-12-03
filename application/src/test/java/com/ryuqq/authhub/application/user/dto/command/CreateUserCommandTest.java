package com.ryuqq.authhub.application.user.dto.command;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Modifier;
import java.lang.reflect.RecordComponent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * CreateUserCommand DTO 설계 검증 테스트
 *
 * <p>Command DTO Zero-Tolerance 규칙:
 *
 * <ul>
 *   <li>Record 타입이어야 함
 *   <li>필수 필드: tenantId, identifier, rawPassword
 *   <li>선택 필드: organizationId, userType, name, phoneNumber
 *   <li>순수 Java 타입만 사용 (jakarta.validation 금지)
 *   <li>불변성 보장
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Tag("unit")
@Tag("dto")
@DisplayName("CreateUserCommand DTO 설계 테스트")
class CreateUserCommandTest {

    @Nested
    @DisplayName("Record 구조 검증")
    class RecordStructureTest {

        @Test
        @DisplayName("[필수] CreateUserCommand는 Record 타입이어야 한다")
        void shouldBeRecord() {
            // When & Then
            assertThat(CreateUserCommand.class.isRecord())
                    .as("CreateUserCommand는 Record 타입으로 선언되어야 합니다")
                    .isTrue();
        }

        @Test
        @DisplayName("[필수] CreateUserCommand는 public이어야 한다")
        void shouldBePublic() {
            // When & Then
            assertThat(Modifier.isPublic(CreateUserCommand.class.getModifiers()))
                    .as("CreateUserCommand는 public으로 선언되어야 합니다")
                    .isTrue();
        }

        @Test
        @DisplayName("[필수] CreateUserCommand는 final이어야 한다")
        void shouldBeFinal() {
            // When & Then
            assertThat(Modifier.isFinal(CreateUserCommand.class.getModifiers()))
                    .as("Record는 기본적으로 final입니다")
                    .isTrue();
        }
    }

    @Nested
    @DisplayName("필수 필드 검증")
    class RequiredFieldsTest {

        @Test
        @DisplayName("[필수] tenantId 필드가 존재해야 한다")
        void shouldHaveTenantIdField() {
            // When
            RecordComponent[] components = CreateUserCommand.class.getRecordComponents();

            // Then
            assertThat(components).extracting(RecordComponent::getName).contains("tenantId");

            RecordComponent tenantIdComponent = findComponent(components, "tenantId");
            assertThat(tenantIdComponent.getType())
                    .as("tenantId는 Long 타입이어야 합니다")
                    .isEqualTo(Long.class);
        }

        @Test
        @DisplayName("[필수] identifier 필드가 존재해야 한다")
        void shouldHaveIdentifierField() {
            // When
            RecordComponent[] components = CreateUserCommand.class.getRecordComponents();

            // Then
            assertThat(components).extracting(RecordComponent::getName).contains("identifier");

            RecordComponent component = findComponent(components, "identifier");
            assertThat(component.getType())
                    .as("identifier는 String 타입이어야 합니다")
                    .isEqualTo(String.class);
        }

        @Test
        @DisplayName("[필수] rawPassword 필드가 존재해야 한다")
        void shouldHaveRawPasswordField() {
            // When
            RecordComponent[] components = CreateUserCommand.class.getRecordComponents();

            // Then
            assertThat(components).extracting(RecordComponent::getName).contains("rawPassword");

            RecordComponent component = findComponent(components, "rawPassword");
            assertThat(component.getType())
                    .as("rawPassword는 String 타입이어야 합니다")
                    .isEqualTo(String.class);
        }
    }

    @Nested
    @DisplayName("선택 필드 검증")
    class OptionalFieldsTest {

        @Test
        @DisplayName("[선택] organizationId 필드가 존재해야 한다")
        void shouldHaveOrganizationIdField() {
            // When
            RecordComponent[] components = CreateUserCommand.class.getRecordComponents();

            // Then
            assertThat(components).extracting(RecordComponent::getName).contains("organizationId");

            RecordComponent component = findComponent(components, "organizationId");
            assertThat(component.getType())
                    .as("organizationId는 Long 타입이어야 합니다")
                    .isEqualTo(Long.class);
        }

        @Test
        @DisplayName("[선택] userType 필드가 존재해야 한다")
        void shouldHaveUserTypeField() {
            // When
            RecordComponent[] components = CreateUserCommand.class.getRecordComponents();

            // Then
            assertThat(components).extracting(RecordComponent::getName).contains("userType");

            RecordComponent component = findComponent(components, "userType");
            assertThat(component.getType())
                    .as("userType은 String 타입이어야 합니다")
                    .isEqualTo(String.class);
        }

        @Test
        @DisplayName("[선택] name 필드가 존재해야 한다")
        void shouldHaveNameField() {
            // When
            RecordComponent[] components = CreateUserCommand.class.getRecordComponents();

            // Then
            assertThat(components).extracting(RecordComponent::getName).contains("name");

            RecordComponent component = findComponent(components, "name");
            assertThat(component.getType()).as("name은 String 타입이어야 합니다").isEqualTo(String.class);
        }

        @Test
        @DisplayName("[선택] phoneNumber 필드가 존재해야 한다")
        void shouldHavePhoneNumberField() {
            // When
            RecordComponent[] components = CreateUserCommand.class.getRecordComponents();

            // Then
            assertThat(components).extracting(RecordComponent::getName).contains("phoneNumber");

            RecordComponent component = findComponent(components, "phoneNumber");
            assertThat(component.getType())
                    .as("phoneNumber는 String 타입이어야 합니다")
                    .isEqualTo(String.class);
        }
    }

    @Nested
    @DisplayName("불변성 검증")
    class ImmutabilityTest {

        @Test
        @DisplayName("[필수] Record 인스턴스 생성 후 필드 값이 유지되어야 한다")
        void shouldMaintainFieldValues() {
            // Given
            Long tenantId = 1L;
            Long organizationId = 10L;
            String identifier = "test@example.com";
            String rawPassword = "Password123!";
            String userType = "PUBLIC";
            String name = "홍길동";
            String phoneNumber = "+82-10-1234-5678";

            // When
            CreateUserCommand command =
                    new CreateUserCommand(
                            tenantId,
                            organizationId,
                            identifier,
                            rawPassword,
                            userType,
                            name,
                            phoneNumber);

            // Then
            assertThat(command.tenantId()).isEqualTo(tenantId);
            assertThat(command.organizationId()).isEqualTo(organizationId);
            assertThat(command.identifier()).isEqualTo(identifier);
            assertThat(command.rawPassword()).isEqualTo(rawPassword);
            assertThat(command.userType()).isEqualTo(userType);
            assertThat(command.name()).isEqualTo(name);
            assertThat(command.phoneNumber()).isEqualTo(phoneNumber);
        }

        @Test
        @DisplayName("[필수] null 필드도 허용되어야 한다 (선택 필드)")
        void shouldAllowNullOptionalFields() {
            // Given
            Long tenantId = 1L;
            String identifier = "test@example.com";
            String rawPassword = "Password123!";

            // When
            CreateUserCommand command =
                    new CreateUserCommand(
                            tenantId,
                            null, // organizationId - optional
                            identifier,
                            rawPassword,
                            null, // userType - optional
                            null, // name - optional
                            null // phoneNumber - optional
                            );

            // Then
            assertThat(command.tenantId()).isEqualTo(tenantId);
            assertThat(command.organizationId()).isNull();
            assertThat(command.identifier()).isEqualTo(identifier);
            assertThat(command.rawPassword()).isEqualTo(rawPassword);
            assertThat(command.userType()).isNull();
            assertThat(command.name()).isNull();
            assertThat(command.phoneNumber()).isNull();
        }
    }

    @Nested
    @DisplayName("프레임워크 독립성 검증")
    class FrameworkIndependenceTest {

        @Test
        @DisplayName("[금지] jakarta.validation 어노테이션이 없어야 한다")
        void shouldNotHaveValidationAnnotations() {
            // When
            boolean hasValidationAnnotation =
                    java.util.Arrays.stream(CreateUserCommand.class.getDeclaredAnnotations())
                            .anyMatch(
                                    annotation ->
                                            annotation
                                                    .annotationType()
                                                    .getPackageName()
                                                    .startsWith("jakarta.validation"));

            // Then
            assertThat(hasValidationAnnotation)
                    .as("Command DTO는 jakarta.validation 의존성이 없어야 합니다")
                    .isFalse();
        }

        @Test
        @DisplayName("[금지] 필드에 jakarta.validation 어노테이션이 없어야 한다")
        void shouldNotHaveValidationAnnotationsOnFields() {
            // When
            boolean hasValidationAnnotation =
                    java.util.Arrays.stream(CreateUserCommand.class.getRecordComponents())
                            .flatMap(
                                    component ->
                                            java.util.Arrays.stream(component.getAnnotations()))
                            .anyMatch(
                                    annotation ->
                                            annotation
                                                    .annotationType()
                                                    .getPackageName()
                                                    .startsWith("jakarta.validation"));

            // Then
            assertThat(hasValidationAnnotation)
                    .as("Command DTO 필드에 jakarta.validation 어노테이션이 없어야 합니다")
                    .isFalse();
        }
    }

    private RecordComponent findComponent(RecordComponent[] components, String name) {
        return java.util.Arrays.stream(components)
                .filter(c -> c.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Component not found: " + name));
    }
}
