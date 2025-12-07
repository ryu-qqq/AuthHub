package com.ryuqq.authhub.application.tenant.dto.query;

/**
 * SearchTenantsQuery - 테넌트 목록 조회 Query DTO (Offset 기반 페이징)
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>순수 Java Record
 *   <li>기본값 처리 금지 (REST API 책임)
 *   <li>Lombok 금지
 * </ul>
 *
 * @param name 테넌트 이름 필터 (null 허용, 부분 검색)
 * @param status 테넌트 상태 필터 (null 허용)
 * @param page 페이지 번호 (0부터 시작)
 * @param size 페이지 크기
 * @author development-team
 * @since 1.0.0
 */
public record SearchTenantsQuery(String name, String status, Integer page, Integer size) {}
