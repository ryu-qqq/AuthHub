package com.ryuqq.authhub.adapter.in.rest.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.core.importer.Location;

/**
 * ArchUnit 테스트 공통 설정
 *
 * <p>모든 ArchUnit 테스트에서 보안 관련 패키지와 테스트 클래스를 제외합니다. auth 패키지는 보안 인프라용 특수 패키지로 일반적인 BC 규칙을 따르지 않습니다.
 *
 * @author development-team
 * @since 1.0.0
 */
public final class ArchTestConfig {

    /** auth 패키지를 제외하는 ImportOption */
    public static final ImportOption EXCLUDE_AUTH_PACKAGE =
            (Location location) -> !location.contains("/auth/");

    /** 보안 패키지를 제외한 REST API Layer 클래스들 (테스트 클래스 제외) */
    public static JavaClasses getRestApiClassesExcludingAuth() {
        return new ClassFileImporter()
                .withImportOption(EXCLUDE_AUTH_PACKAGE)
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("com.ryuqq.authhub.adapter.in.rest");
    }

    /** auth 패키지 포함 여부 확인 */
    public static boolean isInAuthPackage(String packageName) {
        return packageName.contains(".auth.");
    }

    private ArchTestConfig() {}
}
