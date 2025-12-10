package com.ryuqq.authhub.domain.user.exception;

import com.ryuqq.authhub.domain.common.exception.DomainException;
import java.util.Map;
import java.util.UUID;

/**
 * DuplicateUserIdentifierException - 중복 사용자 식별자 예외
 *
 * <p>이미 존재하는 식별자로 사용자 생성 시도 시 발생합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>DomainException 상속 필수
 *   <li>ErrorCode 사용 필수
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public class DuplicateUserIdentifierException extends DomainException {

    public DuplicateUserIdentifierException(UUID tenantId, UUID organizationId, String identifier) {
        super(
                UserErrorCode.DUPLICATE_USER_IDENTIFIER,
                Map.of(
                        "tenantId",
                        tenantId,
                        "organizationId",
                        organizationId,
                        "identifier",
                        identifier));
    }

    public DuplicateUserIdentifierException(String identifier) {
        super(UserErrorCode.DUPLICATE_USER_IDENTIFIER, Map.of("identifier", identifier));
    }
}
