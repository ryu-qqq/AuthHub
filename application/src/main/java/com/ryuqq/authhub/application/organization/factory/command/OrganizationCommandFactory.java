package com.ryuqq.authhub.application.organization.factory.command;

import com.ryuqq.authhub.application.organization.dto.command.CreateOrganizationCommand;
import com.ryuqq.authhub.domain.organization.aggregate.Organization;
import com.ryuqq.authhub.domain.organization.vo.OrganizationName;
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

    public OrganizationCommandFactory(Clock clock) {
        this.clock = clock;
    }

    /**
     * CreateOrganizationCommand → Organization 변환
     *
     * @param command 조직 생성 Command
     * @return 새로운 Organization 인스턴스 (ID 미할당)
     */
    public Organization create(CreateOrganizationCommand command) {
        return Organization.create(
                TenantId.of(command.tenantId()), OrganizationName.of(command.name()), clock);
    }
}
