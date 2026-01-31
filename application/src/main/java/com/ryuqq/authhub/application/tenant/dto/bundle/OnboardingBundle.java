package com.ryuqq.authhub.application.tenant.dto.bundle;

import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;

/**
 * OnboardingBundle - 온보딩 번들 DTO (내부 전용)
 *
 * <p>Factory에서 생성한 Tenant와 Organization 도메인 객체를 Facade로 전달합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>순수 Java Record
 *   <li>Lombok 금지
 *   <li>Application Layer 내부에서만 사용
 * </ul>
 *
 * @param tenant 테넌트 도메인 에그리게이트
 * @param organization 조직 도메인 에그리게이트
 * @author development-team
 * @since 1.0.0
 */
public record OnboardingBundle(Tenant tenant, Organization organization) {}
