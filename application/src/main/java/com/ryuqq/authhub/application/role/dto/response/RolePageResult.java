package com.ryuqq.authhub.application.role.dto.response;

import com.ryuqq.authhub.domain.common.vo.PageMeta;
import java.util.List;

/**
 * RolePageResult - 역할 페이지 조회 결과 DTO
 *
 * <p>페이징된 역할 목록과 메타 정보를 담습니다.
 *
 * <p>RDTO-001: Application 반환 DTO는 Record로 작성합니다.
 *
 * <p>RDTO-009: List 직접 반환 금지 → PageMeta와 함께 반환합니다.
 *
 * @param content 역할 결과 목록
 * @param pageMeta 페이지 메타 정보
 * @author development-team
 * @since 1.0.0
 */
public record RolePageResult(List<RoleResult> content, PageMeta pageMeta) {

    /**
     * 빈 결과 생성
     *
     * @param size 페이지 크기
     * @return 빈 RolePageResult
     */
    public static RolePageResult empty(int size) {
        return new RolePageResult(List.of(), PageMeta.empty(size));
    }

    /**
     * RolePageResult 생성
     *
     * @param content 역할 결과 목록
     * @param page 현재 페이지 번호
     * @param size 페이지 크기
     * @param totalElements 전체 요소 수
     * @return RolePageResult 인스턴스
     */
    public static RolePageResult of(
            List<RoleResult> content, int page, int size, long totalElements) {
        PageMeta pageMeta = PageMeta.of(page, size, totalElements);
        return new RolePageResult(content, pageMeta);
    }
}
