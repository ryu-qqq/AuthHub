package com.ryuqq.authhub.sdk.api;

import com.ryuqq.authhub.sdk.model.common.ApiResponse;
import com.ryuqq.authhub.sdk.model.onboarding.TenantOnboardingRequest;
import com.ryuqq.authhub.sdk.model.onboarding.TenantOnboardingResponse;

/** Onboarding 관련 API 인터페이스. 테넌트 온보딩(테넌트+조직+역할+사용자 일괄 생성) 기능을 제공합니다. */
public interface OnboardingApi {

    /**
     * 테넌트 온보딩을 수행합니다. 테넌트, 조직, 기본 역할, 관리자 사용자를 일괄 생성합니다.
     *
     * @param request 온보딩 요청
     * @return 온보딩 결과 (생성된 리소스 ID들)
     */
    ApiResponse<TenantOnboardingResponse> onboard(TenantOnboardingRequest request);
}
