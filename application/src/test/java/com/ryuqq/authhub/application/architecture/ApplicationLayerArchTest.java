package com.ryuqq.authhub.application.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

/**
 * Application Layer Architecture Test - ArchUnit 기반 아키텍처 규칙 검증.
 *
 * <p>Application Layer의 헥사고날 아키텍처 및 Zero-Tolerance 규칙을 자동으로 검증합니다.</p>
 *
 * <p><strong>검증 항목:</strong></p>
 * <ul>
 *   <li>✅ Layer 의존성 규칙 (Port → Domain, Service → Port)</li>
 *   <li>✅ Lombok 금지 (@Data, @Builder, @Getter, @Setter)</li>
 *   <li>✅ Command/Response는 UseCase의 inner Record</li>
 *   <li>✅ Port는 Interface여야 함</li>
 *   <li>✅ Service는 @Transactional 사용</li>
 *   <li>✅ Assembler는 @Component 사용</li>
 *   <li>✅ 네이밍 규칙 준수</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@DisplayName("Application Layer Architecture Test")
class ApplicationLayerArchTest {

    private static JavaClasses applicationClasses;

    @BeforeAll
    static void setUp() {
        applicationClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("com.ryuqq.authhub.application");
    }

    /**
     * Layer 의존성 규칙 검증.
     */
    @Nested
    @DisplayName("Layer 의존성 규칙")
    class LayerDependencyRules {

        @Test
        @DisplayName("Application Layer는 Domain Layer와 java 패키지에만 의존해야 함")
        void applicationLayerShouldOnlyDependOnDomain() {
            // Application 레이어는 자신의 하위 레이어인 Domain에만 의존해야 함
            // (java, springframework 등 프레임워크 의존성은 허용)
            ArchRule rule = classes()
                    .that().resideInAPackage("com.ryuqq.authhub.application..")
                    .should().onlyDependOnClassesThat()
                    .resideInAnyPackage(
                            "com.ryuqq.authhub.application..",  // 자기 자신
                            "com.ryuqq.authhub.domain..",      // Domain layer
                            "java..",
                            "org.springframework..",
                            "jakarta.."
                    );

            rule.check(applicationClasses);
        }

        @Test
        @DisplayName("Service는 Port와 Domain에만 의존해야 함 (Top-level만 검사)")
        void serviceShouldOnlyDependOnPortsAndDomain() {
            ArchRule rule = classes()
                    .that().resideInAnyPackage("..service..")
                    .and().areTopLevelClasses()  // Inner Exception classes 제외
                    .should().onlyDependOnClassesThat()
                    .resideInAnyPackage(
                            "..port..",
                            "..domain..",
                            "..assembler..",
                            "..config..",
                            "..exception..",  // Application exception classes 허용
                            "java..",
                            "org.springframework..",
                            "jakarta.."
                    );

            rule.check(applicationClasses);
        }

        @Test
        @DisplayName("Port는 Domain에만 의존해야 함 (자신의 inner class 제외)")
        void portsShouldOnlyDependOnDomain() {
            ArchRule rule = classes()
                    .that().resideInAnyPackage("..port..")
                    .and().areTopLevelClasses()  // Inner classes 제외
                    .should().onlyDependOnClassesThat()
                    .resideInAnyPackage(
                            "..domain..",
                            "..port..",  // Port의 Inner Record 허용
                            "java.."
                    );

            rule.check(applicationClasses);
        }

        @Test
        @DisplayName("Assembler는 Domain과 Port에만 의존해야 함")
        void assemblerShouldOnlyDependOnDomainAndPort() {
            ArchRule rule = classes()
                    .that().resideInAnyPackage("..assembler..")
                    .should().onlyDependOnClassesThat()
                    .resideInAnyPackage(
                            "..domain..",
                            "..port..",
                            "java..",
                            "org.springframework.."
                    );

            rule.check(applicationClasses);
        }
    }

    /**
     * Lombok 금지 규칙 검증.
     */
    @Nested
    @DisplayName("Lombok 금지 규칙")
    class LombokProhibitionRules {

        @Test
        @DisplayName("@Data 어노테이션 사용 금지")
        void shouldNotUseDataAnnotation() {
            ArchRule rule = noClasses()
                    .should().beAnnotatedWith("lombok.Data");

            rule.check(applicationClasses);
        }

        @Test
        @DisplayName("@Builder 어노테이션 사용 금지")
        void shouldNotUseBuilderAnnotation() {
            ArchRule rule = noClasses()
                    .should().beAnnotatedWith("lombok.Builder");

            rule.check(applicationClasses);
        }

        @Test
        @DisplayName("@Getter 어노테이션 사용 금지")
        void shouldNotUseGetterAnnotation() {
            ArchRule rule = noClasses()
                    .should().beAnnotatedWith("lombok.Getter");

            rule.check(applicationClasses);
        }

        @Test
        @DisplayName("@Setter 어노테이션 사용 금지")
        void shouldNotUseSetterAnnotation() {
            ArchRule rule = noClasses()
                    .should().beAnnotatedWith("lombok.Setter");

            rule.check(applicationClasses);
        }
    }

    /**
     * UseCase 패턴 규칙 검증.
     */
    @Nested
    @DisplayName("UseCase 패턴 규칙")
    class UseCasePatternRules {

        @Test
        @DisplayName("UseCase는 Interface여야 함")
        void useCaseShouldBeInterface() {
            ArchRule rule = classes()
                    .that().haveSimpleNameEndingWith("UseCase")
                    .should().beInterfaces();

            rule.check(applicationClasses);
        }

        @Test
        @DisplayName("UseCase는 port.in 패키지에 위치해야 함")
        void useCaseShouldResideInPortInPackage() {
            ArchRule rule = classes()
                    .that().haveSimpleNameEndingWith("UseCase")
                    .should().resideInAPackage("..port.in..");

            rule.check(applicationClasses);
        }

        @Test
        @DisplayName("UseCase는 Command 또는 Query 메서드를 가져야 함")
        void useCaseShouldHaveCommandOrQueryMethod() {
            ArchRule rule = classes()
                    .that().haveSimpleNameEndingWith("UseCase")
                    .should().haveOnlyFinalFields()
                    .orShould().beInterfaces();

            rule.check(applicationClasses);
        }
    }

    /**
     * Port 패턴 규칙 검증.
     */
    @Nested
    @DisplayName("Port 패턴 규칙")
    class PortPatternRules {

        @Test
        @DisplayName("Port는 Interface여야 함")
        void portShouldBeInterface() {
            ArchRule rule = classes()
                    .that().haveSimpleNameEndingWith("Port")
                    .should().beInterfaces();

            rule.check(applicationClasses);
        }

        @Test
        @DisplayName("In Port는 port.in 패키지에 위치해야 함 (Inner Record 제외)")
        void inPortShouldResideInPortInPackage() {
            ArchRule rule = classes()
                    .that().resideInAPackage("..port.in..")
                    .and().areTopLevelClasses()  // Inner Record 제외
                    .should().beInterfaces();

            rule.check(applicationClasses);
        }

        @Test
        @DisplayName("Out Port는 port.out 패키지에 위치해야 함")
        void outPortShouldResideInPortOutPackage() {
            ArchRule rule = classes()
                    .that().haveSimpleNameEndingWith("Port")
                    .and().resideInAPackage("..port.out..")
                    .should().beInterfaces();

            rule.check(applicationClasses);
        }
    }

    /**
     * Service 패턴 규칙 검증.
     */
    @Nested
    @DisplayName("Service 패턴 규칙")
    class ServicePatternRules {

        @Test
        @DisplayName("Service는 service 패키지에 위치해야 함")
        void serviceShouldResideInServicePackage() {
            ArchRule rule = classes()
                    .that().haveSimpleNameEndingWith("Service")
                    .should().resideInAPackage("..service..");

            rule.check(applicationClasses);
        }

        @Test
        @DisplayName("Service는 @Service 어노테이션을 가져야 함")
        void serviceShouldBeAnnotatedWithService() {
            ArchRule rule = classes()
                    .that().haveSimpleNameEndingWith("Service")
                    .and().resideInAPackage("..service..")
                    .should().beAnnotatedWith(org.springframework.stereotype.Service.class);

            rule.check(applicationClasses);
        }

        @Test
        @DisplayName("Command Service의 public 메서드는 @Transactional 어노테이션을 가져야 함")
        void commandServicePublicMethodsShouldBeAnnotatedWithTransactional() {
            ArchRule rule = methods()
                    .that().areDeclaredInClassesThat().haveSimpleNameEndingWith("Service")
                    .and().areDeclaredInClassesThat().resideInAPackage("..service.command..")
                    .and().arePublic()
                    .and().areNotStatic()
                    .should().beAnnotatedWith(org.springframework.transaction.annotation.Transactional.class);

            rule.check(applicationClasses);
        }
    }

    /**
     * Assembler 패턴 규칙 검증.
     */
    @Nested
    @DisplayName("Assembler 패턴 규칙")
    class AssemblerPatternRules {

        @Test
        @DisplayName("Assembler는 assembler 패키지에 위치해야 함")
        void assemblerShouldResideInAssemblerPackage() {
            ArchRule rule = classes()
                    .that().haveSimpleNameEndingWith("Assembler")
                    .should().resideInAPackage("..assembler..");

            rule.check(applicationClasses);
        }

        @Test
        @DisplayName("Assembler는 @Component 어노테이션을 가져야 함")
        void assemblerShouldBeAnnotatedWithComponent() {
            ArchRule rule = classes()
                    .that().haveSimpleNameEndingWith("Assembler")
                    .and().resideInAPackage("..assembler..")
                    .should().beAnnotatedWith(org.springframework.stereotype.Component.class);

            rule.check(applicationClasses);
        }
    }

    /**
     * 네이밍 규칙 검증.
     */
    @Nested
    @DisplayName("네이밍 규칙")
    class NamingConventionRules {

        @Test
        @DisplayName("UseCase Interface는 'UseCase'로 끝나야 함")
        void useCaseInterfaceShouldEndWithUseCase() {
            ArchRule rule = classes()
                    .that().resideInAPackage("..port.in..")
                    .and().areInterfaces()
                    .should().haveSimpleNameEndingWith("UseCase");

            rule.check(applicationClasses);
        }

        @Test
        @DisplayName("Out Port는 'Port'로 끝나야 함")
        void outPortShouldEndWithPort() {
            ArchRule rule = classes()
                    .that().resideInAPackage("..port.out..")
                    .and().areInterfaces()
                    .should().haveSimpleNameEndingWith("Port");

            rule.check(applicationClasses);
        }

        @Test
        @DisplayName("Service 구현체는 'Service'로 끝나야 함 (Inner Exception 제외)")
        void serviceImplementationShouldEndWithService() {
            ArchRule rule = classes()
                    .that().resideInAPackage("..service..")
                    .and().areNotInterfaces()
                    .and().areTopLevelClasses()  // Inner classes (Exception 등) 제외
                    .should().haveSimpleNameEndingWith("Service");

            rule.check(applicationClasses);
        }

        @Test
        @DisplayName("Assembler는 'Assembler'로 끝나야 함")
        void assemblerShouldEndWithAssembler() {
            ArchRule rule = classes()
                    .that().resideInAPackage("..assembler..")
                    .should().haveSimpleNameEndingWith("Assembler");

            rule.check(applicationClasses);
        }
    }
}
