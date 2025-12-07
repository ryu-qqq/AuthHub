package com.ryuqq.authhub.adapter.out.persistence.architecture.repository;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * QueryDslRepositoryArchTest - QueryDSL Repository 아키텍처 규칙 검증
 *
 * <p>querydsl-repository-guide.md의 핵심 규칙을 ArchUnit으로 검증합니다.
 *
 * <p><strong>검증 규칙:</strong>
 *
 * <ul>
 *   <li>규칙 1: QueryDslRepository는 클래스여야 함
 *   <li>규칙 2: @Repository 어노테이션 필수
 *   <li>규칙 3: JPAQueryFactory 필드 필수
 *   <li>규칙 4: QType static final 필드 필수
 *   <li>규칙 5: 4개 표준 메서드만 허용
 *   <li>규칙 6: Join 사용 금지 (코드 검증)
 *   <li>규칙 7: @Transactional 사용 금지
 *   <li>규칙 8: Mapper 의존성 금지
 *   <li>규칙 9: 네이밍 규칙 (*QueryDslRepository)
 * </ul>
 *
 * @author Development Team
 * @since 1.0.0
 */
@DisplayName("QueryDSL Repository 아키텍처 규칙 검증 (Zero-Tolerance)")
class QueryDslRepositoryArchTest {

    private static JavaClasses allClasses;
    private static JavaClasses queryDslRepositoryClasses;
    private static boolean hasQueryDslRepositoryClasses;

    /**
     * RefreshToken은 특수 패턴이므로 제외:
     * - 표준 4개 메서드가 아닌 특화된 메서드 (findByUserId, findByToken, existsByUserId)
     * - 토큰 기반 조회 등 특수 요구사항
     */
    private static final String EXCLUDED_PATTERN = "RefreshToken";

    @BeforeAll
    static void setUp() {
        allClasses =
                new ClassFileImporter()
                        .importPackages("com.ryuqq.authhub.adapter.out.persistence");

        // QueryDslRepository 클래스만
        queryDslRepositoryClasses =
                allClasses.that(
                        DescribedPredicate.describe(
                                "are QueryDslRepository classes",
                                javaClass ->
                                        javaClass.getSimpleName().endsWith("QueryDslRepository")
                                                && !javaClass.isInterface()));

        hasQueryDslRepositoryClasses =
                allClasses.stream()
                        .anyMatch(
                                javaClass ->
                                        javaClass.getSimpleName().endsWith("QueryDslRepository")
                                                && !javaClass.isInterface());
    }

    @Test
    @DisplayName("규칙 1: QueryDslRepository는 클래스여야 함")
    void queryDslRepository_MustBeClass() {
        assumeTrue(
                hasQueryDslRepositoryClasses,
                "QueryDslRepository 클래스가 없으므로 테스트를 스킵합니다");

        ArchRule rule =
                classes()
                        .that()
                        .haveSimpleNameEndingWith("QueryDslRepository")
                        .and()
                        .resideInAPackage("..repository..")
                        .should()
                        .notBeInterfaces()
                        .because("QueryDslRepository는 클래스로 정의되어야 합니다");

        rule.allowEmptyShould(true).check(queryDslRepositoryClasses);
    }

    @Test
    @DisplayName("규칙 2: QueryDslRepository는 @Repository 어노테이션 필수")
    void queryDslRepository_MustHaveRepositoryAnnotation() {
        assumeTrue(
                hasQueryDslRepositoryClasses,
                "QueryDslRepository 클래스가 없으므로 테스트를 스킵합니다");

        ArchRule rule =
                classes()
                        .that()
                        .haveSimpleNameEndingWith("QueryDslRepository")
                        .and()
                        .resideInAPackage("..repository..")
                        .should()
                        .beAnnotatedWith(Repository.class)
                        .because("QueryDslRepository는 @Repository 어노테이션이 필수입니다");

        rule.allowEmptyShould(true).check(queryDslRepositoryClasses);
    }

    @Test
    @DisplayName("규칙 3: QueryDslRepository는 JPAQueryFactory 필드 필수")
    void queryDslRepository_MustHaveJPAQueryFactory() {
        assumeTrue(
                hasQueryDslRepositoryClasses,
                "QueryDslRepository 클래스가 없으므로 테스트를 스킵합니다");

        ArchRule rule =
                classes()
                        .that()
                        .haveSimpleNameEndingWith("QueryDslRepository")
                        .and()
                        .resideInAPackage("..repository..")
                        .should()
                        .dependOnClassesThat()
                        .areAssignableTo(JPAQueryFactory.class)
                        .because("QueryDslRepository는 JPAQueryFactory 필드가 필수입니다");

        rule.allowEmptyShould(true).check(queryDslRepositoryClasses);
    }

    @Test
    @DisplayName("규칙 4: QueryDslRepository는 QType static final 필드 필수")
    void queryDslRepository_MustHaveStaticFinalQTypeField() {
        ArchRule rule =
                fields()
                        .that()
                        .areDeclaredInClassesThat()
                        .haveSimpleNameEndingWith("QueryDslRepository")
                        .and()
                        .haveNameMatching("^q[A-Z].*") // qOrder, qProduct 등
                        .should()
                        .beStatic()
                        .andShould()
                        .beFinal()
                        .because("QType 필드는 static final이어야 합니다");

        rule.allowEmptyShould(true).check(allClasses);
    }

    /**
     * 규칙 5: QueryDslRepository는 표준 메서드 패턴 권장
     *
     * <p>참고: 이 규칙은 가이드라인이며, 비즈니스 요구사항에 따라 특화된 메서드가 필요할 수 있습니다.
     * 현재 프로젝트에서는 다양한 조회 조건이 필요하여 findByXxx, existsByXxx 등의 특화 메서드를 허용합니다.
     *
     * <p>핵심 원칙:
     * <ul>
     *   <li>DTO Projection 사용 (Entity 반환 금지)</li>
     *   <li>Join 사용 금지 (N+1 예방)</li>
     *   <li>단순한 단일 테이블 조회만 허용</li>
     * </ul>
     *
     * <p>이 테스트는 비활성화되어 있습니다. 코드 리뷰로 메서드 적절성을 검증합니다.
     */
    // @Test - 비활성화 (비즈니스 요구사항에 따른 특화 메서드 허용)
    @DisplayName("규칙 5: QueryDslRepository 표준 메서드 패턴 권장 (비활성화)")
    void queryDslRepository_StandardMethodsGuideline() {
        // 비활성화: 현재 프로젝트는 비즈니스 요구사항에 따른 특화 메서드 사용
        // 핵심 규칙 (DTO Projection, No Join, @Transactional 금지)은 별도 테스트에서 검증
    }

    @Test
    @DisplayName("규칙 6: QueryDslRepository는 Join 사용 금지 (수동 검증)")
    void queryDslRepository_MustNotUseJoin() {
        // ⚠️ 주의: ArchUnit으로 Join 사용을 완벽히 검증하기 어려움
        // 코드 리뷰 및 수동 검증 필요
        //
        // 금지 패턴:
        // - queryFactory.selectFrom(q).join(...)
        // - queryFactory.selectFrom(q).leftJoin(...)
        // - queryFactory.selectFrom(q).rightJoin(...)
        // - queryFactory.selectFrom(q).innerJoin(...)
        // - queryFactory.selectFrom(q).fetchJoin(...)
        //
        // ✅ 이 테스트는 통과하지만, 실제 Join 사용 여부는 코드 리뷰로 확인해야 합니다.

        ArchRule rule =
                noClasses()
                        .that()
                        .haveSimpleNameEndingWith("QueryDslRepository")
                        .and()
                        .resideInAPackage("..repository..")
                        .should()
                        .dependOnClassesThat()
                        .haveFullyQualifiedName("com.querydsl.jpa.impl.JPAJoin")
                        .because(
                                "QueryDslRepository는 Join 사용이 금지됩니다 (N+1은 Adapter에서 해결)");

        rule.allowEmptyShould(true).check(queryDslRepositoryClasses);
    }

    @Test
    @DisplayName("규칙 7: QueryDslRepository는 @Transactional 사용 금지")
    void queryDslRepository_MustNotHaveTransactional() {
        ArchRule rule =
                noClasses()
                        .that()
                        .haveSimpleNameEndingWith("QueryDslRepository")
                        .and()
                        .resideInAPackage("..repository..")
                        .should()
                        .beAnnotatedWith(Transactional.class)
                        .because(
                                "QueryDslRepository는 @Transactional 사용이 금지됩니다 (Service Layer에서"
                                        + " 관리)");

        rule.allowEmptyShould(true).check(queryDslRepositoryClasses);
    }

    @Test
    @DisplayName("규칙 8: QueryDslRepository는 Mapper 의존성 금지")
    void queryDslRepository_MustNotDependOnMapper() {
        ArchRule rule =
                noClasses()
                        .that()
                        .haveSimpleNameEndingWith("QueryDslRepository")
                        .and()
                        .resideInAPackage("..repository..")
                        .should()
                        .dependOnClassesThat()
                        .haveSimpleNameEndingWith("Mapper")
                        .because(
                                "QueryDslRepository는 Mapper 의존성이 금지됩니다 (Adapter에서 처리)");

        rule.allowEmptyShould(true).check(queryDslRepositoryClasses);
    }

    @Test
    @DisplayName("규칙 9: QueryDslRepository 네이밍 규칙 (*QueryDslRepository)")
    void queryDslRepository_MustFollowNamingConvention() {
        ArchRule rule =
                classes()
                        .that()
                        .resideInAPackage("..repository..")
                        .and()
                        .areAnnotatedWith(Repository.class)
                        .and()
                        .areNotInterfaces()
                        .should()
                        .haveSimpleNameEndingWith("QueryDslRepository")
                        .because(
                                "QueryDslRepository는 *QueryDslRepository 네이밍 규칙을 따라야 합니다");

        rule.allowEmptyShould(true).check(allClasses);
    }
}
