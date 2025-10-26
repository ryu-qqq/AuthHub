package com.ryuqq.authhub.adapter.out.persistence.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.persistence.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

/**
 * Persistence Layer 아키텍처 규칙 검증 테스트.
 *
 * <p>ArchUnit을 사용하여 Persistence Layer의 아키텍처 규칙을 자동으로 검증합니다.
 * Zero-Tolerance 규칙 위반 시 빌드가 실패하여 품질을 보장합니다.</p>
 *
 * <p><strong>검증 규칙:</strong></p>
 * <ul>
 *   <li>Long FK 전략 - JPA 관계 어노테이션 금지</li>
 *   <li>Lombok 금지 - {@code @Data}, {@code @Builder} 등 금지</li>
 *   <li>패키지 네이밍 규칙 준수</li>
 *   <li>Repository 인터페이스 명명 규칙</li>
 *   <li>Adapter는 Component 또는 Repository 어노테이션 필수</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@DisplayName("Persistence Layer 아키텍처 규칙 검증")
class PersistenceLayerArchitectureTest {

    private static JavaClasses persistenceClasses;

    @BeforeAll
    static void setUp() {
        persistenceClasses = new ClassFileImporter()
                .importPackages("com.ryuqq.authhub.adapter.out.persistence.auth");
    }

    @Test
    @DisplayName("JPA Entity는 관계 어노테이션을 사용하지 않아야 한다 (Long FK 전략)")
    void jpaEntities_ShouldNotUse_RelationshipAnnotations() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..entity..")
                .and().areAnnotatedWith(Entity.class)
                .should().dependOnClassesThat().areAnnotatedWith(OneToMany.class)
                .orShould().dependOnClassesThat().areAnnotatedWith(ManyToOne.class)
                .orShould().dependOnClassesThat().areAnnotatedWith(OneToOne.class)
                .orShould().dependOnClassesThat().areAnnotatedWith(ManyToMany.class)
                .because("Persistence Layer에서는 Long FK 전략을 사용해야 합니다. " +
                        "JPA 관계 어노테이션(@OneToMany, @ManyToOne 등)은 절대 금지됩니다.");

        rule.check(persistenceClasses);
    }

    @Test
    @DisplayName("JPA Entity 클래스는 Lombok을 사용하지 않아야 한다")
    void jpaEntities_ShouldNotUse_Lombok() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..entity..")
                .and().areAnnotatedWith(Entity.class)
                .should().dependOnClassesThat().resideInAPackage("lombok..")
                .because("Zero-Tolerance 규칙: Lombok 사용 절대 금지. Plain Java getter/setter를 직접 작성해야 합니다.");

        rule.check(persistenceClasses);
    }

    @Test
    @DisplayName("Repository 인터페이스는 'Repository'로 끝나야 한다")
    void repositories_ShouldBe_NamedRepository() {
        ArchRule rule = classes()
                .that().resideInAPackage("..repository..")
                .and().areInterfaces()
                .should().haveSimpleNameEndingWith("Repository")
                .because("Repository 인터페이스는 일관된 네이밍 규칙을 따라야 합니다.");

        rule.check(persistenceClasses);
    }

    @Test
    @DisplayName("Repository 인터페이스는 @Repository 어노테이션을 가져야 한다")
    void repositories_ShouldBe_AnnotatedWithRepository() {
        ArchRule rule = classes()
                .that().resideInAPackage("..repository..")
                .and().areInterfaces()
                .should().beAnnotatedWith(Repository.class)
                .because("Spring Data JPA Repository는 @Repository 어노테이션이 필요합니다.");

        rule.check(persistenceClasses);
    }

    @Test
    @DisplayName("Adapter 클래스는 'Adapter'로 끝나야 한다")
    void adapters_ShouldBe_NamedAdapter() {
        ArchRule rule = classes()
                .that().resideInAPackage("..adapter")
                .and().areNotInterfaces()
                .and().areNotAnonymousClasses()
                .and().areTopLevelClasses()
                .and().haveSimpleNameNotEndingWith("Test")
                .should().haveSimpleNameEndingWith("Adapter")
                .because("Adapter 클래스는 일관된 네이밍 규칙을 따라야 합니다.");

        rule.check(persistenceClasses);
    }

    @Test
    @DisplayName("Adapter 클래스는 @Component 어노테이션을 가져야 한다")
    void adapters_ShouldBe_AnnotatedWithComponent() {
        ArchRule rule = classes()
                .that().resideInAPackage("..adapter")
                .and().areNotInterfaces()
                .and().areNotAnonymousClasses()
                .and().areTopLevelClasses()
                .and().haveSimpleNameEndingWith("Adapter")
                .and().haveSimpleNameNotEndingWith("Test")
                .should().beAnnotatedWith(Component.class)
                .because("Persistence Adapter는 Spring Bean으로 등록되어야 합니다.");

        rule.check(persistenceClasses);
    }

    @Test
    @DisplayName("Mapper 클래스는 'Mapper'로 끝나야 한다")
    void mappers_ShouldBe_NamedMapper() {
        ArchRule rule = classes()
                .that().resideInAPackage("..mapper")
                .and().areNotInterfaces()
                .and().areNotAnonymousClasses()
                .and().areTopLevelClasses()
                .should().haveSimpleNameEndingWith("Mapper")
                .because("Mapper 클래스는 일관된 네이밍 규칙을 따라야 합니다.");

        rule.check(persistenceClasses);
    }

    @Test
    @DisplayName("Mapper 클래스는 @Component 어노테이션을 가져야 한다")
    void mappers_ShouldBe_AnnotatedWithComponent() {
        ArchRule rule = classes()
                .that().resideInAPackage("..mapper")
                .and().areNotInterfaces()
                .and().areNotAnonymousClasses()
                .and().areTopLevelClasses()
                .and().haveSimpleNameEndingWith("Mapper")
                .should().beAnnotatedWith(Component.class)
                .because("Mapper는 Spring Bean으로 등록되어야 합니다.");

        rule.check(persistenceClasses);
    }

    @Test
    @DisplayName("Entity 클래스는 'Entity' 또는 'JpaEntity'로 끝나야 한다")
    void entities_ShouldBe_NamedEntity() {
        ArchRule rule = classes()
                .that().resideInAPackage("..entity..")
                .and().areAnnotatedWith(Entity.class)
                .should().haveSimpleNameEndingWith("Entity")
                .orShould().haveSimpleNameEndingWith("JpaEntity")
                .because("JPA Entity 클래스는 일관된 네이밍 규칙을 따라야 합니다.");

        rule.check(persistenceClasses);
    }

    @Test
    @DisplayName("Persistence Layer는 Application Layer의 Service에 의존하지 않아야 한다")
    void persistenceLayer_ShouldNotDependOn_ApplicationLayerServices() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("com.ryuqq.authhub.adapter.out.persistence..")
                .should().dependOnClassesThat().resideInAPackage("com.ryuqq.authhub.application..service..")
                .orShould().dependOnClassesThat().resideInAPackage("com.ryuqq.authhub.application..usecase..")
                .because("Persistence Layer는 Application Layer의 Port 인터페이스는 구현할 수 있지만, " +
                        "Application Layer의 Service나 UseCase에 직접 의존해서는 안 됩니다. " +
                        "Port 인터페이스(LoadUserPort, SaveUserPort 등)에 대한 의존성은 허용됩니다.");

        rule.check(persistenceClasses);
    }

    @Test
    @DisplayName("Adapter 클래스는 Adapter 패키지에만 위치해야 한다")
    void adapters_ShouldResideIn_AdapterPackage() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Adapter")
                .should().resideInAPackage("..adapter")
                .because("Adapter 클래스는 adapter 패키지에만 위치해야 합니다.");

        rule.check(persistenceClasses);
    }
}
