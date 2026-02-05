package com.ryuqq.authhub.application.service.fixture;

import com.ryuqq.authhub.application.common.dto.query.CommonSearchParams;
import com.ryuqq.authhub.application.service.dto.query.ServiceSearchParams;
import java.util.List;

/**
 * Service Query DTO 테스트 픽스처
 *
 * <p>Application Layer 테스트에서 재사용 가능한 Query DTO를 제공합니다.
 *
 * <p><strong>설계 원칙:</strong>
 *
 * <ul>
 *   <li>Domain Fixture와 일관된 기본값 사용
 *   <li>테스트 가독성을 위한 명확한 팩토리 메서드
 *   <li>불변 객체 반환으로 테스트 격리 보장
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public final class ServiceQueryFixtures {

    private ServiceQueryFixtures() {}

    // ==================== ServiceSearchParams ====================

    /** 기본 검색 파라미터 (기본값 사용) */
    public static ServiceSearchParams searchParams() {
        CommonSearchParams commonParams = defaultCommonSearchParams();
        return ServiceSearchParams.of(commonParams, null, null, null);
    }

    /** 검색어를 포함한 검색 파라미터 */
    public static ServiceSearchParams searchParamsWithWord(String searchWord) {
        CommonSearchParams commonParams = defaultCommonSearchParams();
        return ServiceSearchParams.of(commonParams, searchWord, null, null);
    }

    /** 검색어 + 검색필드를 포함한 검색 파라미터 */
    public static ServiceSearchParams searchParamsWithField(String searchWord, String searchField) {
        CommonSearchParams commonParams = defaultCommonSearchParams();
        return ServiceSearchParams.of(commonParams, searchWord, searchField, null);
    }

    /** 상태 필터를 포함한 검색 파라미터 */
    public static ServiceSearchParams searchParamsWithStatuses(List<String> statuses) {
        CommonSearchParams commonParams = defaultCommonSearchParams();
        return ServiceSearchParams.of(commonParams, null, null, statuses);
    }

    /** 모든 조건을 포함한 검색 파라미터 */
    public static ServiceSearchParams searchParams(
            String searchWord, String searchField, List<String> statuses) {
        CommonSearchParams commonParams = defaultCommonSearchParams();
        return ServiceSearchParams.of(commonParams, searchWord, searchField, statuses);
    }

    /** 페이징 지정 검색 파라미터 */
    public static ServiceSearchParams searchParams(int page, int size) {
        CommonSearchParams commonParams =
                CommonSearchParams.of(false, null, null, "createdAt", "DESC", page, size);
        return ServiceSearchParams.of(commonParams, null, null, null);
    }

    // ==================== CommonSearchParams ====================

    /** 기본 CommonSearchParams */
    public static CommonSearchParams defaultCommonSearchParams() {
        return CommonSearchParams.of(false, null, null, "createdAt", "DESC", 0, 20);
    }
}
