package com.ryuqq.authhub.sdk.api;

import com.ryuqq.authhub.sdk.model.common.ApiResponse;
import com.ryuqq.authhub.sdk.model.onboarding.TenantOnboardingRequest;
import com.ryuqq.authhub.sdk.model.onboarding.TenantOnboardingResponse;

/**
 * Onboarding 관련 API 인터페이스. 테넌트 온보딩(테넌트+조직 일괄 생성) 기능을 제공합니다.
 *
 * <p><strong>멱등키 필수:</strong>
 *
 * <ul>
 *   <li>X-Idempotency-Key 헤더를 통해 멱등키 전송 (필수)
 *   <li>동일한 멱등키로 요청 시 캐시된 응답 반환 (24시간 유지)
 *   <li>네트워크 오류 등으로 인한 재시도 시 중복 생성 방지
 *   <li>UUID 형식 권장
 * </ul>
 */
public interface OnboardingApi {

    /**
     * 테넌트 온보딩을 수행합니다.
     *
     * <p>멱등키를 사용하여 동일한 요청에 대해 항상 동일한 응답을 보장합니다. 네트워크 오류로 인한 재시도 시 중복 생성을 방지합니다.
     *
     * @param request 온보딩 요청
     * @param idempotencyKey 멱등키 (필수, UUID 권장, X-Idempotency-Key 헤더로 전송)
     * @return 온보딩 결과 (생성된 리소스 ID들)
     */
    ApiResponse<TenantOnboardingResponse> onboard(
            TenantOnboardingRequest request, String idempotencyKey);
}
