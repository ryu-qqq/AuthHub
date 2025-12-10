package com.ryuqq.authhub.application.architecture.facade;

import static com.tngtech.archunit.core.domain.JavaModifier.FINAL;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Component;

/**
 * Facade ArchUnit 검증 테스트 (Zero-Tolerance)
 *
 * <p>핵심 철학: Facade는 여러 Manager 조합만, 비즈니스 로직 금지
 *
 * <h3>Facade 의존성 규칙:</h3>
 *
 * <ul>
 *   <li>TransactionManager 의존 필수 (여러 Manager 조합)
 *   <li>TransactionEventRegistry 의존 허용 (Event 발행)
 *   <li>Factory 의존 금지 (Factory는 Service 책임)
 *   <li>Port/Repository 직접 의존 금지
 * </ul>
 *
 * <h3>Event 발행 흐름:</h3>
 *
 * <pre>
 * Facade → TransactionEventRegistry.registerAfterCommit(event)
 *       → 트랜잭션 커밋 후 → ApplicationEventPublisher.publishEvent(event)
 * </pre>
 */
@DisplayName("Facade ArchUnit Tests (Zero-Tolerance)")
@Tag("architecture")
@Tag("facade")
class FacadeArchTest {

    private static JavaClasses classes;
    private static boolean hasFacadeClasses;

    @BeforeAll
    static void setUp() {
        classes = new ClassFileImporter().importPackages("com.ryuqq.application");

        hasFacadeClasses =
                classes.stream()
                        .anyMatch(javaClass -> javaClass.getSimpleName().endsWith("Facade"));
    }

    // ==================== 기본 구조 규칙 ====================

    @Nested
    @DisplayName("기본 구조 규칙")
    class BasicStructureRules {

        @Test
        @DisplayName("[필수] Facade는 @Component 어노테이션을 가져야 한다")
        void facade_MustHaveComponentAnnotation() {
            assumeTrue(hasFacadeClasses, "Facade 클래스가 없어 테스트를 스킵합니다");

            ArchRule rule =
                    classes()
                            .that()
                            .haveSimpleNameEndingWith("Facade")
                            .should()
                            .beAnnotatedWith(Component.class)
                            .because("Facade는 Spring Bean으로 등록되어야 합니다");

            rule.check(classes);
        }

        @Test
        @DisplayName("[필수] facade 패키지의 클래스는 'Facade' 접미사를 가져야 한다")
        void facade_MustHaveCorrectSuffix() {
            assumeTrue(hasFacadeClasses, "Facade 클래스가 없어 테스트를 스킵합니다");

            ArchRule rule =
                    classes()
                            .that()
                            .resideInAPackage("..application..facade..")
                            .and()
                            .areNotInterfaces()
                            .and()
                            .areNotEnums()
                            .should()
                            .haveSimpleNameEndingWith("Facade")
                            .because("facade 패키지의 클래스는 'Facade' 접미사를 사용해야 합니다");

            rule.check(classes);
        }

        @Test
        @DisplayName("[필수] Facade는 ..application..facade.. 패키지에 위치해야 한다")
        void facade_MustBeInCorrectPackage() {
            assumeTrue(hasFacadeClasses, "Facade 클래스가 없어 테스트를 스킵합니다");

            ArchRule rule =
                    classes()
                            .that()
                            .haveSimpleNameEndingWith("Facade")
                            .should()
                            .resideInAPackage("..application..facade..")
                            .because("Facade는 application.*.facade 패키지에 위치해야 합니다");

            rule.check(classes);
        }

        @Test
        @DisplayName("[필수] Facade는 final 클래스가 아니어야 한다")
        void facade_MustNotBeFinal() {
            assumeTrue(hasFacadeClasses, "Facade 클래스가 없어 테스트를 스킵합니다");

            ArchRule rule =
                    classes()
                            .that()
                            .haveSimpleNameEndingWith("Facade")
                            .should()
                            .notHaveModifier(FINAL)
                            .because("Spring 프록시 생성을 위해 Facade가 final이 아니어야 합니다");

            rule.check(classes);
        }
    }

    // ==================== 메서드 규칙 ====================

    @Nested
    @DisplayName("메서드 규칙")
    class MethodRules {

        @Test
        @DisplayName("[권장] Facade의 public 메서드는 persist로 시작해야 한다")
        void facade_MethodsShouldStartWithPersist() {
            assumeTrue(hasFacadeClasses, "Facade 클래스가 없어 테스트를 스킵합니다");

            ArchRule rule =
                    methods()
                            .that()
                            .areDeclaredInClassesThat()
                            .haveSimpleNameEndingWith("Facade")
                            .and()
                            .arePublic()
                            .and()
                            .doNotHaveFullName(".*<init>.*")
                            .should()
                            .haveNameMatching("persist.*")
                            .because("Facade 메서드는 persist*() 네이밍을 권장합니다 (save, create 금지)");

            rule.check(classes);
        }

        @Test
        @DisplayName("[필수] Facade의 @Transactional 메서드는 public이어야 한다")
        void facade_TransactionalMethodsMustBePublic() {
            assumeTrue(hasFacadeClasses, "Facade 클래스가 없어 테스트를 스킵합니다");

            ArchRule rule =
                    methods()
                            .that()
                            .areDeclaredInClassesThat()
                            .haveSimpleNameEndingWith("Facade")
                            .and()
                            .areAnnotatedWith(
                                    "org.springframework.transaction.annotation.Transactional")
                            .should()
                            .bePublic()
                            .because("Facade의 @Transactional은 public 메서드에서만 유효합니다");

            rule.check(classes);
        }
    }

    // ==================== 금지 규칙 (Zero-Tolerance) ====================

    @Nested
    @DisplayName("금지 규칙 (Zero-Tolerance)")
    class ProhibitionRules {

        @Test
        @DisplayName("[금지] Facade는 @Service 어노테이션을 가지지 않아야 한다")
        void facade_MustNotHaveServiceAnnotation() {
            assumeTrue(hasFacadeClasses, "Facade 클래스가 없어 테스트를 스킵합니다");

            ArchRule rule =
                    noClasses()
                            .that()
                            .haveSimpleNameEndingWith("Facade")
                            .should()
                            .beAnnotatedWith("org.springframework.stereotype.Service")
                            .because("Facade는 @Service가 아닌 @Component를 사용해야 합니다");

            rule.check(classes);
        }

        @Test
        @DisplayName("[금지] Facade는 클래스 레벨 @Transactional을 가지지 않아야 한다")
        void facade_MustNotHaveClassLevelTransactional() {
            assumeTrue(hasFacadeClasses, "Facade 클래스가 없어 테스트를 스킵합니다");

            ArchRule rule =
                    noClasses()
                            .that()
                            .haveSimpleNameEndingWith("Facade")
                            .should()
                            .beAnnotatedWith(
                                    "org.springframework.transaction.annotation.Transactional")
                            .because(
                                    "Facade는 클래스 레벨 @Transactional 금지. "
                                            + "메서드 단위로 트랜잭션을 관리해야 합니다.");

            rule.check(classes);
        }

        @Test
        @DisplayName("[금지] Facade는 Lombok 어노테이션을 가지지 않아야 한다")
        void facade_MustNotUseLombok() {
            assumeTrue(hasFacadeClasses, "Facade 클래스가 없어 테스트를 스킵합니다");

            ArchRule rule =
                    noClasses()
                            .that()
                            .haveSimpleNameEndingWith("Facade")
                            .should()
                            .beAnnotatedWith("lombok.Data")
                            .orShould()
                            .beAnnotatedWith("lombok.Builder")
                            .orShould()
                            .beAnnotatedWith("lombok.Getter")
                            .orShould()
                            .beAnnotatedWith("lombok.Setter")
                            .orShould()
                            .beAnnotatedWith("lombok.AllArgsConstructor")
                            .orShould()
                            .beAnnotatedWith("lombok.NoArgsConstructor")
                            .orShould()
                            .beAnnotatedWith("lombok.RequiredArgsConstructor")
                            .because("Facade는 Plain Java를 사용해야 합니다 (Lombok 금지)");

            rule.check(classes);
        }

        @Test
        @DisplayName("[금지] Facade는 Port 인터페이스를 직접 의존하지 않아야 한다")
        void facade_MustNotDependOnPorts() {
            assumeTrue(hasFacadeClasses, "Facade 클래스가 없어 테스트를 스킵합니다");

            ArchRule rule =
                    noClasses()
                            .that()
                            .haveSimpleNameEndingWith("Facade")
                            .should()
                            .dependOnClassesThat()
                            .haveNameMatching(".*Port")
                            .because(
                                    "Facade는 Port를 직접 주입받지 않습니다. "
                                            + "TransactionManager를 통해 Port에 접근합니다.");

            rule.check(classes);
        }

        @Test
        @DisplayName("[금지] Facade는 Repository를 직접 의존하지 않아야 한다")
        void facade_MustNotDependOnRepositories() {
            assumeTrue(hasFacadeClasses, "Facade 클래스가 없어 테스트를 스킵합니다");

            ArchRule rule =
                    noClasses()
                            .that()
                            .haveSimpleNameEndingWith("Facade")
                            .should()
                            .dependOnClassesThat()
                            .haveNameMatching(".*Repository")
                            .because(
                                    "Facade는 Repository를 직접 주입받지 않습니다. "
                                            + "TransactionManager를 통해 접근합니다.");

            rule.check(classes);
        }

        @Test
        @DisplayName("[금지] Facade는 Factory를 의존하지 않아야 한다")
        void facade_MustNotDependOnFactories() {
            assumeTrue(hasFacadeClasses, "Facade 클래스가 없어 테스트를 스킵합니다");

            ArchRule rule =
                    noClasses()
                            .that()
                            .haveSimpleNameEndingWith("Facade")
                            .should()
                            .dependOnClassesThat()
                            .haveSimpleNameEndingWith("Factory")
                            .because(
                                    "Facade는 Factory를 의존하지 않아야 합니다. "
                                            + "Factory는 Service 책임입니다. "
                                            + "Facade는 Manager 조합과 Event 발행만 담당합니다.");

            rule.check(classes);
        }
    }

    // ==================== 의존성 규칙 ====================

    @Nested
    @DisplayName("의존성 규칙")
    class DependencyRules {

        @Test
        @DisplayName("[필수] Facade는 Application Layer와 Domain Layer만 의존해야 한다")
        void facade_MustOnlyDependOnApplicationAndDomainLayers() {
            assumeTrue(hasFacadeClasses, "Facade 클래스가 없어 테스트를 스킵합니다");

            ArchRule rule =
                    classes()
                            .that()
                            .haveSimpleNameEndingWith("Facade")
                            .should()
                            .onlyAccessClassesThat()
                            .resideInAnyPackage(
                                    "com.ryuqq.application..",
                                    "com.ryuqq.domain..",
                                    "org.springframework..",
                                    "java..",
                                    "jakarta..")
                            .because("Facade는 Application Layer와 Domain Layer만 의존해야 합니다");

            rule.check(classes);
        }

        @Test
        @DisplayName("[필수] Facade는 TransactionManager에 의존해야 한다")
        void facade_MustDependOnTransactionManager() {
            assumeTrue(hasFacadeClasses, "Facade 클래스가 없어 테스트를 스킵합니다");

            ArchRule rule =
                    classes()
                            .that()
                            .haveSimpleNameEndingWith("Facade")
                            .should()
                            .dependOnClassesThat()
                            .haveNameMatching(".*TransactionManager")
                            .because("Facade는 TransactionManager를 조합해야 합니다");

            rule.check(classes);
        }

        @Test
        @DisplayName("[허용] Facade는 TransactionEventRegistry를 의존할 수 있다")
        void facade_CanDependOnTransactionEventRegistry() {
            assumeTrue(hasFacadeClasses, "Facade 클래스가 없어 테스트를 스킵합니다");

            // 이 테스트는 TransactionEventRegistry 의존이 허용됨을 문서화하는 역할
            // 실제로 의존하지 않아도 통과하지만, 의존해도 금지 규칙에 걸리지 않음을 보장
            ArchRule rule =
                    classes()
                            .that()
                            .haveSimpleNameEndingWith("Facade")
                            .should()
                            .dependOnClassesThat()
                            .haveSimpleNameEndingWith("Manager")
                            .orShould()
                            .dependOnClassesThat()
                            .haveSimpleNameEndingWith("EventRegistry")
                            .because(
                                    "Facade는 Manager와 EventRegistry를 의존할 수 있습니다."
                                        + " TransactionEventRegistry를 통해 트랜잭션 커밋 후 Event를 발행합니다.");

            rule.check(classes);
        }
    }
}
