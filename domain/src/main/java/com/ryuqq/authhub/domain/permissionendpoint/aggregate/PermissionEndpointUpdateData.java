package com.ryuqq.authhub.domain.permissionendpoint.aggregate;

import com.ryuqq.authhub.domain.permissionendpoint.vo.HttpMethod;
import com.ryuqq.authhub.domain.permissionendpoint.vo.ServiceName;
import com.ryuqq.authhub.domain.permissionendpoint.vo.UrlPattern;

/**
 * PermissionEndpointUpdateData - PermissionEndpoint 수정 데이터 Value Object
 *
 * <p>PermissionEndpoint 수정 시 필요한 데이터를 캡슐화합니다. 모든 필드가 채워져 있어야 합니다.
 *
 * @param serviceName 서비스 이름
 * @param urlPattern URL 패턴
 * @param httpMethod HTTP 메서드
 * @param description 설명
 * @param isPublic 공개 엔드포인트 여부
 * @author development-team
 * @since 1.0.0
 */
public record PermissionEndpointUpdateData(
        ServiceName serviceName,
        UrlPattern urlPattern,
        HttpMethod httpMethod,
        String description,
        boolean isPublic) {

    /**
     * 팩토리 메서드 (VO 타입)
     *
     * @param serviceName 서비스 이름
     * @param urlPattern URL 패턴
     * @param httpMethod HTTP 메서드
     * @param description 설명
     * @param isPublic 공개 엔드포인트 여부
     * @return PermissionEndpointUpdateData 인스턴스
     */
    public static PermissionEndpointUpdateData of(
            ServiceName serviceName,
            UrlPattern urlPattern,
            HttpMethod httpMethod,
            String description,
            boolean isPublic) {
        return new PermissionEndpointUpdateData(
                serviceName, urlPattern, httpMethod, description, isPublic);
    }

    /**
     * 팩토리 메서드 (문자열 파라미터 편의 메서드)
     *
     * @param serviceName 서비스 이름 (String)
     * @param urlPattern URL 패턴 (String)
     * @param httpMethod HTTP 메서드 (String)
     * @param description 설명
     * @param isPublic 공개 엔드포인트 여부
     * @return PermissionEndpointUpdateData 인스턴스
     */
    public static PermissionEndpointUpdateData of(
            String serviceName,
            String urlPattern,
            String httpMethod,
            String description,
            boolean isPublic) {
        return new PermissionEndpointUpdateData(
                ServiceName.of(serviceName),
                UrlPattern.of(urlPattern),
                HttpMethod.from(httpMethod),
                description,
                isPublic);
    }
}
