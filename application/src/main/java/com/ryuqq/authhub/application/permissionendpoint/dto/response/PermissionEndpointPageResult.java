package com.ryuqq.authhub.application.permissionendpoint.dto.response;

import com.ryuqq.authhub.domain.common.vo.PageMeta;
import java.util.List;

/**
 * PermissionEndpointPageResult - 엔드포인트 페이지 조회 결과 DTO
 *
 * <p>Application Layer에서 사용하는 PermissionEndpoint 페이지 응답 DTO입니다.
 *
 * <p>RDTO-001: Response DTO는 Record로 정의.
 *
 * <p>RDTO-009: List 직접 반환 금지 -> PageResult 래핑 필수.
 *
 * @param content 결과 목록
 * @param pageMeta 페이지 메타데이터
 * @author development-team
 * @since 1.0.0
 */
public record PermissionEndpointPageResult(
        List<PermissionEndpointResult> content, PageMeta pageMeta) {

    /**
     * 팩토리 메서드
     *
     * @param content 결과 목록
     * @param page 현재 페이지 번호
     * @param size 페이지 크기
     * @param totalElements 전체 요소 수
     * @return PermissionEndpointPageResult 인스턴스
     */
    public static PermissionEndpointPageResult of(
            List<PermissionEndpointResult> content, int page, int size, long totalElements) {
        PageMeta pageMeta = PageMeta.of(page, size, totalElements);
        return new PermissionEndpointPageResult(content, pageMeta);
    }
}
