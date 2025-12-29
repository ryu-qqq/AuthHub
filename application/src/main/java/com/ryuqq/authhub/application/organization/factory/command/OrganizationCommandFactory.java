package com.ryuqq.authhub.application.organization.factory.command;

import com.ryuqq.authhub.application.organization.dto.command.CreateOrganizationCommand;
import com.ryuqq.authhub.domain.common.util.UuidHolder;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.identifier.OrganizationId;
import com.ryuqq.authhub.domain.organization.vo.OrganizationName;
import com.ryuqq.authhub.domain.organization.vo.OrganizationStatus;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import java.time.Clock;
import org.springframework.stereotype.Component;

/**
 * OrganizationCommandFactory - Command → Domain 변환
 *
 * <p>Command DTO를 Domain Aggregate로 변환합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Component} 어노테이션 ({@code @Service} 아님)
 *   <li>{@code create*()} 메서드 네이밍
 *   <li>순수 변환만 수행 (비즈니스 로직 금지)
 *   <li>Port 호출 금지 (조회 금지)
 *   <li>{@code @Transactional} 금지
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class OrganizationCommandFactory {

    private final Clock clock;
    private final UuidHolder uuidHolder;

    public OrganizationCommandFactory(Clock clock, UuidHolder uuidHolder) {
        this.clock = clock;
        this.uuidHolder = uuidHolder;
    }

    /**
     * CreateOrganizationCommand → Organization 변환
     *
     * <p>UUIDv7 기반 식별자를 생성하여 Organization을 생성합니다.
     *
     * @param command 조직 생성 Command
     * @return 새로운 Organization 인스턴스 (UUIDv7 ID 할당)
     */
    public Organization create(CreateOrganizationCommand command) {
        OrganizationId organizationId = OrganizationId.forNew(uuidHolder.random());
        return Organization.create(
                organizationId,
                TenantId.of(command.tenantId()),
                OrganizationName.of(command.name()),
                clock);
    }

    /**
     * String → OrganizationName 변환
     *
     * <p>Validator에서 사용할 OrganizationName VO를 생성합니다.
     *
     * @param name 조직 이름 문자열
     * @return OrganizationName VO
     */
    public OrganizationName toName(String name) {
        return OrganizationName.of(name);
    }

    /**
     * 조직 이름 변경 적용
     *
     * @param organization 이름을 변경할 조직
     * @param newName 새로운 조직 이름
     * @return 이름이 변경된 새로운 Organization 인스턴스
     */
    public Organization applyNameChange(Organization organization, OrganizationName newName) {
        return organization.changeName(newName, clock);
    }

    /**
     * 조직 상태 변경 적용
     *
     * <p>대상 상태에 따라 적절한 도메인 메서드를 호출합니다. DELETED 상태 변경은 DeleteOrganizationUseCase를 사용해야 합니다.
     *
     * @param organization 상태를 변경할 조직
     * @param targetStatus 목표 상태 문자열 (ACTIVE, INACTIVE)
     * @return 상태가 변경된 새로운 Organization 인스턴스
     * @throws IllegalArgumentException DELETED 상태로 변경 시도 시
     */
    public Organization applyStatusChange(Organization organization, String targetStatus) {
        OrganizationStatus status = OrganizationStatus.valueOf(targetStatus);
        return switch (status) {
            case ACTIVE -> organization.activate(clock);
            case INACTIVE -> organization.deactivate(clock);
            case DELETED ->
                    throw new IllegalArgumentException(
                            "DELETED 상태로의 변경은 DeleteOrganizationUseCase를 사용하세요");
        };
    }
}
