package com.ryuqq.authhub.adapter.out.persistence.architecture.adpater.command;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * CommandAdapter 아키텍처 규칙 검증 테스트
 *
 * <p>CQRS Command Adapter의 규칙을 검증합니다:
 *
 * <p><strong>규칙 레벨:</strong>
 * <ul>
 *   <li>MUST (필수): 위반 시 빌드 실패 - 핵심 아키텍처 규칙
 *   <li>SHOULD (권장): 경고만 표시 - 베스트 프랙티스
 * </ul>
 *
 * <p><strong>제외 대상:</strong>
 * <ul>
 *   <li>RefreshTokenCommandAdapter: 토큰 특성상 표준 CQRS 패턴 적용 불가
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@DisplayName("CommandAdapter 아키텍처 규칙 검증")
class CommandAdapterArchTest {

    private static final String EXCLUDED_ADAPTER = "RefreshToken";

    private static JavaClasses commandAdapterClasses;
    private static boolean hasCommandAdapterClasses;

    @BeforeAll
    static void setUp() {
        commandAdapterClasses =
                new ClassFileImporter().importPackages("com.ryuqq.authhub.adapter.out.persistence");

        hasCommandAdapterClasses =
                commandAdapterClasses.stream()
                        .anyMatch(
                                javaClass ->
                                        javaClass.getSimpleName().endsWith("CommandAdapter")
                                                && !javaClass
                                                        .getSimpleName()
                                                        .contains(EXCLUDED_ADAPTER));
    }

    /**
     * MUST 규칙 (필수) - 위반 시 빌드 실패
     *
     * <p>핵심 아키텍처 규칙으로, 반드시 준수해야 합니다.
     */
    @Nested
    @DisplayName("MUST 규칙 (필수)")
    class MustRules {

        @Test
        @DisplayName("규칙 1: @Component 어노테이션 필수")
        void commandAdapter_MustBeAnnotatedWithComponent() {
            assumeTrue(hasCommandAdapterClasses, "CommandAdapter 클래스가 없으므로 테스트를 스킵합니다");

            ArchRule rule =
                    classes()
                            .that()
                            .haveSimpleNameEndingWith("CommandAdapter")
                            .and()
                            .haveSimpleNameNotContaining(EXCLUDED_ADAPTER)
                            .should()
                            .beAnnotatedWith(org.springframework.stereotype.Component.class)
                            .because("CommandAdapter는 @Component로 Spring Bean 등록이 필수입니다");

            rule.check(commandAdapterClasses);
        }

        @Test
        @DisplayName("규칙 2: *PersistencePort 인터페이스 구현 필수")
        void commandAdapter_MustImplementPersistencePort() {
            assumeTrue(hasCommandAdapterClasses, "CommandAdapter 클래스가 없으므로 테스트를 스킵합니다");

            // 직접 스트림으로 검증 (DescribedPredicate 대신)
            boolean allImplementPort =
                    commandAdapterClasses.stream()
                            .filter(javaClass -> javaClass.getSimpleName().endsWith("CommandAdapter"))
                            .filter(
                                    javaClass ->
                                            !javaClass.getSimpleName().contains(EXCLUDED_ADAPTER))
                            .allMatch(
                                    javaClass ->
                                            javaClass.getAllRawInterfaces().stream()
                                                    .anyMatch(
                                                            iface ->
                                                                    iface.getSimpleName()
                                                                                    .endsWith(
                                                                                            "PersistencePort")
                                                                            || iface.getSimpleName()
                                                                                    .endsWith(
                                                                                            "CommandPort")));

            if (!allImplementPort) {
                throw new AssertionError(
                        "CommandAdapter는 Application Layer의 Persistence Port를 구현해야 합니다");
            }
        }

        @Test
        @DisplayName("규칙 4: 모든 필드는 final 필수")
        void commandAdapter_AllFieldsMustBeFinal() {
            assumeTrue(hasCommandAdapterClasses, "CommandAdapter 클래스가 없으므로 테스트를 스킵합니다");

            ArchRule rule =
                    fields().that()
                            .areDeclaredInClassesThat()
                            .haveSimpleNameEndingWith("CommandAdapter")
                            .and()
                            .areDeclaredInClassesThat()
                            .haveSimpleNameNotContaining(EXCLUDED_ADAPTER)
                            .should()
                            .beFinal()
                            .because("CommandAdapter의 모든 필드는 final로 불변성을 보장해야 합니다");

            rule.check(commandAdapterClasses);
        }

        @Test
        @DisplayName("규칙 5: @Autowired 필드 주입 금지")
        void commandAdapter_MustNotUseFieldInjection() {
            assumeTrue(hasCommandAdapterClasses, "CommandAdapter 클래스가 없으므로 테스트를 스킵합니다");

            ArchRule rule =
                    fields().that()
                            .areDeclaredInClassesThat()
                            .haveSimpleNameEndingWith("CommandAdapter")
                            .and()
                            .areDeclaredInClassesThat()
                            .haveSimpleNameNotContaining(EXCLUDED_ADAPTER)
                            .should()
                            .notBeAnnotatedWith(
                                    org.springframework.beans.factory.annotation.Autowired.class)
                            .because(
                                    "CommandAdapter는 생성자 주입만 허용되며, @Autowired 필드 주입은 금지입니다");

            rule.check(commandAdapterClasses);
        }

        @Test
        @DisplayName("규칙 11: Query 메서드 금지")
        void commandAdapter_MustNotContainQueryMethods() {
            assumeTrue(hasCommandAdapterClasses, "CommandAdapter 클래스가 없으므로 테스트를 스킵합니다");

            ArchRule rule =
                    methods()
                            .that()
                            .areDeclaredInClassesThat()
                            .haveSimpleNameEndingWith("CommandAdapter")
                            .and()
                            .areDeclaredInClassesThat()
                            .haveSimpleNameNotContaining(EXCLUDED_ADAPTER)
                            .should()
                            .haveNameNotMatching("(find|load|get|query|search|list|count|exists).*")
                            .because(
                                    "CommandAdapter는 Query 메서드를 포함하면 안 됩니다. QueryAdapter로 분리하세요");

            rule.check(commandAdapterClasses);
        }

        @Test
        @DisplayName("규칙 12: JpaRepository 의존성 필수")
        void commandAdapter_MustDependOnJpaRepository() {
            assumeTrue(hasCommandAdapterClasses, "CommandAdapter 클래스가 없으므로 테스트를 스킵합니다");

            ArchRule rule =
                    classes()
                            .that()
                            .haveSimpleNameEndingWith("CommandAdapter")
                            .and()
                            .haveSimpleNameNotContaining(EXCLUDED_ADAPTER)
                            .should()
                            .dependOnClassesThat()
                            .haveSimpleNameEndingWith("JpaRepository")
                            .because("CommandAdapter는 JpaRepository를 의존성으로 가져야 합니다");

            rule.check(commandAdapterClasses);
        }

        // 규칙 15 제거: @Override는 SOURCE retention이라 ArchUnit으로 체크 불가
        // 컴파일러가 자동으로 검증하므로 별도 테스트 불필요

        @Test
        @DisplayName("규칙 18: 로깅 금지")
        void commandAdapter_MustNotContainLogging() {
            ArchRule rule =
                    noClasses()
                            .that()
                            .haveSimpleNameEndingWith("CommandAdapter")
                            .and()
                            .haveSimpleNameNotContaining(EXCLUDED_ADAPTER)
                            .should()
                            .accessClassesThat()
                            .haveNameMatching(".*Logger.*")
                            .because("CommandAdapter는 로깅을 포함하지 않습니다. AOP로 처리하세요");

            rule.allowEmptyShould(true).check(commandAdapterClasses);
        }

        @Test
        @DisplayName("규칙 19: Validator 의존성 금지")
        void commandAdapter_MustNotDependOnValidator() {
            ArchRule rule =
                    noClasses()
                            .that()
                            .haveSimpleNameEndingWith("CommandAdapter")
                            .and()
                            .haveSimpleNameNotContaining(EXCLUDED_ADAPTER)
                            .should()
                            .accessClassesThat()
                            .haveNameMatching(".*Validator.*")
                            .because("CommandAdapter는 Validator를 사용하지 않습니다. Domain에서 처리하세요");

            rule.allowEmptyShould(true).check(commandAdapterClasses);
        }
    }

    /**
     * SHOULD 규칙 (권장) - 경고만 표시, 빌드 실패 없음
     *
     * <p>베스트 프랙티스로, 준수를 권장하지만 필수는 아닙니다.
     */
    @Nested
    @DisplayName("SHOULD 규칙 (권장)")
    class ShouldRules {

        @Test
        @DisplayName("권장 3: 정확히 2개 필드 (JpaRepository, Mapper)")
        void commandAdapter_ShouldHaveExactlyTwoFields() {
            assumeTrue(hasCommandAdapterClasses, "CommandAdapter 클래스가 없으므로 테스트를 스킵합니다");

            boolean allMatch =
                    commandAdapterClasses.stream()
                            .filter(javaClass -> javaClass.getSimpleName().endsWith("CommandAdapter"))
                            .filter(
                                    javaClass ->
                                            !javaClass.getSimpleName().contains(EXCLUDED_ADAPTER))
                            .allMatch(javaClass -> javaClass.getAllFields().size() == 2);

            if (!allMatch) {
                System.out.println(
                        "[SHOULD] 권장 위반: 일부 CommandAdapter가 2개 이상의 필드를 가지고 있습니다.");
            }
            // 빌드 실패 없이 경고만 출력
        }

        @Test
        @DisplayName("권장 6: 정확히 1개의 public 메서드")
        void commandAdapter_ShouldHaveExactlyOnePublicMethod() {
            assumeTrue(hasCommandAdapterClasses, "CommandAdapter 클래스가 없으므로 테스트를 스킵합니다");

            boolean allMatch =
                    commandAdapterClasses.stream()
                            .filter(javaClass -> javaClass.getSimpleName().endsWith("CommandAdapter"))
                            .filter(
                                    javaClass ->
                                            !javaClass.getSimpleName().contains(EXCLUDED_ADAPTER))
                            .allMatch(
                                    javaClass ->
                                            javaClass.getMethods().stream()
                                                            .filter(
                                                                    method ->
                                                                            method.getModifiers()
                                                                                    .contains(
                                                                                            JavaModifier
                                                                                                    .PUBLIC))
                                                            .filter(
                                                                    method ->
                                                                            !method.getName()
                                                                                    .equals("<init>"))
                                                            .count()
                                                    == 1);

            if (!allMatch) {
                System.out.println(
                        "[SHOULD] 권장 위반: 일부 CommandAdapter가 1개 이상의 public 메서드를 가지고 있습니다.");
            }
        }

        @Test
        @DisplayName("권장 7: public 메서드명은 'persist'")
        void commandAdapter_ShouldHavePersistMethodName() {
            assumeTrue(hasCommandAdapterClasses, "CommandAdapter 클래스가 없으므로 테스트를 스킵합니다");

            boolean allMatch =
                    commandAdapterClasses.stream()
                            .filter(javaClass -> javaClass.getSimpleName().endsWith("CommandAdapter"))
                            .filter(
                                    javaClass ->
                                            !javaClass.getSimpleName().contains(EXCLUDED_ADAPTER))
                            .allMatch(
                                    javaClass ->
                                            javaClass.getMethods().stream()
                                                    .filter(
                                                            method ->
                                                                    method.getModifiers()
                                                                            .contains(
                                                                                    JavaModifier
                                                                                            .PUBLIC))
                                                    .filter(
                                                            method ->
                                                                    !method.getName().equals("<init>"))
                                                    .allMatch(
                                                            method ->
                                                                    method.getName()
                                                                            .equals("persist")));

            if (!allMatch) {
                System.out.println(
                        "[SHOULD] 권장 위반: 일부 CommandAdapter에 'persist' 외의 public 메서드가 있습니다.");
            }
        }

        @Test
        @DisplayName("권장 8: persist 메서드는 정확히 1개 파라미터")
        void commandAdapter_ShouldHaveExactlyOneParameter() {
            assumeTrue(hasCommandAdapterClasses, "CommandAdapter 클래스가 없으므로 테스트를 스킵합니다");

            boolean allMatch =
                    commandAdapterClasses.stream()
                            .filter(javaClass -> javaClass.getSimpleName().endsWith("CommandAdapter"))
                            .filter(
                                    javaClass ->
                                            !javaClass.getSimpleName().contains(EXCLUDED_ADAPTER))
                            .flatMap(javaClass -> javaClass.getMethods().stream())
                            .filter(method -> method.getName().equals("persist"))
                            .allMatch(method -> method.getRawParameterTypes().size() == 1);

            if (!allMatch) {
                System.out.println(
                        "[SHOULD] 권장 위반: 일부 persist() 메서드가 1개 이상의 파라미터를 가지고 있습니다.");
            }
        }

        @Test
        @DisplayName("권장 9: persist 메서드는 *Id 타입 반환")
        void commandAdapter_ShouldReturnIdType() {
            assumeTrue(hasCommandAdapterClasses, "CommandAdapter 클래스가 없으므로 테스트를 스킵합니다");

            boolean allMatch =
                    commandAdapterClasses.stream()
                            .filter(javaClass -> javaClass.getSimpleName().endsWith("CommandAdapter"))
                            .filter(
                                    javaClass ->
                                            !javaClass.getSimpleName().contains(EXCLUDED_ADAPTER))
                            .flatMap(javaClass -> javaClass.getMethods().stream())
                            .filter(method -> method.getName().equals("persist"))
                            .allMatch(
                                    method ->
                                            method.getRawReturnType().getSimpleName().endsWith("Id"));

            if (!allMatch) {
                System.out.println(
                        "[SHOULD] 권장 위반: 일부 persist() 메서드가 *Id 타입을 반환하지 않습니다.");
            }
        }

        @Test
        @DisplayName("권장 10: Domain Layer 직접 의존성 금지")
        void commandAdapter_ShouldNotDependOnDomainLayer() {
            // Domain 의존성은 Mapper를 통해 간접적으로 발생하므로 권장 수준
            ArchRule rule =
                    noClasses()
                            .that()
                            .haveSimpleNameEndingWith("CommandAdapter")
                            .and()
                            .haveSimpleNameNotContaining(EXCLUDED_ADAPTER)
                            .should()
                            .accessClassesThat()
                            .resideInAnyPackage("..domain..")
                            .because(
                                    "[SHOULD] CommandAdapter는 Domain Layer에 직접 접근을 최소화하세요");

            try {
                rule.check(commandAdapterClasses);
            } catch (AssertionError e) {
                System.out.println("[SHOULD] 권장 위반: " + e.getMessage());
                // 빌드 실패 없이 경고만 출력
            }
        }

        @Test
        @DisplayName("권장 13: @Transactional 금지")
        void commandAdapter_ShouldNotUseTransactional() {
            assumeTrue(hasCommandAdapterClasses, "CommandAdapter 클래스가 없으므로 테스트를 스킵합니다");

            try {
                ArchRule classRule =
                        classes()
                                .that()
                                .haveSimpleNameEndingWith("CommandAdapter")
                                .and()
                                .haveSimpleNameNotContaining(EXCLUDED_ADAPTER)
                                .should()
                                .notBeAnnotatedWith(
                                        org.springframework.transaction.annotation.Transactional
                                                .class)
                                .because(
                                        "[SHOULD] CommandAdapter에서 @Transactional 사용을 권장하지 않습니다. UseCase에서 관리하세요");

                classRule.check(commandAdapterClasses);
            } catch (AssertionError e) {
                System.out.println("[SHOULD] 권장 위반: " + e.getMessage());
            }
        }

        @Test
        @DisplayName("권장 14: Mapper 의존성 권장")
        void commandAdapter_ShouldDependOnMapper() {
            assumeTrue(hasCommandAdapterClasses, "CommandAdapter 클래스가 없으므로 테스트를 스킵합니다");

            try {
                ArchRule rule =
                        classes()
                                .that()
                                .haveSimpleNameEndingWith("CommandAdapter")
                                .and()
                                .haveSimpleNameNotContaining(EXCLUDED_ADAPTER)
                                .should()
                                .dependOnClassesThat()
                                .haveSimpleNameEndingWith("Mapper")
                                .because("[SHOULD] CommandAdapter는 Mapper 사용을 권장합니다");

                rule.check(commandAdapterClasses);
            } catch (AssertionError e) {
                System.out.println("[SHOULD] 권장 위반: " + e.getMessage());
            }
        }

        @Test
        @DisplayName("권장 16: private helper 메서드 금지")
        void commandAdapter_ShouldNotHavePrivateHelperMethods() {
            assumeTrue(hasCommandAdapterClasses, "CommandAdapter 클래스가 없으므로 테스트를 스킵합니다");

            boolean hasPrivateMethods =
                    commandAdapterClasses.stream()
                            .filter(javaClass -> javaClass.getSimpleName().endsWith("CommandAdapter"))
                            .filter(
                                    javaClass ->
                                            !javaClass.getSimpleName().contains(EXCLUDED_ADAPTER))
                            .flatMap(javaClass -> javaClass.getMethods().stream())
                            .anyMatch(
                                    method ->
                                            method.getModifiers().contains(JavaModifier.PRIVATE)
                                                    && !method.getName().equals("<init>"));

            if (hasPrivateMethods) {
                System.out.println(
                        "[SHOULD] 권장 위반: 일부 CommandAdapter에 private helper 메서드가 있습니다.");
            }
        }

        @Test
        @DisplayName("권장 17: 비즈니스 예외 throw 금지")
        void commandAdapter_ShouldNotThrowBusinessExceptions() {
            assumeTrue(hasCommandAdapterClasses, "CommandAdapter 클래스가 없으므로 테스트를 스킵합니다");

            try {
                ArchRule rule =
                        methods()
                                .that()
                                .areDeclaredInClassesThat()
                                .haveSimpleNameEndingWith("CommandAdapter")
                                .and()
                                .areDeclaredInClassesThat()
                                .haveSimpleNameNotContaining(EXCLUDED_ADAPTER)
                                .and()
                                .haveName("persist")
                                .should()
                                .notDeclareThrowableOfType(RuntimeException.class)
                                .because(
                                        "[SHOULD] CommandAdapter는 비즈니스 예외 throw를 권장하지 않습니다");

                rule.check(commandAdapterClasses);
            } catch (AssertionError e) {
                System.out.println("[SHOULD] 권장 위반: " + e.getMessage());
            }
        }

        @Test
        @DisplayName("권장 20: *CommandAdapter 네이밍 규칙")
        void commandAdapter_ShouldFollowNamingConvention() {
            assumeTrue(hasCommandAdapterClasses, "CommandAdapter 클래스가 없으므로 테스트를 스킵합니다");

            // 직접 스트림으로 검증
            boolean allFollowNaming =
                    commandAdapterClasses.stream()
                            .filter(
                                    javaClass ->
                                            javaClass.getAllRawInterfaces().stream()
                                                    .anyMatch(
                                                            iface ->
                                                                    iface.getSimpleName()
                                                                                    .endsWith(
                                                                                            "PersistencePort")
                                                                            || iface.getSimpleName()
                                                                                    .endsWith(
                                                                                            "CommandPort")))
                            .filter(javaClass -> javaClass.getPackageName().contains("adapter"))
                            .filter(
                                    javaClass ->
                                            !javaClass.getSimpleName().contains(EXCLUDED_ADAPTER))
                            .allMatch(
                                    javaClass ->
                                            javaClass.getSimpleName().endsWith("CommandAdapter"));

            if (!allFollowNaming) {
                System.out.println(
                        "[SHOULD] 권장 위반: 일부 Adapter가 *CommandAdapter 네이밍을 따르지 않습니다.");
            }
        }
    }
}
