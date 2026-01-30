package com.ryuqq.authhub.application.rolepermission.dto.response;

import com.ryuqq.authhub.domain.common.vo.PageMeta;
import java.util.List;

/**
 * RolePermissionPageResult - 역할-권한 관계 페이지 조회 결과
 *
 * <p>역할-권한 관계 목록과 페이지 메타 정보를 포함하는 응답 DTO입니다.
 *
 * @param content 역할-권한 관계 목록
 * @param pageMeta 페이지 메타 정보
 * @author development-team
 * @since 1.0.0
 */
public record RolePermissionPageResult(List<RolePermissionResult> content, PageMeta pageMeta) {

    /**
     * 빈 결과 생성
     *
     * @param size 페이지 크기
     * @return 빈 RolePermissionPageResult
     */
    public static RolePermissionPageResult empty(int size) {
        return new RolePermissionPageResult(List.of(), PageMeta.empty(size));
    }

    /**
     * 팩토리 메서드
     *
     * @param content 역할-권한 관계 목록
     * @param pageNumber 현재 페이지 번호
     * @param pageSize 페이지 크기
     * @param totalElements 전체 요소 개수
     * @return RolePermissionPageResult
     */
    public static RolePermissionPageResult of(
            List<RolePermissionResult> content, int pageNumber, int pageSize, long totalElements) {
        PageMeta pageMeta = PageMeta.of(pageNumber, pageSize, totalElements);
        return new RolePermissionPageResult(content, pageMeta);
    }
}
