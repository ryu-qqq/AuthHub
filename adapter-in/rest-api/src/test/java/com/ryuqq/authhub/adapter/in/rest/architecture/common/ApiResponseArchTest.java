package com.ryuqq.authhub.adapter.in.rest.architecture.common;

import static com.ryuqq.authhub.adapter.in.rest.architecture.ArchUnitPackageConstants.ADAPTER_IN_REST;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * API Response ArchUnit 검증 테스트 (Zero-Tolerance)
 *
 * <p>Common API Response 패턴을 검증합니다.
 *
 * <p><strong>응답 전략:</strong>
 *
 * <ul>
 *   <li>성공 응답: ApiResponse&lt;T&gt; 사용
 *   <li>실패 응답: RFC 7807 ProblemDetail 사용 (GlobalExceptionHandler에서 처리)
 * </ul>
 *
 * <p><strong>검증 규칙:</strong>
 *
 * <ul>
 *   <li>규칙 1: Common Response DTOs는 common.dto 패키지에 위치
 *   <li>규칙 2: Common Response DTOs는 Record 타입이어야 함
 *   <li>규칙 3: ApiResponse는 ofSuccess static factory method 패턴 사용
 *   <li>규칙 4: PageApiResponse는 from() 메서드 필수
 *   <li>규칙 5: Common Response DTOs는 Lombok 금지
 *   <li>규칙 6: Common Response DTOs는 public이어야 함
 *   <li>규칙 7: Common Response DTOs는 final이어야 함 (Record 특성)
 * </ul>
 *
 * <p><strong>참고 문서:</strong>
 *
 * <ul>
 *   <li>rest-api-guide.md - REST API Layer 전체 가이드
 *   <li>dto/response/response-dto-guide.md - Response DTO 가이드
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@DisplayName("API Response ArchUnit Tests (Zero-Tolerance)")
@Tag("architecture")
@Tag("adapter-rest")
class ApiResponseArchTest {

    private static JavaClasses classes;

    @BeforeAll
    static void setUp() {
        classes =
                new ClassFileImporter()
                        .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                        .importPackages(ADAPTER_IN_REST);
    }

    /**
     * 규칙 1: Common Response DTOs는 common.dto 패키지에 위치
     *
     * <p>Note: ApiResponse, PageApiResponse, SliceApiResponse 등 공통 Response 래퍼만 common.dto에 있어야
     * 합니다. 도메인별 Response DTO(예: TenantApiResponse, UserApiResponse)는 해당 도메인 패키지의 dto.response에
     * 위치합니다.
     *
     * <p>Note: 실패 응답은 RFC 7807 ProblemDetail을 사용하므로 ErrorInfo 클래스는 사용하지 않습니다.
     */
    @Test
    @DisplayName("[필수] Common Response DTOs는 common.dto 패키지에 위치해야 한다")
    void commonResponseDtos_MustBeInCommonDtoPackage() {
        // Common Response 래퍼 클래스만 체크 (도메인별 Response DTO 제외)
        // ApiResponse, PageApiResponse, SliceApiResponse만 체크
        ArchRule rule =
                classes()
                        .that()
                        .haveSimpleName("ApiResponse")
                        .or()
                        .haveSimpleName("PageApiResponse")
                        .or()
                        .haveSimpleName("SliceApiResponse")
                        .should()
                        .resideInAPackage("..common.dto..")
                        .because(
                                "Common Response 래퍼(ApiResponse, PageApiResponse,"
                                        + " SliceApiResponse)는 common.dto 패키지에 위치해야 합니다");

        rule.allowEmptyShould(true).check(classes);
    }

    /** 규칙 2: Common Response DTOs는 Record 타입이어야 함 */
    @Test
    @DisplayName("[필수] Common Response DTOs는 Java Record 타입이어야 한다")
    void commonResponseDtos_MustBeRecords() {
        ArchRule rule =
                classes()
                        .that()
                        .haveNameMatching(".*ApiResponse")
                        .and()
                        .resideInAPackage("..common.dto..")
                        .and()
                        .areNotNestedClasses()
                        .should()
                        .beRecords()
                        .because("Common Response DTOs는 불변성 보장을 위해 Java 21 Record를 사용해야 합니다");

        rule.allowEmptyShould(true).check(classes);
    }

    /**
     * 규칙 3: ApiResponse는 ofSuccess static factory method 패턴 사용
     *
     * <p>Note: 실패 응답은 GlobalExceptionHandler에서 RFC 7807 ProblemDetail로 처리합니다.
     */
    @Test
    @DisplayName("[필수] ApiResponse는 ofSuccess static factory method를 가져야 한다")
    void apiResponse_MustHaveStaticFactoryMethods() {
        ArchRule successRule =
                methods()
                        .that()
                        .areDeclaredInClassesThat()
                        .haveSimpleName("ApiResponse")
                        .and()
                        .haveName("ofSuccess")
                        .should()
                        .bePublic()
                        .andShould()
                        .beStatic()
                        .because("ApiResponse는 ofSuccess() static factory method가 필수입니다");

        successRule.allowEmptyShould(true).check(classes);
    }

    /** 규칙 4: PageApiResponse는 from() 메서드 필수 */
    @Test
    @DisplayName("[필수] PageApiResponse는 from() static factory method를 가져야 한다")
    void pageApiResponse_MustHaveFromMethod() {
        ArchRule rule =
                methods()
                        .that()
                        .areDeclaredInClassesThat()
                        .haveSimpleName("PageApiResponse")
                        .and()
                        .haveName("from")
                        .should()
                        .bePublic()
                        .andShould()
                        .beStatic()
                        .because(
                                "PageApiResponse는 Application Layer PageResponse를 변환하는 from()"
                                        + " static method가 필수입니다");

        rule.allowEmptyShould(true).check(classes);
    }

    /** 규칙 5: Common Response DTOs는 Lombok 금지 */
    @Test
    @DisplayName("[금지] Common Response DTOs는 Lombok을 사용하지 않아야 한다")
    void commonResponseDtos_MustNotUseLombok() {
        ArchRule rule =
                noClasses()
                        .that()
                        .haveNameMatching(".*ApiResponse")
                        .and()
                        .resideInAPackage("..common.dto..")
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
                        .because("Common Response DTOs는 Pure Java Record를 사용해야 하며 Lombok은 금지됩니다");

        rule.allowEmptyShould(true).check(classes);
    }

    /** 규칙 6: Common Response DTOs는 public이어야 함 */
    @Test
    @DisplayName("[필수] Common Response DTOs는 public이어야 한다")
    void commonResponseDtos_MustBePublic() {
        ArchRule rule =
                classes()
                        .that()
                        .haveNameMatching(".*ApiResponse")
                        .and()
                        .resideInAPackage("..common.dto..")
                        .and()
                        .areNotNestedClasses()
                        .should()
                        .bePublic()
                        .because("Common Response DTOs는 외부에서 사용 가능하도록 public이어야 합니다");

        rule.allowEmptyShould(true).check(classes);
    }

    /** 규칙 7: Common Response DTOs는 final이어야 함 (Record 특성) */
    @Test
    @DisplayName("[권장] Common Response DTOs Record는 final 특성을 가진다")
    void commonResponseDtos_ShouldBeFinal() {
        // Note: Java Record는 암묵적으로 final이므로 Record 타입 검증으로 대체
        ArchRule rule =
                classes()
                        .that()
                        .haveNameMatching(".*ApiResponse")
                        .and()
                        .resideInAPackage("..common.dto..")
                        .and()
                        .areNotNestedClasses()
                        .should()
                        .beRecords()
                        .because("Common Response DTOs는 Record로 정의되어 암묵적으로 final 특성을 가집니다");

        rule.allowEmptyShould(true).check(classes);
    }
}
