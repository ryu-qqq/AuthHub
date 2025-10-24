package com.ryuqq.authhub.bootstrap.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

/**
 * Bootstrap Layer 아키텍처 검증 테스트.
 *
 * <p>Bootstrap 모듈은 통합 Redis persistence 코드를 포함하며,
 * Hexagonal Architecture의 Adapter-Out 역할을 수행합니다.</p>
 *
 * <p><strong>검증 항목:</strong></p>
 * <ul>
 *   <li>✅ Lombok 금지</li>
 *   <li>✅ JPA 관계 어노테이션 금지</li>
 *   <li>✅ Layer 의존성 규칙 준수</li>
 *   <li>✅ Package 구조 규칙</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
@DisplayName("Bootstrap Layer 아키텍처 검증")
class BootstrapLayerArchitectureTest {

    private static JavaClasses importedClasses;

    @BeforeAll
    static void setup() {
        importedClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("com.ryuqq.authhub.bootstrap");
    }

    @Nested
    @DisplayName("1. Zero-Tolerance 규칙 검증")
    class ZeroToleranceRules {

        @Test
        @DisplayName("[Zero-Tolerance] Lombok 사용 금지")
        void shouldNotUseLombok() {
            noClasses()
                    .should().dependOnClassesThat().resideInAnyPackage("lombok..")
                    .because("Lombok is STRICTLY PROHIBITED. Use pure Java.")
                    .check(importedClasses);
        }

        @Test
        @DisplayName("[Zero-Tolerance] JPA 관계 어노테이션 금지 (@OneToMany, @ManyToOne, etc)")
        void shouldNotUseJpaRelationshipAnnotations() {
            noClasses()
                    .should().beAnnotatedWith("jakarta.persistence.OneToMany")
                    .orShould().beAnnotatedWith("jakarta.persistence.ManyToOne")
                    .orShould().beAnnotatedWith("jakarta.persistence.OneToOne")
                    .orShould().beAnnotatedWith("jakarta.persistence.ManyToMany")
                    .because("JPA relationship annotations are STRICTLY PROHIBITED. Use Long FK strategy.")
                    .check(importedClasses);
        }
    }

    @Nested
    @DisplayName("2. Layer 의존성 규칙")
    class LayerDependencyRules {

        @Test
        @DisplayName("[의존성] Bootstrap Persistence는 Adapter Layer에 의존하면 안 됨")
        void bootstrapPersistenceShouldNotDependOnAdapters() {
            noClasses()
                    .that().resideInAPackage("com.ryuqq.authhub.bootstrap.persistence..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage(
                            "com.ryuqq.authhub.adapter.in..",
                            "com.ryuqq.authhub.adapter.out.."
                    )
                    .because("Bootstrap Persistence는 다른 Adapter Layer에 의존하면 안 됨")
                    .check(importedClasses);
        }
    }

    @Nested
    @DisplayName("3. Naming Convention 규칙")
    class NamingConventionRules {

        @Test
        @DisplayName("[네이밍] Repository는 interface이고 Repository 접미사 필수")
        void repositoriesShouldBeInterfaces() {
            classes()
                    .that().haveSimpleNameEndingWith("Repository")
                    .and().resideInAPackage("..repository..")
                    .should().beInterfaces()
                    .because("Repository는 Spring Data Repository interface여야 함")
                    .check(importedClasses);
        }

        @Test
        @DisplayName("[네이밍] Entity는 Entity 접미사 필수")
        void entitiesShouldHaveEntitySuffix() {
            classes()
                    .that().resideInAPackage("..entity..")
                    .and().areNotMemberClasses()
                    .should().haveSimpleNameEndingWith("Entity")
                    .because("Redis Entity는 'Entity' 접미사를 가져야 함")
                    .check(importedClasses);
        }

        @Test
        @DisplayName("[네이밍] Mapper는 Mapper 접미사 필수")
        void mappersShouldHaveMapperSuffix() {
            classes()
                    .that().resideInAPackage("..mapper..")
                    .should().haveSimpleNameEndingWith("Mapper")
                    .because("Mapper는 'Mapper' 접미사를 가져야 함")
                    .check(importedClasses);
        }

        @Test
        @DisplayName("[네이밍] Adapter는 Adapter 접미사 필수")
        void adaptersShouldHaveAdapterSuffix() {
            classes()
                    .that().resideInAPackage("..adapter..")
                    .and().areNotMemberClasses()
                    .should().haveSimpleNameEndingWith("Adapter")
                    .because("Adapter는 'Adapter' 접미사를 가져야 함")
                    .check(importedClasses);
        }
    }

    @Nested
    @DisplayName("4. Package 구조 규칙")
    class PackageStructureRules {

        @Test
        @DisplayName("[패키지] Repository는 repository 패키지에 위치")
        void repositoriesShouldResideInRepositoryPackage() {
            classes()
                    .that().haveSimpleNameEndingWith("Repository")
                    .should().resideInAPackage("..repository..")
                    .because("Repository는 repository 패키지에 위치해야 함")
                    .check(importedClasses);
        }

        @Test
        @DisplayName("[패키지] Entity는 entity 패키지에 위치")
        void entitiesShouldResideInEntityPackage() {
            classes()
                    .that().haveSimpleNameEndingWith("Entity")
                    .and().resideInAPackage("com.ryuqq.authhub.bootstrap..")
                    .should().resideInAPackage("..entity..")
                    .because("Entity는 entity 패키지에 위치해야 함")
                    .check(importedClasses);
        }

        @Test
        @DisplayName("[패키지] Mapper는 mapper 패키지에 위치")
        void mappersShouldResideInMapperPackage() {
            classes()
                    .that().haveSimpleNameEndingWith("Mapper")
                    .should().resideInAPackage("..mapper..")
                    .because("Mapper는 mapper 패키지에 위치해야 함")
                    .check(importedClasses);
        }

        @Test
        @DisplayName("[패키지] Adapter는 adapter 패키지에 위치")
        void adaptersShouldResideInAdapterPackage() {
            classes()
                    .that().haveSimpleNameEndingWith("Adapter")
                    .and().resideInAPackage("com.ryuqq.authhub.bootstrap..")
                    .should().resideInAPackage("..adapter..")
                    .because("Adapter는 adapter 패키지에 위치해야 함")
                    .check(importedClasses);
        }
    }

    @Nested
    @DisplayName("5. Spring Annotation 규칙")
    class SpringAnnotationRules {

        @Test
        @DisplayName("[어노테이션] Repository는 Spring Data Repository interface")
        void repositoriesShouldExtendRepository() {
            classes()
                    .that().haveSimpleNameEndingWith("Repository")
                    .and().resideInAPackage("..repository..")
                    .should().beInterfaces()
                    .andShould().beAssignableTo("org.springframework.data.repository.Repository")
                    .because("Repository는 Spring Data Repository를 상속해야 함")
                    .check(importedClasses);
        }

        @Test
        @DisplayName("[어노테이션] Mapper와 Adapter는 @Component 필수")
        void mappersAndAdaptersShouldBeAnnotatedWithComponent() {
            classes()
                    .that().haveSimpleNameEndingWith("Mapper")
                    .or().haveSimpleNameEndingWith("Adapter")
                    .and().resideInAPackage("com.ryuqq.authhub.bootstrap.persistence..")
                    .and().areNotInterfaces()
                    .should().beAnnotatedWith("org.springframework.stereotype.Component")
                    .because("Mapper와 Adapter는 Spring Bean이어야 함")
                    .check(importedClasses);
        }
    }
}
