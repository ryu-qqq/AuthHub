package com.ryuqq.authhub.application.service.fixture;

import com.ryuqq.authhub.application.service.dto.command.CreateServiceCommand;
import com.ryuqq.authhub.application.service.dto.command.UpdateServiceCommand;
import com.ryuqq.authhub.domain.service.fixture.ServiceFixture;

/**
 * Service Command DTO 테스트 픽스처
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
public final class ServiceCommandFixtures {

    private static final Long DEFAULT_SERVICE_ID = ServiceFixture.defaultIdValue();
    private static final String DEFAULT_SERVICE_CODE = ServiceFixture.defaultCodeValue();
    private static final String DEFAULT_NAME = "자사몰";
    private static final String DEFAULT_DESCRIPTION = "자사몰 서비스";

    private ServiceCommandFixtures() {}

    // ==================== CreateServiceCommand ====================

    /** 기본 생성 Command 반환 */
    public static CreateServiceCommand createCommand() {
        return new CreateServiceCommand(DEFAULT_SERVICE_CODE, DEFAULT_NAME, DEFAULT_DESCRIPTION);
    }

    /** 지정된 코드/이름으로 생성 Command 반환 */
    public static CreateServiceCommand createCommand(String serviceCode, String name) {
        return new CreateServiceCommand(serviceCode, name, name + " 서비스");
    }

    /** 모든 값을 지정하여 생성 Command 반환 */
    public static CreateServiceCommand createCommand(
            String serviceCode, String name, String description) {
        return new CreateServiceCommand(serviceCode, name, description);
    }

    // ==================== UpdateServiceCommand ====================

    /** 기본 수정 Command 반환 (이름만 변경) */
    public static UpdateServiceCommand updateCommand() {
        return new UpdateServiceCommand(DEFAULT_SERVICE_ID, "수정된 서비스명", null, null);
    }

    /** 전체 필드 수정 Command 반환 */
    public static UpdateServiceCommand updateCommandFull() {
        return new UpdateServiceCommand(DEFAULT_SERVICE_ID, "수정된 서비스명", "수정된 설명", "INACTIVE");
    }

    /** 지정된 값으로 수정 Command 반환 */
    public static UpdateServiceCommand updateCommand(
            Long serviceId, String name, String description, String status) {
        return new UpdateServiceCommand(serviceId, name, description, status);
    }

    // ==================== 기본값 접근자 ====================

    public static Long defaultServiceId() {
        return DEFAULT_SERVICE_ID;
    }

    public static String defaultServiceCode() {
        return DEFAULT_SERVICE_CODE;
    }

    public static String defaultName() {
        return DEFAULT_NAME;
    }

    public static String defaultDescription() {
        return DEFAULT_DESCRIPTION;
    }
}
