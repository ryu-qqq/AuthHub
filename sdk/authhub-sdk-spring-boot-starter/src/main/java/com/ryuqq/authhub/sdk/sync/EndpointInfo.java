package com.ryuqq.authhub.sdk.sync;

/**
 * EndpointInfo - 스캔된 엔드포인트 정보
 *
 * <p>@RequirePermission 어노테이션이 붙은 엔드포인트의 정보를 담습니다.
 *
 * @param httpMethod HTTP 메서드 (GET, POST, PUT, DELETE 등)
 * @param pathPattern URL 패턴 (예: "/api/v1/users/{id}")
 * @param permissionKey 필요 권한 키 (예: "user:read")
 * @param description 엔드포인트 설명
 * @author development-team
 * @since 1.0.0
 */
public record EndpointInfo(
        String httpMethod, String pathPattern, String permissionKey, String description) {

    /**
     * EndpointInfo 생성
     *
     * @param httpMethod HTTP 메서드
     * @param pathPattern URL 패턴
     * @param permissionKey 권한 키
     * @param description 설명
     * @return EndpointInfo 인스턴스
     */
    public static EndpointInfo of(
            String httpMethod, String pathPattern, String permissionKey, String description) {
        return new EndpointInfo(httpMethod, pathPattern, permissionKey, description);
    }
}
