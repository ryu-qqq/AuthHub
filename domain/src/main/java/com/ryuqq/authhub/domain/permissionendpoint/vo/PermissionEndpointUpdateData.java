package com.ryuqq.authhub.domain.permissionendpoint.vo;

/**
 * PermissionEndpointUpdateData - PermissionEndpoint 수정 데이터 Value Object
 *
 * <p>PermissionEndpoint 수정 시 필요한 데이터를 캡슐화합니다.
 *
 * @param urlPattern 새 URL 패턴 (null이면 변경 안 함)
 * @param httpMethod 새 HTTP 메서드 (null이면 변경 안 함)
 * @param description 새 설명 (null이면 변경 안 함)
 * @author development-team
 * @since 1.0.0
 */
public record PermissionEndpointUpdateData(
        String urlPattern, String httpMethod, String description) {

    /**
     * 팩토리 메서드
     *
     * @param urlPattern URL 패턴
     * @param httpMethod HTTP 메서드 문자열
     * @param description 설명
     * @return PermissionEndpointUpdateData 인스턴스
     */
    public static PermissionEndpointUpdateData of(
            String urlPattern, String httpMethod, String description) {
        return new PermissionEndpointUpdateData(urlPattern, httpMethod, description);
    }

    /**
     * URL 패턴 변경 여부
     *
     * @return urlPattern이 null이 아니고 빈 값이 아니면 true
     */
    public boolean hasUrlPattern() {
        return urlPattern != null && !urlPattern.isBlank();
    }

    /**
     * HTTP 메서드 변경 여부
     *
     * @return httpMethod가 null이 아니고 빈 값이 아니면 true
     */
    public boolean hasHttpMethod() {
        return httpMethod != null && !httpMethod.isBlank();
    }

    /**
     * 설명 변경 여부
     *
     * @return description이 null이 아니면 true
     */
    public boolean hasDescription() {
        return description != null;
    }

    /**
     * HTTP 메서드를 HttpMethod enum으로 변환
     *
     * @return HttpMethod enum 또는 null
     */
    public HttpMethod httpMethodEnum() {
        return hasHttpMethod() ? HttpMethod.from(httpMethod) : null;
    }
}
