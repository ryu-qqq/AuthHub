package com.ryuqq.authhub.application.tenantservice.dto.response;

import com.ryuqq.authhub.domain.common.vo.PageMeta;
import java.util.List;

/**
 * TenantServicePageResult - 테넌트-서비스 구독 페이지 조회 결과 DTO
 *
 * <p>RDTO-001: Application 반환 DTO는 Record로 작성합니다.
 *
 * <p>RDTO-009: List 직접 반환 금지 -> PageMeta와 함께 반환합니다.
 *
 * @param content 테넌트-서비스 구독 결과 목록
 * @param pageMeta 페이지 메타 정보
 * @author development-team
 * @since 1.0.0
 */
public record TenantServicePageResult(List<TenantServiceResult> content, PageMeta pageMeta) {

    /**
     * 빈 결과 생성
     *
     * @param size 페이지 크기
     * @return 빈 TenantServicePageResult
     */
    public static TenantServicePageResult empty(int size) {
        return new TenantServicePageResult(List.of(), PageMeta.empty(size));
    }

    /**
     * TenantServicePageResult 생성
     *
     * @param content 결과 목록
     * @param page 현재 페이지 번호
     * @param size 페이지 크기
     * @param totalElements 전체 요소 수
     * @return TenantServicePageResult 인스턴스
     */
    public static TenantServicePageResult of(
            List<TenantServiceResult> content, int page, int size, long totalElements) {
        PageMeta pageMeta = PageMeta.of(page, size, totalElements);
        return new TenantServicePageResult(content, pageMeta);
    }
}
