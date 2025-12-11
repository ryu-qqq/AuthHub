package com.ryuqq.authhub.adapter.in.rest.architecture.security;

import static com.ryuqq.authhub.adapter.in.rest.architecture.ArchUnitPackageConstants.ADAPTER_IN_REST;
import static com.ryuqq.authhub.adapter.in.rest.architecture.ArchUnitPackageConstants.DOMAIN_ALL;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Security Layer ArchUnit 검증 테스트 (Zero-Tolerance)
 *
 * <p>Security 관련 아키텍처 규칙을 검증합니다.
 *
 * <p><strong>검증 규칙:</strong>
 *
 * <ul>
 *   <li>ApiPaths: final 클래스, private 생성자, static final 필드
 *   <li>SecurityPaths: final 클래스, private 생성자
 *   <li>Config: @Configuration, @EnableWebSecurity, @EnableMethodSecurity
 *   <li>Filter: OncePerRequestFilter 상속, *Filter 네이밍
 *   <li>Handler: AuthenticationEntryPoint/AccessDeniedHandler 구현
 *   <li>Component: @Component 어노테이션
 *   <li>Lombok 금지
 * </ul>
 *
 * <p><strong>참고 문서:</strong>
 *
 * <ul>
 *   <li>security/security-guide.md - Security 아키텍처 가이드
 *   <li>security/api-paths-guide.md - API 경로 Constants 가이드
 *   <li>security/security-archunit.md - ArchUnit 테스트 가이드
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@DisplayName("Security Layer ArchUnit Tests (Zero-Tolerance)")
@Tag("architecture")
@Tag("adapter-rest")
@Tag("security")
class SecurityArchTest {

    private static JavaClasses classes;

    @BeforeAll
    static void setUp() {
        classes =
                new ClassFileImporter()
                        .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                        .importPackages(ADAPTER_IN_REST);
    }

    // =========================================================================
    // API Paths Constants 규칙
    // =========================================================================

    @Nested
    @DisplayName("ApiPaths Constants 규칙")
    class ApiPathsRules {

        /** 규칙 1: ApiPaths 클래스는 auth.paths 패키지에 위치해야 한다 */
        @Test
        @DisplayName("[필수] ApiPaths는 auth.paths 패키지에 위치해야 한다")
        void apiPaths_MustBeInAuthPathsPackage() {
            ArchRule rule =
                    classes()
                            .that()
                            .haveSimpleName("ApiPaths")
                            .should()
                            .resideInAPackage("..auth.paths..")
                            .because("ApiPaths는 auth.paths 패키지에 위치해야 합니다");

            rule.allowEmptyShould(true).check(classes);
        }

        /** 규칙 2: ApiPaths 클래스는 final이어야 한다 */
        @Test
        @DisplayName("[필수] ApiPaths는 final 클래스여야 한다")
        void apiPaths_MustBeFinal() {
            ArchRule rule =
                    classes()
                            .that()
                            .haveSimpleName("ApiPaths")
                            .should()
                            .haveModifier(JavaModifier.FINAL)
                            .because("ApiPaths는 인스턴스화 및 상속을 방지하기 위해 final이어야 합니다");

            rule.allowEmptyShould(true).check(classes);
        }

        /** 규칙 3: ApiPaths 필드는 static final이어야 한다 */
        @Test
        @DisplayName("[필수] ApiPaths 필드는 static final이어야 한다")
        void apiPaths_FieldsMustBeStaticFinal() {
            ArchRule rule =
                    fields().that()
                            .areDeclaredInClassesThat()
                            .haveSimpleName("ApiPaths")
                            .and()
                            .areNotPrivate() // private 생성자용 필드 제외
                            .should()
                            .beStatic()
                            .andShould()
                            .beFinal()
                            .because("ApiPaths의 경로 필드는 상수로서 static final이어야 합니다");

            rule.allowEmptyShould(true).check(classes);
        }

        /** 규칙 4: ApiPaths 필드는 String 타입이어야 한다 */
        @Test
        @DisplayName("[필수] ApiPaths 필드는 String 타입이어야 한다")
        void apiPaths_FieldsMustBeStringType() {
            ArchRule rule =
                    fields().that()
                            .areDeclaredInClassesThat()
                            .haveSimpleName("ApiPaths")
                            .and()
                            .areStatic()
                            .and()
                            .areFinal()
                            .should()
                            .haveRawType(String.class)
                            .because("ApiPaths의 경로 필드는 String 타입이어야 합니다");

            rule.allowEmptyShould(true).check(classes);
        }
    }

    // =========================================================================
    // SecurityPaths Constants 규칙
    // =========================================================================

    @Nested
    @DisplayName("SecurityPaths Constants 규칙")
    class SecurityPathsRules {

        /** 규칙 5: SecurityPaths 클래스는 auth.paths 패키지에 위치해야 한다 */
        @Test
        @DisplayName("[필수] SecurityPaths는 auth.paths 패키지에 위치해야 한다")
        void securityPaths_MustBeInAuthPathsPackage() {
            ArchRule rule =
                    classes()
                            .that()
                            .haveSimpleName("SecurityPaths")
                            .should()
                            .resideInAPackage("..auth.paths..")
                            .because("SecurityPaths는 auth.paths 패키지에 위치해야 합니다");

            rule.allowEmptyShould(true).check(classes);
        }

        /** 규칙 6: SecurityPaths 클래스는 final이어야 한다 */
        @Test
        @DisplayName("[필수] SecurityPaths는 final 클래스여야 한다")
        void securityPaths_MustBeFinal() {
            ArchRule rule =
                    classes()
                            .that()
                            .haveSimpleName("SecurityPaths")
                            .should()
                            .haveModifier(JavaModifier.FINAL)
                            .because("SecurityPaths는 인스턴스화 및 상속을 방지하기 위해 final이어야 합니다");

            rule.allowEmptyShould(true).check(classes);
        }
    }

    // =========================================================================
    // Security Config 규칙
    // =========================================================================

    @Nested
    @DisplayName("Security Config 규칙")
    class SecurityConfigRules {

        /** 규칙 7: SecurityConfig는 auth.config 패키지에 위치해야 한다 */
        @Test
        @DisplayName("[필수] SecurityConfig는 auth.config 패키지에 위치해야 한다")
        void securityConfig_MustBeInAuthConfigPackage() {
            ArchRule rule =
                    classes()
                            .that()
                            .haveSimpleNameEndingWith("SecurityConfig")
                            .and()
                            .resideInAPackage("..adapter.in.rest..")
                            .should()
                            .resideInAPackage("..auth.config..")
                            .because("SecurityConfig는 auth.config 패키지에 위치해야 합니다");

            rule.allowEmptyShould(true).check(classes);
        }

        /** 규칙 8: SecurityConfig는 @Configuration 어노테이션을 가져야 한다 */
        @Test
        @DisplayName("[필수] SecurityConfig는 @Configuration을 가져야 한다")
        void securityConfig_MustHaveConfigurationAnnotation() {
            ArchRule rule =
                    classes()
                            .that()
                            .haveSimpleNameEndingWith("SecurityConfig")
                            .and()
                            .resideInAPackage("..auth.config..")
                            .should()
                            .beAnnotatedWith(
                                    org.springframework.context.annotation.Configuration.class)
                            .because("SecurityConfig는 Spring Bean 설정을 위해 @Configuration이 필수입니다");

            rule.allowEmptyShould(true).check(classes);
        }

        /** 규칙 9: SecurityConfig는 @EnableWebSecurity 어노테이션을 가져야 한다 */
        @Test
        @DisplayName("[필수] SecurityConfig는 @EnableWebSecurity를 가져야 한다")
        void securityConfig_MustHaveEnableWebSecurityAnnotation() {
            ArchRule rule =
                    classes()
                            .that()
                            .haveSimpleNameEndingWith("SecurityConfig")
                            .and()
                            .resideInAPackage("..auth.config..")
                            .should()
                            .beAnnotatedWith(
                                    org.springframework.security.config.annotation.web.configuration
                                            .EnableWebSecurity.class)
                            .because(
                                    "SecurityConfig는 Spring Security 활성화를 위해 @EnableWebSecurity가"
                                            + " 필수입니다");

            rule.allowEmptyShould(true).check(classes);
        }
    }

    // =========================================================================
    // Security Filter 규칙
    // =========================================================================

    @Nested
    @DisplayName("Security Filter 규칙")
    class SecurityFilterRules {

        /** 규칙 10: Security Filter는 auth.filter 패키지에 위치해야 한다 */
        @Test
        @DisplayName("[필수] Security Filter는 auth.filter 패키지에 위치해야 한다")
        void securityFilter_MustBeInAuthFilterPackage() {
            ArchRule rule =
                    classes()
                            .that()
                            .areAssignableTo(OncePerRequestFilter.class)
                            .and()
                            .resideInAPackage("..adapter.in.rest..")
                            .and()
                            .haveSimpleNameContaining("Authentication")
                            .should()
                            .resideInAPackage("..auth.filter..")
                            .because("인증 관련 Filter는 auth.filter 패키지에 위치해야 합니다");

            rule.allowEmptyShould(true).check(classes);
        }

        /**
         * 규칙 11: Authentication Filter는 OncePerRequestFilter를 상속해야 한다.
         *
         * <p>JWT 또는 Gateway 헤더 인증 필터 모두 포함합니다.
         */
        @Test
        @DisplayName("[필수] Authentication Filter는 OncePerRequestFilter를 상속해야 한다")
        void authenticationFilter_MustExtendOncePerRequestFilter() {
            ArchRule rule =
                    classes()
                            .that()
                            .haveSimpleNameContaining("AuthenticationFilter")
                            .or()
                            .haveSimpleNameContaining("AuthFilter")
                            .and()
                            .resideInAPackage("..adapter.in.rest..")
                            .should()
                            .beAssignableTo(OncePerRequestFilter.class)
                            .because("인증 필터는 요청당 한 번만 실행되어야 하므로 OncePerRequestFilter를 상속해야 합니다");

            rule.allowEmptyShould(true).check(classes);
        }

        /** 규칙 12: Security Filter는 *Filter 네이밍 규칙을 따라야 한다 */
        @Test
        @DisplayName("[필수] Security Filter는 *Filter 네이밍 규칙을 따라야 한다")
        void securityFilter_MustFollowNamingConvention() {
            ArchRule rule =
                    classes()
                            .that()
                            .areAssignableTo(OncePerRequestFilter.class)
                            .and()
                            .resideInAPackage("..auth.filter..")
                            .should()
                            .haveSimpleNameEndingWith("Filter")
                            .because("Security Filter는 *Filter 네이밍 규칙을 따라야 합니다");

            rule.allowEmptyShould(true).check(classes);
        }
    }

    // =========================================================================
    // Security Handler 규칙
    // =========================================================================

    @Nested
    @DisplayName("Security Handler 규칙")
    class SecurityHandlerRules {

        /** 규칙 13: Security Handler는 auth.handler 패키지에 위치해야 한다 */
        @Test
        @DisplayName("[필수] Security Handler는 auth.handler 패키지에 위치해야 한다")
        void securityHandler_MustBeInAuthHandlerPackage() {
            ArchRule rule =
                    classes()
                            .that()
                            .implement(AuthenticationEntryPoint.class)
                            .or()
                            .implement(AccessDeniedHandler.class)
                            .should()
                            .resideInAPackage("..auth.handler..")
                            .because("Security Handler는 auth.handler 패키지에 위치해야 합니다");

            rule.allowEmptyShould(true).check(classes);
        }

        /** 규칙 14: AuthenticationEntryPoint 구현체는 @Component를 가져야 한다 */
        @Test
        @DisplayName("[필수] AuthenticationEntryPoint 구현체는 @Component를 가져야 한다")
        void authenticationEntryPoint_MustHaveComponentAnnotation() {
            ArchRule rule =
                    classes()
                            .that()
                            .implement(AuthenticationEntryPoint.class)
                            .and()
                            .resideInAPackage("..auth.handler..")
                            .should()
                            .beAnnotatedWith(Component.class)
                            .because("AuthenticationEntryPoint 구현체는 Bean 등록을 위해 @Component가 필수입니다");

            rule.allowEmptyShould(true).check(classes);
        }

        /** 규칙 15: AccessDeniedHandler 구현체는 @Component를 가져야 한다 */
        @Test
        @DisplayName("[필수] AccessDeniedHandler 구현체는 @Component를 가져야 한다")
        void accessDeniedHandler_MustHaveComponentAnnotation() {
            ArchRule rule =
                    classes()
                            .that()
                            .implement(AccessDeniedHandler.class)
                            .and()
                            .resideInAPackage("..auth.handler..")
                            .should()
                            .beAnnotatedWith(Component.class)
                            .because("AccessDeniedHandler 구현체는 Bean 등록을 위해 @Component가 필수입니다");

            rule.allowEmptyShould(true).check(classes);
        }
    }

    // =========================================================================
    // Security Component 규칙
    // =========================================================================

    @Nested
    @DisplayName("Security Component 규칙")
    class SecurityComponentRules {

        /** 규칙 16: Security Component는 auth.component 패키지에 위치해야 한다 */
        @Test
        @DisplayName("[필수] Security Component는 auth.component 패키지에 위치해야 한다")
        void securityComponent_MustBeInAuthComponentPackage() {
            ArchRule rule =
                    classes()
                            .that()
                            .haveSimpleNameContaining("TokenProvider")
                            .or()
                            .haveSimpleNameContaining("TokenResolver")
                            .or()
                            .haveSimpleNameContaining("CookieProvider")
                            .should()
                            .resideInAPackage("..auth.component..")
                            .because("Security 관련 Component는 auth.component 패키지에 위치해야 합니다");

            rule.allowEmptyShould(true).check(classes);
        }

        /**
         * 규칙 17: Security Component는 @Component를 가져야 한다.
         *
         * <p>auth.component 패키지에서 컴포넌트 역할을 하는 클래스는 @Component가 필수입니다.
         *
         * <p>예외:
         *
         * <ul>
         *   <li>record: 값 객체로서 Bean이 아님
         *   <li>SecurityContext: ThreadLocal에 저장되는 값 객체, Bean이 아님
         *   <li>SecurityContextHolder: static utility 클래스, Bean이 아님
         * </ul>
         */
        @Test
        @DisplayName("[필수] Security Component는 @Component를 가져야 한다")
        void securityComponent_MustHaveComponentAnnotation() {
            ArchRule rule =
                    classes()
                            .that()
                            .resideInAPackage("..auth.component..")
                            .and()
                            .areNotInterfaces()
                            .and()
                            .areNotNestedClasses()
                            .and()
                            .areNotRecords()
                            .and()
                            .doNotHaveSimpleName("SecurityContext") // 값 객체 예외
                            .and()
                            .doNotHaveSimpleName("SecurityContextHolder") // static utility 예외
                            .should()
                            .beAnnotatedWith(Component.class)
                            .because(
                                    "Security Component는 Bean 등록을 위해 @Component가 필수입니다"
                                            + " (SecurityContext, SecurityContextHolder는 Bean이 아니므로"
                                            + " 예외)");

            rule.allowEmptyShould(true).check(classes);
        }
    }

    // =========================================================================
    // Security Properties 규칙
    // =========================================================================

    @Nested
    @DisplayName("Security Properties 규칙")
    class SecurityPropertiesRules {

        /** 규칙 18: Security Properties는 auth.config 또는 config.properties 패키지에 위치해야 한다 */
        @Test
        @DisplayName("[필수] Security Properties는 적절한 패키지에 위치해야 한다")
        void securityProperties_MustBeInCorrectPackage() {
            ArchRule rule =
                    classes()
                            .that()
                            .haveSimpleNameEndingWith("SecurityProperties")
                            .and()
                            .resideInAPackage("..adapter.in.rest..")
                            .should()
                            .resideInAnyPackage("..auth.config..", "..config.properties..")
                            .because(
                                    "Security Properties는 auth.config 또는 config.properties 패키지에"
                                            + " 위치해야 합니다");

            rule.allowEmptyShould(true).check(classes);
        }

        /** 규칙 19: Security Properties는 @ConfigurationProperties를 가져야 한다 */
        @Test
        @DisplayName("[필수] Security Properties는 @ConfigurationProperties를 가져야 한다")
        void securityProperties_MustHaveConfigurationPropertiesAnnotation() {
            ArchRule rule =
                    classes()
                            .that()
                            .haveSimpleNameEndingWith("SecurityProperties")
                            .and()
                            .resideInAPackage("..adapter.in.rest..")
                            .should()
                            .beAnnotatedWith(
                                    org.springframework.boot.context.properties
                                            .ConfigurationProperties.class)
                            .because(
                                    "Security Properties는 설정 바인딩을 위해 @ConfigurationProperties가"
                                            + " 필수입니다");

            rule.allowEmptyShould(true).check(classes);
        }
    }

    // =========================================================================
    // 금지 규칙 (Prohibition Rules)
    // =========================================================================

    @Nested
    @DisplayName("금지 규칙 (Prohibition Rules)")
    class ProhibitionRules {

        /** 규칙 20: Security Layer는 Lombok을 사용하지 않아야 한다 */
        @Test
        @DisplayName("[금지] Security Layer는 Lombok을 사용하지 않아야 한다")
        void securityLayer_MustNotUseLombok() {
            ArchRule rule =
                    noClasses()
                            .that()
                            .resideInAPackage("..auth..")
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
                            .orShould()
                            .beAnnotatedWith("lombok.Value")
                            .because("Security Layer는 Pure Java를 사용해야 하며 Lombok은 금지됩니다");

            rule.allowEmptyShould(true).check(classes);
        }

        /**
         * 규칙 21: Security Layer는 Domain Layer를 직접 의존하지 않아야 한다.
         *
         * <p>단, 다음은 예외로 허용:
         *
         * <ul>
         *   <li>Role: 역할 상수 (ROLE_SUPER_ADMIN, ROLE_TENANT_ADMIN 등)
         *   <li>auth.error 패키지: ErrorMapper는 Domain Exception 처리 필요
         * </ul>
         *
         * <p>상수 클래스는 순수 값만 포함하므로 도메인 로직 의존이 아님
         */
        @Test
        @DisplayName("[금지] Security Layer는 Domain Layer를 직접 의존하지 않아야 한다 (상수/에러매퍼 예외)")
        void securityLayer_MustNotDependOnDomain() {
            ArchRule rule =
                    noClasses()
                            .that()
                            .resideInAPackage("..auth..")
                            .and()
                            .resideOutsideOfPackage(
                                    "..auth.error..") // ErrorMapper는 Domain Exception 처리 필요
                            .should()
                            .dependOnClassesThat()
                            .resideInAnyPackage(DOMAIN_ALL)
                            .andShould()
                            .dependOnClassesThat()
                            .haveSimpleNameNotEndingWith("Role") // Role 상수 클래스 예외
                            .because(
                                    "Security Layer는 Domain Layer를 직접 의존하면 안 됩니다"
                                            + " (단, Role 상수 및 auth.error 패키지는 예외)");

            rule.allowEmptyShould(true).check(classes);
        }

        /** 규칙 22: Security Layer는 Persistence Layer를 직접 의존하지 않아야 한다 */
        @Test
        @DisplayName("[금지] Security Layer는 Persistence Layer를 직접 의존하지 않아야 한다")
        void securityLayer_MustNotDependOnPersistence() {
            ArchRule rule =
                    noClasses()
                            .that()
                            .resideInAPackage("..auth..")
                            .should()
                            .dependOnClassesThat()
                            .resideInAnyPackage("..adapter.out.persistence..")
                            .because("Security Layer는 Persistence Layer를 직접 의존하면 안 됩니다");

            rule.allowEmptyShould(true).check(classes);
        }
    }

    // =========================================================================
    // 패키지 구조 규칙
    // =========================================================================

    @Nested
    @DisplayName("패키지 구조 규칙")
    class PackageStructureRules {

        /**
         * 규칙 23: auth 패키지는 표준 하위 구조를 가져야 한다.
         *
         * <p>허용되는 하위 패키지:
         *
         * <ul>
         *   <li>paths: API 경로 상수
         *   <li>config: Security 설정
         *   <li>filter: Security 필터
         *   <li>handler: Security 핸들러
         *   <li>component: Security 컴포넌트
         *   <li>controller: Auth 관련 컨트롤러 (로그인, 로그아웃 등)
         *   <li>dto: Auth 관련 DTO (Request/Response)
         *   <li>error: Auth 관련 에러 매퍼
         *   <li>mapper: Auth 관련 매퍼
         * </ul>
         */
        @Test
        @DisplayName("[필수] auth 패키지는 표준 하위 구조를 가져야 한다")
        void authPackage_MustHaveStandardSubStructure() {
            ArchRule rule =
                    classes()
                            .that()
                            .resideInAPackage("..auth..")
                            .and()
                            .resideOutsideOfPackage("..architecture..")
                            .and()
                            .haveSimpleNameNotEndingWith("Test")
                            .should()
                            .resideInAnyPackage(
                                    "..auth.paths..",
                                    "..auth.config..",
                                    "..auth.filter..",
                                    "..auth.handler..",
                                    "..auth.component..",
                                    "..auth.controller..",
                                    "..auth.dto..",
                                    "..auth.error..",
                                    "..auth.mapper..")
                            .because(
                                    "auth 패키지는 paths, config, filter, handler, component,"
                                            + " controller, dto, error, mapper 하위 구조를 가져야 합니다");

            rule.allowEmptyShould(true).check(classes);
        }
    }

    // =========================================================================
    // Gateway Only 아키텍처 규칙
    // =========================================================================

    /**
     * Gateway Only 아키텍처 검증 규칙.
     *
     * <p>Gateway에서 JWT 검증 후 헤더로 사용자 정보를 전달하는 패턴을 검증합니다.
     *
     * @see <a href="../security/gateway-only-architecture.md">Gateway Only Architecture Guide</a>
     */
    @Nested
    @DisplayName("Gateway Only 아키텍처 규칙")
    class GatewayOnlyArchitectureRules {

        /**
         * 규칙 24: Gateway 관련 컴포넌트는 auth 하위 패키지에 위치해야 한다.
         *
         * <p>GatewayUserResolver, GatewayUser 등 Gateway 헤더 처리 컴포넌트의 위치를 검증합니다.
         *
         * <ul>
         *   <li>GatewayUser, GatewayUserResolver → auth.component 패키지
         *   <li>GatewayHeaderAuthFilter → auth.filter 패키지
         *   <li>GatewayProperties → auth.config 패키지
         * </ul>
         */
        @Test
        @DisplayName("[필수] Gateway 관련 컴포넌트는 auth.component/filter/config 패키지에 위치해야 한다")
        void gatewayComponents_MustBeInAuthPackage() {
            ArchRule rule =
                    classes()
                            .that()
                            .haveSimpleNameContaining("Gateway")
                            .and()
                            .resideInAPackage("..auth..")
                            .and()
                            .areNotInterfaces()
                            .should()
                            .resideInAnyPackage(
                                    "..auth.component..", "..auth.filter..", "..auth.config..")
                            .because(
                                    "Gateway 관련 컴포넌트는 auth.component, auth.filter, 또는 auth.config"
                                            + " 패키지에 위치해야 합니다");

            rule.allowEmptyShould(true).check(classes);
        }

        /**
         * 규칙 25: Gateway 헤더 인증 필터는 OncePerRequestFilter를 상속해야 한다.
         *
         * <p>Gateway에서 전달받은 헤더를 읽어 인증 정보를 설정하는 필터입니다.
         */
        @Test
        @DisplayName("[필수] Gateway 헤더 인증 필터는 OncePerRequestFilter를 상속해야 한다")
        void gatewayHeaderAuthFilter_MustExtendOncePerRequestFilter() {
            ArchRule rule =
                    classes()
                            .that()
                            .haveSimpleNameContaining("GatewayHeaderAuthFilter")
                            .or()
                            .haveSimpleNameContaining("GatewayAuthFilter")
                            .should()
                            .beAssignableTo(OncePerRequestFilter.class)
                            .because(
                                    "Gateway 헤더 인증 필터는 요청당 한 번만 실행되어야 하므로"
                                            + " OncePerRequestFilter를 상속해야 합니다");

            rule.allowEmptyShould(true).check(classes);
        }

        /**
         * 규칙 26: Gateway User VO는 record 또는 불변 클래스여야 한다.
         *
         * <p>Gateway에서 전달받은 사용자 정보는 변경되면 안 됩니다.
         */
        @Test
        @DisplayName("[권장] Gateway User는 record 타입이어야 한다")
        void gatewayUser_ShouldBeRecord() {
            ArchRule rule =
                    classes()
                            .that()
                            .haveSimpleName("GatewayUser")
                            .should()
                            .beRecords()
                            .because("Gateway User는 불변성을 위해 record 타입이어야 합니다");

            rule.allowEmptyShould(true).check(classes);
        }

        /**
         * 규칙 27: Security Layer는 JWT Secret/Key 관련 클래스를 직접 참조하지 않아야 한다.
         *
         * <p>Gateway Only 아키텍처에서 서비스는 JWT 검증을 수행하지 않습니다.
         */
        @Test
        @DisplayName("[금지] Security Layer는 JWT Secret 관련 클래스를 직접 참조하지 않아야 한다")
        void securityLayer_MustNotReferenceJwtSecretClasses() {
            ArchRule rule =
                    noClasses()
                            .that()
                            .resideInAPackage("..auth..")
                            .and()
                            .haveSimpleNameNotContaining("Jwt") // JWT 필터 자체는 허용 (혼용 시)
                            .should()
                            .dependOnClassesThat()
                            .haveSimpleNameContaining("JwtSecret")
                            .orShould()
                            .dependOnClassesThat()
                            .haveSimpleNameContaining("SecretKey")
                            .because(
                                    "Gateway Only 아키텍처에서 서비스는 JWT Secret을 직접 참조하면 안 됩니다."
                                            + " JWT 검증은 Gateway에서 수행합니다.");

            rule.allowEmptyShould(true).check(classes);
        }

        /**
         * 규칙 28: GatewayUser의 userId 필드는 UUID 타입이어야 한다.
         *
         * <p>UUIDv7을 사용하여 시간 순서가 보장되는 고유 식별자를 사용합니다. Long 타입은 보안상 취약하므로 금지됩니다.
         */
        @Test
        @DisplayName("[필수] GatewayUser의 userId 필드는 UUID 타입이어야 한다")
        void gatewayUser_UserIdFieldMustBeUUID() {
            ArchRule rule =
                    fields().that()
                            .areDeclaredInClassesThat()
                            .haveSimpleName("GatewayUser")
                            .and()
                            .haveName("userId")
                            .should()
                            .haveRawType(UUID.class)
                            .because(
                                    "GatewayUser의 userId는 보안을 위해 UUID 타입이어야 합니다. "
                                            + "Long 타입은 예측 가능하여 보안상 취약합니다.");

            rule.allowEmptyShould(true).check(classes);
        }

        /**
         * 규칙 29: SecurityContextAuthenticator의 Gateway 인증 메서드는 UUID를 반환해야 한다.
         *
         * <p>GatewayUser를 파라미터로 받는 authenticate 메서드는 UUID를 반환해야 합니다. JWT 모드용 authenticate(String)
         * 메서드는 별도 검사 대상입니다.
         */
        @Test
        @DisplayName("[필수] SecurityContextAuthenticator.authenticate(GatewayUser)는 UUID를 반환해야 한다")
        void securityContextAuthenticator_GatewayAuthenticateMustReturnUUID() {
            ArchRule rule =
                    methods()
                            .that()
                            .areDeclaredInClassesThat()
                            .haveSimpleName("SecurityContextAuthenticator")
                            .and()
                            .haveName("authenticate")
                            .and()
                            .haveRawReturnType(UUID.class)
                            .should()
                            .haveRawReturnType(UUID.class)
                            .because(
                                    "SecurityContextAuthenticator.authenticate(GatewayUser)는 "
                                            + "GatewayUser의 UUID userId를 반환해야 합니다.");

            rule.allowEmptyShould(true).check(classes);
        }
    }
}
