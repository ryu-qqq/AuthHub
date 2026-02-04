package com.ryuqq.authhub.application.tenantservice.fixture;

import com.ryuqq.authhub.application.tenantservice.dto.command.SubscribeTenantServiceCommand;
import com.ryuqq.authhub.application.tenantservice.dto.command.UpdateTenantServiceStatusCommand;
import com.ryuqq.authhub.domain.tenantservice.fixture.TenantServiceFixture;

/**
 * TenantService Command DTO 테스트 픽스처
 *
 * <p>Application Layer 테스트에서 재사용 가능한 Command DTO를 제공합니다.
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
public final class TenantServiceCommandFixtures {

    private static final Long DEFAULT_TENANT_SERVICE_ID = TenantServiceFixture.defaultIdValue();
    private static final String DEFAULT_TENANT_ID = TenantServiceFixture.defaultTenantIdValue();
    private static final Long DEFAULT_SERVICE_ID = TenantServiceFixture.defaultServiceIdValue();
    private static final String DEFAULT_STATUS = "ACTIVE";

    private TenantServiceCommandFixtures() {}

    // ==================== SubscribeTenantServiceCommand ====================

    /** 기본 구독 Command 반환 */
    public static SubscribeTenantServiceCommand subscribeCommand() {
        return new SubscribeTenantServiceCommand(DEFAULT_TENANT_ID, DEFAULT_SERVICE_ID);
    }

    /** 지정된 테넌트로 구독 Command 반환 */
    public static SubscribeTenantServiceCommand subscribeCommand(String tenantId) {
        return new SubscribeTenantServiceCommand(tenantId, DEFAULT_SERVICE_ID);
    }

    /** 지정된 서비스로 구독 Command 반환 */
    public static SubscribeTenantServiceCommand subscribeCommand(Long serviceId) {
        return new SubscribeTenantServiceCommand(DEFAULT_TENANT_ID, serviceId);
    }

    /** 지정된 테넌트 + 서비스로 구독 Command 반환 */
    public static SubscribeTenantServiceCommand subscribeCommand(String tenantId, Long serviceId) {
        return new SubscribeTenantServiceCommand(tenantId, serviceId);
    }

    // ==================== UpdateTenantServiceStatusCommand ====================

    /** 기본 상태 변경 Command 반환 (INACTIVE) */
    public static UpdateTenantServiceStatusCommand updateStatusCommand() {
        return new UpdateTenantServiceStatusCommand(DEFAULT_TENANT_SERVICE_ID, "INACTIVE");
    }

    /** 지정된 상태로 변경 Command 반환 */
    public static UpdateTenantServiceStatusCommand updateStatusCommand(String status) {
        return new UpdateTenantServiceStatusCommand(DEFAULT_TENANT_SERVICE_ID, status);
    }

    /** 지정된 ID + 상태로 변경 Command 반환 */
    public static UpdateTenantServiceStatusCommand updateStatusCommand(
            Long tenantServiceId, String status) {
        return new UpdateTenantServiceStatusCommand(tenantServiceId, status);
    }

    // ==================== 기본값 접근자 ====================

    public static Long defaultTenantServiceId() {
        return DEFAULT_TENANT_SERVICE_ID;
    }

    public static String defaultTenantId() {
        return DEFAULT_TENANT_ID;
    }

    public static Long defaultServiceId() {
        return DEFAULT_SERVICE_ID;
    }

    public static String defaultStatus() {
        return DEFAULT_STATUS;
    }
}
