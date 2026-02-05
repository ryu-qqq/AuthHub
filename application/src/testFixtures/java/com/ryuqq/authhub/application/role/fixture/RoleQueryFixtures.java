package com.ryuqq.authhub.application.role.fixture;

import com.ryuqq.authhub.application.common.dto.query.CommonSearchParams;
import com.ryuqq.authhub.application.role.dto.query.RoleSearchParams;
import java.util.List;

/**
 * Role Query DTO 테스트 픽스처
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
public final class RoleQueryFixtures {

    private RoleQueryFixtures() {}

    // ==================== RoleSearchParams ====================

    /** 기본 검색 파라미터 (Global, 기본값 사용) */
    public static RoleSearchParams searchParams() {
        CommonSearchParams commonParams = defaultCommonSearchParams();
        return RoleSearchParams.of(commonParams, null, null, null, null, null);
    }

    /** 검색어를 포함한 검색 파라미터 */
    public static RoleSearchParams searchParamsWithWord(String searchWord) {
        CommonSearchParams commonParams = defaultCommonSearchParams();
        return RoleSearchParams.of(commonParams, null, null, searchWord, null, null);
    }

    /** 역할 유형 필터를 포함한 검색 파라미터 */
    public static RoleSearchParams searchParamsWithTypes(List<String> types) {
        CommonSearchParams commonParams = defaultCommonSearchParams();
        return RoleSearchParams.of(commonParams, null, null, null, null, types);
    }

    /** 테넌트 ID를 포함한 검색 파라미터 */
    public static RoleSearchParams searchParamsWithTenantId(String tenantId) {
        CommonSearchParams commonParams = defaultCommonSearchParams();
        return RoleSearchParams.of(commonParams, tenantId, null, null, null, null);
    }

    /** ofGlobal 간편 생성 (검색어, 타입, 날짜, 페이징) */
    public static RoleSearchParams searchParamsGlobal(
            String searchWord, List<String> types, Integer page, Integer size) {
        return RoleSearchParams.ofGlobal(searchWord, types, null, null, page, size);
    }

    // ==================== CommonSearchParams ====================

    /** 기본 CommonSearchParams */
    public static CommonSearchParams defaultCommonSearchParams() {
        return CommonSearchParams.of(false, null, null, "createdAt", "DESC", 0, 20);
    }
}
