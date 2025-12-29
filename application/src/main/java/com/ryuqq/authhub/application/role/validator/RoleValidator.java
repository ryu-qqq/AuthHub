package com.ryuqq.authhub.application.role.validator;

import com.ryuqq.authhub.application.role.manager.query.RoleReadManager;
import com.ryuqq.authhub.domain.role.exception.DuplicateRoleNameException;
import com.ryuqq.authhub.domain.role.vo.RoleName;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import org.springframework.stereotype.Component;

/**
 * RoleValidator - 역할 비즈니스 규칙 검증
 *
 * <p>조회가 필요한 검증 로직을 담당합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Component} 어노테이션 ({@code @Service} 아님)
 *   <li>{@code validate*()} 메서드 네이밍
 *   <li>ReadManager만 의존 (Port 직접 호출 금지)
 *   <li>검증 실패 시 Domain Exception throw
 *   <li>{@code @Transactional} 금지 (ReadManager 책임)
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class RoleValidator {

    private final RoleReadManager readManager;

    public RoleValidator(RoleReadManager readManager) {
        this.readManager = readManager;
    }

    /**
     * 테넌트 내 역할 이름 중복 검증
     *
     * @param tenantId Tenant ID (null일 경우 GLOBAL 범위)
     * @param name 검증할 역할 이름
     * @throws DuplicateRoleNameException 중복 시
     */
    public void validateNameNotDuplicated(TenantId tenantId, RoleName name) {
        if (readManager.existsByTenantIdAndName(tenantId, name)) {
            if (tenantId != null) {
                throw new DuplicateRoleNameException(tenantId.value(), name.value());
            } else {
                throw new DuplicateRoleNameException(name.value());
            }
        }
    }
}
