package com.ryuqq.authhub.application.permission.dto.response;

import com.ryuqq.authhub.domain.common.vo.PageMeta;
import java.util.List;

/**
 * PermissionPageResult - 권한 목록 페이징 결과 DTO
 *
 * <p>페이징된 Permission 목록과 메타 정보를 담습니다.
 *
 * <p>RDTO-001: Application 반환 DTO는 Record로 작성합니다.
 *
 * <p>RDTO-009: List 직접 반환 금지 → PageMeta와 함께 반환합니다.
 *
 * @param content Permission 결과 목록
 * @param pageMeta 페이지 메타 정보
 * @author development-team
 * @since 1.0.0
 */
public record PermissionPageResult(List<PermissionResult> content, PageMeta pageMeta) {

    /**
     * 빈 결과 생성
     *
     * @param size 페이지 크기
     * @return 빈 PermissionPageResult
     */
    public static PermissionPageResult empty(int size) {
        return new PermissionPageResult(List.of(), PageMeta.empty(size));
    }

    /**
     * PermissionPageResult 생성
     *
     * @param content Permission 결과 목록
     * @param page 현재 페이지 번호
     * @param size 페이지 크기
     * @param totalElements 전체 요소 수
     * @return PermissionPageResult 인스턴스
     */
    public static PermissionPageResult of(
            List<PermissionResult> content, int page, int size, long totalElements) {
        PageMeta pageMeta = PageMeta.of(page, size, totalElements);
        return new PermissionPageResult(content, pageMeta);
    }
}
