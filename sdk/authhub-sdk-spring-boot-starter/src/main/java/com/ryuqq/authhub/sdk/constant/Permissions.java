package com.ryuqq.authhub.sdk.constant;

/**
 * Permissions - 권한 상수 정의
 *
 * <p>시스템 전역에서 사용되는 세분화된 권한 상수입니다. 형식: {domain}:{action}
 *
 * <p><strong>권한 형식:</strong>
 *
 * <ul>
 *   <li>domain: 리소스 도메인 (user, product, order 등)
 *   <li>action: 수행 가능한 동작 (read, write, delete 등)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public final class Permissions {

    // ===== User Domain =====

    /** 사용자 조회 권한 */
    public static final String USER_READ = "user:read";

    /** 사용자 생성/수정 권한 */
    public static final String USER_WRITE = "user:write";

    /** 사용자 삭제 권한 */
    public static final String USER_DELETE = "user:delete";

    /** 사용자 역할 할당 권한 */
    public static final String USER_ROLE_ASSIGN = "user:role:assign";

    // ===== Role Domain =====

    /** 역할 조회 권한 */
    public static final String ROLE_READ = "role:read";

    /** 역할 생성/수정 권한 */
    public static final String ROLE_WRITE = "role:write";

    /** 역할 삭제 권한 */
    public static final String ROLE_DELETE = "role:delete";

    // ===== Permission Domain =====

    /** 권한 조회 권한 */
    public static final String PERMISSION_READ = "permission:read";

    /** 권한 생성/수정 권한 */
    public static final String PERMISSION_WRITE = "permission:write";

    /** 권한 삭제 권한 */
    public static final String PERMISSION_DELETE = "permission:delete";

    // ===== Organization Domain =====

    /** 조직 조회 권한 */
    public static final String ORGANIZATION_READ = "organization:read";

    /** 조직 생성/수정 권한 */
    public static final String ORGANIZATION_WRITE = "organization:write";

    /** 조직 삭제 권한 */
    public static final String ORGANIZATION_DELETE = "organization:delete";

    // ===== Tenant Domain =====

    /** 테넌트 조회 권한 */
    public static final String TENANT_READ = "tenant:read";

    /** 테넌트 생성/수정 권한 */
    public static final String TENANT_WRITE = "tenant:write";

    /** 테넌트 삭제 권한 */
    public static final String TENANT_DELETE = "tenant:delete";

    // ===== Product Domain =====

    /** 상품 조회 권한 */
    public static final String PRODUCT_READ = "product:read";

    /** 상품 생성/수정 권한 */
    public static final String PRODUCT_WRITE = "product:write";

    /** 상품 삭제 권한 */
    public static final String PRODUCT_DELETE = "product:delete";

    // ===== File Domain =====

    /** 파일 조회 권한 */
    public static final String FILE_READ = "file:read";

    /** 파일 업로드 권한 */
    public static final String FILE_UPLOAD = "file:upload";

    /** 파일 삭제 권한 */
    public static final String FILE_DELETE = "file:delete";

    // ===== Wildcard =====

    /** 모든 권한 (Super Admin용) */
    public static final String ALL = "*:*";

    private Permissions() {
        throw new AssertionError("Utility class - cannot instantiate");
    }

    // ===== Validation Patterns =====

    /**
     * 권한 세그먼트 패턴 (소문자, 숫자, 하이픈만 허용)
     *
     * <p>예: user, product-service, order123
     */
    private static final String SEGMENT_PATTERN = "[a-z][a-z0-9-]*";

    /**
     * 유효한 권한 형식 패턴
     *
     * <p>허용 형식:
     *
     * <ul>
     *   <li>{domain}:{action} - 예: user:read, product:write
     *   <li>{service}:{domain}:{action} - 예: product-service:product:read
     * </ul>
     */
    private static final java.util.regex.Pattern VALID_FORMAT_PATTERN =
            java.util.regex.Pattern.compile(
                    "^" + SEGMENT_PATTERN + ":" + SEGMENT_PATTERN + "(:" + SEGMENT_PATTERN + ")?$");

    /**
     * 권한 형식이 유효한지 확인 (엄격한 검증)
     *
     * <p>유효한 형식:
     *
     * <ul>
     *   <li>{domain}:{action} - 예: user:read
     *   <li>{service}:{domain}:{action} - 예: authhub:user:read
     * </ul>
     *
     * <p>규칙:
     *
     * <ul>
     *   <li>소문자, 숫자, 하이픈만 허용
     *   <li>각 세그먼트는 소문자로 시작
     *   <li>와일드카드(*:*)는 별도 허용
     * </ul>
     *
     * @param permission 권한 문자열
     * @return 유효한 형식이면 true
     */
    public static boolean isValidFormat(String permission) {
        if (permission == null || permission.isBlank()) {
            return false;
        }
        if (ALL.equals(permission)) {
            return true;
        }
        return VALID_FORMAT_PATTERN.matcher(permission).matches();
    }

    /**
     * 권한 형식이 유효한지 확인 (느슨한 검증 - 레거시 호환)
     *
     * <p>최소 2개 세그먼트만 확인하고 문자 규칙은 검증하지 않습니다.
     *
     * @param permission 권한 문자열
     * @return 유효한 형식이면 true
     * @deprecated {@link #isValidFormat(String)} 사용 권장
     */
    @Deprecated
    public static boolean isValidFormatLegacy(String permission) {
        if (permission == null || permission.isBlank()) {
            return false;
        }
        if (ALL.equals(permission)) {
            return true;
        }
        String[] parts = permission.split(":");
        return parts.length >= 2 && !parts[0].isBlank() && !parts[1].isBlank();
    }

    /**
     * 권한 형식 검증 결과 반환 (상세 오류 메시지 포함)
     *
     * @param permission 권한 문자열
     * @return 검증 결과 (유효하면 빈 Optional, 오류면 메시지 포함)
     */
    public static java.util.Optional<String> validateFormat(String permission) {
        if (permission == null || permission.isBlank()) {
            return java.util.Optional.of("Permission cannot be null or blank");
        }
        if (ALL.equals(permission)) {
            return java.util.Optional.empty();
        }

        String[] parts = permission.split(":");
        if (parts.length < 2 || parts.length > 3) {
            return java.util.Optional.of(
                    "Permission must have 2 or 3 segments (domain:action or service:domain:action),"
                            + " got: "
                            + parts.length);
        }

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (part.isBlank()) {
                return java.util.Optional.of("Segment " + (i + 1) + " cannot be blank");
            }
            if (!part.matches(SEGMENT_PATTERN)) {
                return java.util.Optional.of(
                        "Segment '"
                                + part
                                + "' must start with lowercase letter and contain only lowercase"
                                + " letters, digits, or hyphens");
            }
        }

        return java.util.Optional.empty();
    }

    /**
     * 권한에서 서비스명 추출 (3세그먼트 형식에서만)
     *
     * @param permission 권한 문자열
     * @return 서비스명 (예: "authhub:user:read" -> "authhub"), 2세그먼트면 null
     */
    public static String extractService(String permission) {
        if (permission == null || !permission.contains(":")) {
            return null;
        }
        String[] parts = permission.split(":");
        return parts.length == 3 ? parts[0] : null;
    }

    /**
     * 권한에서 도메인 추출
     *
     * @param permission 권한 문자열
     * @return 도메인 (예: "user:read" -> "user", "authhub:user:read" -> "user")
     */
    public static String extractDomain(String permission) {
        if (permission == null || !permission.contains(":")) {
            return null;
        }
        String[] parts = permission.split(":");
        if (parts.length == 2) {
            return parts[0];
        } else if (parts.length == 3) {
            return parts[1];
        }
        return null;
    }

    /**
     * 권한에서 액션 추출
     *
     * @param permission 권한 문자열
     * @return 액션 (예: "user:read" -> "read", "authhub:user:read" -> "read")
     */
    public static String extractAction(String permission) {
        if (permission == null || !permission.contains(":")) {
            return null;
        }
        String[] parts = permission.split(":");
        if (parts.length >= 2) {
            return parts[parts.length - 1];
        }
        return null;
    }

    /**
     * 권한 문자열 생성 (2세그먼트: domain:action)
     *
     * @param domain 도메인
     * @param action 액션
     * @return 권한 문자열
     * @throws IllegalArgumentException 형식이 유효하지 않으면
     */
    public static String of(String domain, String action) {
        String permission = domain + ":" + action;
        validateFormat(permission)
                .ifPresent(
                        error -> {
                            throw new IllegalArgumentException(error);
                        });
        return permission;
    }

    /**
     * 권한 문자열 생성 (3세그먼트: service:domain:action)
     *
     * @param service 서비스명
     * @param domain 도메인
     * @param action 액션
     * @return 권한 문자열
     * @throws IllegalArgumentException 형식이 유효하지 않으면
     */
    public static String of(String service, String domain, String action) {
        String permission = service + ":" + domain + ":" + action;
        validateFormat(permission)
                .ifPresent(
                        error -> {
                            throw new IllegalArgumentException(error);
                        });
        return permission;
    }
}
