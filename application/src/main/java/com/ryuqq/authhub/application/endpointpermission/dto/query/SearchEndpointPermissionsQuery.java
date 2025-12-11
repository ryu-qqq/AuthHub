package com.ryuqq.authhub.application.endpointpermission.dto.query;

/**
 * SearchEndpointPermissionsQuery - 엔드포인트 권한 검색 Query DTO
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>순수 Java Record (jakarta.validation 금지)
 *   <li>Lombok 금지
 *   <li>비즈니스 로직 금지 (Domain 책임)
 * </ul>
 *
 * @param serviceName 서비스 이름 필터 (null 가능)
 * @param pathPattern 경로 패턴 필터 (부분 일치, null 가능)
 * @param method HTTP 메서드 필터 (null 가능)
 * @param isPublic 공개 여부 필터 (null 가능)
 * @param page 페이지 번호 (0부터 시작)
 * @param size 페이지 크기
 * @author development-team
 * @since 1.0.0
 */
public record SearchEndpointPermissionsQuery(
        String serviceName,
        String pathPattern,
        String method,
        Boolean isPublic,
        int page,
        int size) {}
