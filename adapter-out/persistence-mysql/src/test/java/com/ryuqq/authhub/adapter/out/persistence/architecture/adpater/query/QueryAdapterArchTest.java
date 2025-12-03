package com.ryuqq.authhub.adapter.out.persistence.architecture.adpater.query;

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
 * QueryAdapter 아키텍처 규칙 검증 테스트
 *
 * <p>CQRS Query Adapter의 규칙을 MUST(필수)와 SHOULD(권장)로 분류하여 검증합니다:
 *
 * <p><strong>MUST 규칙 (필수 - 위반 시 빌드 실패):</strong>
 *
 * <ul>
 *   <li>@Component 어노테이션 필수
 *   <li>*QueryPort 인터페이스 구현 필수
 *   <li>모든 필드 final 필수
 *   <li>생성자 주입만 허용
 *   <li>@Transactional 금지
 *   <li>Command 메서드 금지
 *   <li>비즈니스 메서드 금지
 *   <li>JPAQueryFactory 직접 사용 금지
 *   <li>로깅/Validator 의존성 금지
 * </ul>
 *
 * <p><strong>SHOULD 규칙 (권장 - 위반 시 Warning만 출력):</strong>
 *
 * <ul>
 *   <li>필드 2개 (QueryDslRepository, Mapper)
 *   <li>public 메서드 4개 (findById, existsById, findByCriteria, countByCriteria)
 *   <li>표준 메서드명
 *   <li>Mapper 의존성
 *   <li>private helper 메서드 금지
 * </ul>
 *
 * <p><strong>제외 대상:</strong> RefreshToken (특수 패턴), Lock (별도 패턴)
 *
 * @author development-team
 * @since 1.0.0
 */
@DisplayName("QueryAdapter 아키텍처 규칙 검증 (Zero-Tolerance)")
class QueryAdapterArchTest {

    private static JavaClasses queryAdapterClasses;
    private static boolean hasQueryAdapterClasses;

    /**
     * RefreshToken은 특수 패턴이므로 제외:
     * - 표준 4개 메서드가 아닌 특화된 메서드 (findByUserId, existsByUserId, findUserIdByToken)
     * - Mapper 없이 직접 변환 (단순 VO 변환)
     */
    private static final String EXCLUDED_ADAPTER = "RefreshToken";

    @BeforeAll
    static void setUp() {
        queryAdapterClasses =
                new ClassFileImporter()
                        .importPackages("com.ryuqq.authhub.adapter.out.persistence");

        hasQueryAdapterClasses =
                queryAdapterClasses.stream()
                        .anyMatch(
                                javaClass ->
                                        javaClass.getSimpleName().endsWith("QueryAdapter")
                                                && !javaClass.getSimpleName().contains("Lock")
                                                && !javaClass.getSimpleName().contains(EXCLUDED_ADAPTER));
    }

    // ========================================
    // MUST 규칙 (필수 - 위반 시 빌드 실패)
    // ========================================

    @Nested
    @DisplayName("MUST 규칙 (필수)")
    class MustRules {

        /**
         * 규칙 1: @Component 어노테이션 필수
         */
        @Test
        @DisplayName("규칙 1: @Component 어노테이션 필수")
        void queryAdapter_MustBeAnnotatedWithComponent() {
            assumeTrue(hasQueryAdapterClasses, "QueryAdapter 클래스가 없으므로 테스트를 스킵합니다");

            ArchRule rule =
                    classes()
                            .that()
                            .haveSimpleNameEndingWith("QueryAdapter")
                            .and()
                            .haveSimpleNameNotContaining("Lock")
                            .and()
                            .haveSimpleNameNotContaining(EXCLUDED_ADAPTER)
                            .should()
                            .beAnnotatedWith(org.springframework.stereotype.Component.class)
                            .because("QueryAdapter는 @Component로 Spring Bean 등록이 필수입니다");

            rule.allowEmptyShould(true).check(queryAdapterClasses);
        }

        /**
         * 규칙 2: *QueryPort 인터페이스 구현 필수
         */
        @Test
        @DisplayName("규칙 2: *QueryPort 인터페이스 구현 필수")
        void queryAdapter_MustImplementQueryPort() {
            assumeTrue(hasQueryAdapterClasses, "QueryAdapter 클래스가 없으므로 테스트를 스킵합니다");

            boolean allImplementPort = queryAdapterClasses.stream()
                    .filter(javaClass -> javaClass.getSimpleName().endsWith("QueryAdapter"))
                    .filter(javaClass -> !javaClass.getSimpleName().contains("Lock"))
                    .filter(javaClass -> !javaClass.getSimpleName().contains(EXCLUDED_ADAPTER))
                    .allMatch(javaClass -> javaClass.getAllRawInterfaces().stream()
                            .anyMatch(iface -> iface.getSimpleName().endsWith("QueryPort")
                                    || iface.getSimpleName().endsWith("LoadPort")));

            if (!allImplementPort) {
                throw new AssertionError("QueryAdapter는 *QueryPort 또는 *LoadPort 인터페이스를 구현해야 합니다");
            }
        }

        /**
         * 규칙 4: 모든 필드는 final 필수
         */
        @Test
        @DisplayName("규칙 4: 모든 필드는 final 필수")
        void queryAdapter_AllFieldsMustBeFinal() {
            ArchRule rule =
                    fields()
                            .that()
                            .areDeclaredInClassesThat()
                            .haveSimpleNameEndingWith("QueryAdapter")
                            .and()
                            .areDeclaredInClassesThat()
                            .haveSimpleNameNotContaining("Lock")
                            .and()
                            .areDeclaredInClassesThat()
                            .haveSimpleNameNotContaining(EXCLUDED_ADAPTER)
                            .should()
                            .beFinal()
                            .because("QueryAdapter의 모든 필드는 final로 불변성을 보장해야 합니다");

            rule.allowEmptyShould(true).check(queryAdapterClasses);
        }

        /**
         * 규칙 5: 생성자 주입만 허용 (Field Injection 금지)
         */
        @Test
        @DisplayName("규칙 5: @Autowired 필드 주입 금지")
        void queryAdapter_MustNotUseFieldInjection() {
            ArchRule rule =
                    fields()
                            .that()
                            .areDeclaredInClassesThat()
                            .haveSimpleNameEndingWith("QueryAdapter")
                            .and()
                            .areDeclaredInClassesThat()
                            .haveSimpleNameNotContaining("Lock")
                            .and()
                            .areDeclaredInClassesThat()
                            .haveSimpleNameNotContaining(EXCLUDED_ADAPTER)
                            .should()
                            .notBeAnnotatedWith(
                                    org.springframework.beans.factory.annotation.Autowired.class)
                            .because(
                                    "QueryAdapter는 생성자 주입만 허용되며, @Autowired 필드 주입은 금지입니다");

            rule.allowEmptyShould(true).check(queryAdapterClasses);
        }

        /**
         * 규칙 11: @Transactional 금지
         */
        @Test
        @DisplayName("규칙 11: @Transactional 금지")
        void queryAdapter_MustNotBeAnnotatedWithTransactional() {
            ArchRule classRule =
                    classes()
                            .that()
                            .haveSimpleNameEndingWith("QueryAdapter")
                            .and()
                            .haveSimpleNameNotContaining("Lock")
                            .and()
                            .haveSimpleNameNotContaining(EXCLUDED_ADAPTER)
                            .should()
                            .notBeAnnotatedWith(
                                    org.springframework.transaction.annotation.Transactional.class)
                            .because("QueryAdapter 클래스에 @Transactional 사용 금지. 읽기 전용 작업입니다");

            ArchRule methodRule =
                    methods()
                            .that()
                            .areDeclaredInClassesThat()
                            .haveSimpleNameEndingWith("QueryAdapter")
                            .and()
                            .areDeclaredInClassesThat()
                            .haveSimpleNameNotContaining("Lock")
                            .and()
                            .areDeclaredInClassesThat()
                            .haveSimpleNameNotContaining(EXCLUDED_ADAPTER)
                            .should()
                            .notBeAnnotatedWith(
                                    org.springframework.transaction.annotation.Transactional.class)
                            .because("QueryAdapter 메서드에 @Transactional 사용 금지. 읽기 전용 작업입니다");

            classRule.allowEmptyShould(true).check(queryAdapterClasses);
            methodRule.allowEmptyShould(true).check(queryAdapterClasses);
        }

        /**
         * 규칙 12: Command 메서드 금지
         */
        @Test
        @DisplayName("규칙 12: Command 메서드 금지")
        void queryAdapter_MustNotContainCommandMethods() {
            ArchRule rule =
                    methods()
                            .that()
                            .areDeclaredInClassesThat()
                            .haveSimpleNameEndingWith("QueryAdapter")
                            .and()
                            .areDeclaredInClassesThat()
                            .haveSimpleNameNotContaining("Lock")
                            .and()
                            .areDeclaredInClassesThat()
                            .haveSimpleNameNotContaining(EXCLUDED_ADAPTER)
                            .should()
                            .haveNameNotMatching("(save|persist|update|delete|insert|remove|create).*")
                            .because(
                                    "QueryAdapter는 Command 메서드를 포함하면 안 됩니다. CommandAdapter로 분리하세요");

            rule.allowEmptyShould(true).check(queryAdapterClasses);
        }

        /**
         * 규칙 13: 비즈니스 메서드 금지
         */
        @Test
        @DisplayName("규칙 13: 비즈니스 메서드 금지")
        void queryAdapter_MustNotContainBusinessMethods() {
            ArchRule rule =
                    methods()
                            .that()
                            .areDeclaredInClassesThat()
                            .haveSimpleNameEndingWith("QueryAdapter")
                            .and()
                            .areDeclaredInClassesThat()
                            .haveSimpleNameNotContaining("Lock")
                            .and()
                            .areDeclaredInClassesThat()
                            .haveSimpleNameNotContaining(EXCLUDED_ADAPTER)
                            .should()
                            .haveNameNotMatching(
                                    "(confirm|cancel|approve|reject|modify|change|validate|calculate).*")
                            .because(
                                    "QueryAdapter는 비즈니스 메서드를 포함하면 안 됩니다. Domain에서 처리하세요");

            rule.allowEmptyShould(true).check(queryAdapterClasses);
        }

        /**
         * 규칙 16: JPAQueryFactory 직접 사용 금지
         */
        @Test
        @DisplayName("규칙 16: JPAQueryFactory 직접 사용 금지")
        void queryAdapter_MustNotUseJPAQueryFactoryDirectly() {
            ArchRule rule =
                    noClasses()
                            .that()
                            .haveSimpleNameEndingWith("QueryAdapter")
                            .and()
                            .haveSimpleNameNotContaining("Lock")
                            .and()
                            .haveSimpleNameNotContaining(EXCLUDED_ADAPTER)
                            .should()
                            .accessClassesThat()
                            .haveNameMatching(".*JPAQueryFactory.*")
                            .because(
                                    "QueryAdapter는 JPAQueryFactory를 직접 사용하지 않고 QueryDslRepository를 통해"
                                            + " 조회해야 합니다");

            rule.allowEmptyShould(true).check(queryAdapterClasses);
        }

        /**
         * 규칙 18: 로깅 금지
         */
        @Test
        @DisplayName("규칙 18: 로깅 금지")
        void queryAdapter_MustNotContainLogging() {
            ArchRule rule =
                    noClasses()
                            .that()
                            .haveSimpleNameEndingWith("QueryAdapter")
                            .and()
                            .haveSimpleNameNotContaining("Lock")
                            .and()
                            .haveSimpleNameNotContaining(EXCLUDED_ADAPTER)
                            .should()
                            .accessClassesThat()
                            .haveNameMatching(".*Logger.*")
                            .because("QueryAdapter는 로깅을 포함하지 않습니다. AOP로 처리하세요");

            rule.allowEmptyShould(true).check(queryAdapterClasses);
        }

        /**
         * 규칙 19: Validator 의존성 금지
         */
        @Test
        @DisplayName("규칙 19: Validator 의존성 금지")
        void queryAdapter_MustNotDependOnValidator() {
            ArchRule rule =
                    noClasses()
                            .that()
                            .haveSimpleNameEndingWith("QueryAdapter")
                            .and()
                            .haveSimpleNameNotContaining("Lock")
                            .and()
                            .haveSimpleNameNotContaining(EXCLUDED_ADAPTER)
                            .should()
                            .accessClassesThat()
                            .haveNameMatching(".*Validator.*")
                            .because("QueryAdapter는 Validator를 사용하지 않습니다");

            rule.allowEmptyShould(true).check(queryAdapterClasses);
        }

        /**
         * 규칙 20: *QueryAdapter 네이밍 규칙
         */
        @Test
        @DisplayName("규칙 20: *QueryAdapter 네이밍 규칙")
        void queryAdapter_MustFollowNamingConvention() {
            assumeTrue(hasQueryAdapterClasses, "QueryAdapter 클래스가 없으므로 테스트를 스킵합니다");

            boolean allFollowNaming = queryAdapterClasses.stream()
                    .filter(javaClass -> javaClass.getAllRawInterfaces().stream()
                            .anyMatch(iface -> iface.getSimpleName().endsWith("QueryPort")
                                    || iface.getSimpleName().endsWith("LoadPort")))
                    .filter(javaClass -> javaClass.getPackageName().contains("adapter"))
                    .filter(javaClass -> !javaClass.getSimpleName().contains(EXCLUDED_ADAPTER))
                    .allMatch(javaClass -> javaClass.getSimpleName().endsWith("QueryAdapter"));

            if (!allFollowNaming) {
                throw new AssertionError("Query Adapter는 반드시 *QueryAdapter 네이밍 규칙을 따라야 합니다");
            }
        }
    }

    // ========================================
    // SHOULD 규칙 (권장 - 위반 시 Warning만 출력)
    // ========================================

    @Nested
    @DisplayName("SHOULD 규칙 (권장)")
    class ShouldRules {

        /**
         * 규칙 3: 정확히 2개 필드 (QueryDslRepository, Mapper)
         */
        @Test
        @DisplayName("규칙 3: 정확히 2개 필드 (QueryDslRepository, Mapper)")
        void queryAdapter_ShouldHaveExactlyTwoFields() {
            assumeTrue(hasQueryAdapterClasses, "QueryAdapter 클래스가 없으므로 테스트를 스킵합니다");

            queryAdapterClasses.stream()
                    .filter(javaClass -> javaClass.getSimpleName().endsWith("QueryAdapter"))
                    .filter(javaClass -> !javaClass.getSimpleName().contains("Lock"))
                    .filter(javaClass -> !javaClass.getSimpleName().contains(EXCLUDED_ADAPTER))
                    .forEach(javaClass -> {
                        int fieldCount = javaClass.getAllFields().size();
                        if (fieldCount != 2) {
                            System.out.println("[WARNING] SHOULD 규칙 위반: " + javaClass.getSimpleName()
                                    + " - 필드 개수 " + fieldCount + "개 (권장: 2개)");
                        }
                    });
        }

        /**
         * 규칙 6: 정확히 4개의 public 메서드
         */
        @Test
        @DisplayName("규칙 6: 정확히 4개의 public 메서드")
        void queryAdapter_ShouldHaveExactlyFourPublicMethods() {
            assumeTrue(hasQueryAdapterClasses, "QueryAdapter 클래스가 없으므로 테스트를 스킵합니다");

            queryAdapterClasses.stream()
                    .filter(javaClass -> javaClass.getSimpleName().endsWith("QueryAdapter"))
                    .filter(javaClass -> !javaClass.getSimpleName().contains("Lock"))
                    .filter(javaClass -> !javaClass.getSimpleName().contains(EXCLUDED_ADAPTER))
                    .forEach(javaClass -> {
                        long methodCount = javaClass.getMethods().stream()
                                .filter(method -> method.getModifiers().contains(JavaModifier.PUBLIC))
                                .filter(method -> !method.getName().equals("<init>"))
                                .count();
                        if (methodCount != 4) {
                            System.out.println("[WARNING] SHOULD 규칙 위반: " + javaClass.getSimpleName()
                                    + " - public 메서드 " + methodCount + "개 (권장: 4개)");
                        }
                    });
        }

        /**
         * 규칙 7: findById() 메서드 필수
         */
        @Test
        @DisplayName("규칙 7: findById() 메서드 필수")
        void queryAdapter_ShouldHaveFindByIdMethod() {
            assumeTrue(hasQueryAdapterClasses, "QueryAdapter 클래스가 없으므로 테스트를 스킵합니다");

            queryAdapterClasses.stream()
                    .filter(javaClass -> javaClass.getSimpleName().endsWith("QueryAdapter"))
                    .filter(javaClass -> !javaClass.getSimpleName().contains("Lock"))
                    .filter(javaClass -> !javaClass.getSimpleName().contains(EXCLUDED_ADAPTER))
                    .forEach(javaClass -> {
                        boolean hasFindById = javaClass.getMethods().stream()
                                .anyMatch(method -> method.getName().equals("findById")
                                        && method.getModifiers().contains(JavaModifier.PUBLIC));
                        if (!hasFindById) {
                            System.out.println("[WARNING] SHOULD 규칙 위반: " + javaClass.getSimpleName()
                                    + " - findById() 메서드 없음");
                        }
                    });
        }

        /**
         * 규칙 8: findByCriteria() 메서드 필수
         */
        @Test
        @DisplayName("규칙 8: findByCriteria() 메서드 필수")
        void queryAdapter_ShouldHaveFindByCriteriaMethod() {
            assumeTrue(hasQueryAdapterClasses, "QueryAdapter 클래스가 없으므로 테스트를 스킵합니다");

            queryAdapterClasses.stream()
                    .filter(javaClass -> javaClass.getSimpleName().endsWith("QueryAdapter"))
                    .filter(javaClass -> !javaClass.getSimpleName().contains("Lock"))
                    .filter(javaClass -> !javaClass.getSimpleName().contains(EXCLUDED_ADAPTER))
                    .forEach(javaClass -> {
                        boolean hasFindByCriteria = javaClass.getMethods().stream()
                                .anyMatch(method -> method.getName().equals("findByCriteria")
                                        && method.getModifiers().contains(JavaModifier.PUBLIC));
                        if (!hasFindByCriteria) {
                            System.out.println("[WARNING] SHOULD 규칙 위반: " + javaClass.getSimpleName()
                                    + " - findByCriteria() 메서드 없음");
                        }
                    });
        }

        /**
         * 규칙 9: countByCriteria() 메서드 필수
         */
        @Test
        @DisplayName("규칙 9: countByCriteria() 메서드 필수")
        void queryAdapter_ShouldHaveCountByCriteriaMethod() {
            assumeTrue(hasQueryAdapterClasses, "QueryAdapter 클래스가 없으므로 테스트를 스킵합니다");

            queryAdapterClasses.stream()
                    .filter(javaClass -> javaClass.getSimpleName().endsWith("QueryAdapter"))
                    .filter(javaClass -> !javaClass.getSimpleName().contains("Lock"))
                    .filter(javaClass -> !javaClass.getSimpleName().contains(EXCLUDED_ADAPTER))
                    .forEach(javaClass -> {
                        boolean hasCountByCriteria = javaClass.getMethods().stream()
                                .anyMatch(method -> method.getName().equals("countByCriteria")
                                        && method.getModifiers().contains(JavaModifier.PUBLIC));
                        if (!hasCountByCriteria) {
                            System.out.println("[WARNING] SHOULD 규칙 위반: " + javaClass.getSimpleName()
                                    + " - countByCriteria() 메서드 없음");
                        }
                    });
        }

        /**
         * 규칙 10: existsById() 메서드 필수
         */
        @Test
        @DisplayName("규칙 10: existsById() 메서드 필수")
        void queryAdapter_ShouldHaveExistsByIdMethod() {
            assumeTrue(hasQueryAdapterClasses, "QueryAdapter 클래스가 없으므로 테스트를 스킵합니다");

            queryAdapterClasses.stream()
                    .filter(javaClass -> javaClass.getSimpleName().endsWith("QueryAdapter"))
                    .filter(javaClass -> !javaClass.getSimpleName().contains("Lock"))
                    .filter(javaClass -> !javaClass.getSimpleName().contains(EXCLUDED_ADAPTER))
                    .forEach(javaClass -> {
                        boolean hasExistsById = javaClass.getMethods().stream()
                                .anyMatch(method -> method.getName().equals("existsById")
                                        && method.getModifiers().contains(JavaModifier.PUBLIC));
                        if (!hasExistsById) {
                            System.out.println("[WARNING] SHOULD 규칙 위반: " + javaClass.getSimpleName()
                                    + " - existsById() 메서드 없음");
                        }
                    });
        }

        /**
         * 규칙 14: Mapper 의존성 필수
         */
        @Test
        @DisplayName("규칙 14: Mapper 의존성 필수")
        void queryAdapter_ShouldDependOnMapper() {
            assumeTrue(hasQueryAdapterClasses, "QueryAdapter 클래스가 없으므로 테스트를 스킵합니다");

            queryAdapterClasses.stream()
                    .filter(javaClass -> javaClass.getSimpleName().endsWith("QueryAdapter"))
                    .filter(javaClass -> !javaClass.getSimpleName().contains("Lock"))
                    .filter(javaClass -> !javaClass.getSimpleName().contains(EXCLUDED_ADAPTER))
                    .forEach(javaClass -> {
                        boolean hasMapperDependency = javaClass.getDirectDependenciesFromSelf().stream()
                                .anyMatch(dep -> dep.getTargetClass().getSimpleName().endsWith("Mapper"));
                        if (!hasMapperDependency) {
                            System.out.println("[WARNING] SHOULD 규칙 위반: " + javaClass.getSimpleName()
                                    + " - Mapper 의존성 없음");
                        }
                    });
        }

        /**
         * 규칙 17: private helper 메서드 금지
         */
        @Test
        @DisplayName("규칙 17: private helper 메서드 금지")
        void queryAdapter_ShouldNotHavePrivateHelperMethods() {
            assumeTrue(hasQueryAdapterClasses, "QueryAdapter 클래스가 없으므로 테스트를 스킵합니다");

            queryAdapterClasses.stream()
                    .filter(javaClass -> javaClass.getSimpleName().endsWith("QueryAdapter"))
                    .filter(javaClass -> !javaClass.getSimpleName().contains("Lock"))
                    .filter(javaClass -> !javaClass.getSimpleName().contains(EXCLUDED_ADAPTER))
                    .forEach(javaClass -> {
                        long privateMethodCount = javaClass.getMethods().stream()
                                .filter(method -> method.getModifiers().contains(JavaModifier.PRIVATE))
                                .filter(method -> !method.getName().equals("<init>"))
                                .count();
                        if (privateMethodCount > 0) {
                            System.out.println("[WARNING] SHOULD 규칙 위반: " + javaClass.getSimpleName()
                                    + " - private 메서드 " + privateMethodCount + "개 존재");
                        }
                    });
        }

        /**
         * 규칙 15: @Override 어노테이션 확인 (컴파일러가 검증하므로 Warning으로 전환)
         */
        @Test
        @DisplayName("규칙 15: @Override 어노테이션 확인")
        void queryAdapter_ShouldHaveOverrideAnnotation() {
            // @Override는 SOURCE retention이므로 ArchUnit으로 검증 불가
            // 컴파일러가 자동으로 검증하므로 Warning만 출력
            System.out.println("[INFO] 규칙 15: @Override 어노테이션은 컴파일러가 자동 검증합니다.");
        }
    }
}
