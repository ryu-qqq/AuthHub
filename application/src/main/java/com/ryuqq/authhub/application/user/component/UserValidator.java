package com.ryuqq.authhub.application.user.component;

import com.ryuqq.authhub.application.user.port.out.query.UserQueryPort;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import com.ryuqq.authhub.domain.user.exception.PhoneNumberAlreadyExistsException;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import com.ryuqq.authhub.domain.user.vo.PhoneNumber;
import org.springframework.stereotype.Component;

/**
 * UserValidator - User 관련 검증 컴포넌트
 *
 * <p>User 도메인 관련 검증 로직을 위임받아 처리합니다.
 *
 * <p><strong>검증 범위:</strong>
 *
 * <ul>
 *   <li>Tenant 내 전화번호 중복 검증
 *   <li>기타 User 관련 비즈니스 검증
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class UserValidator {

    private final UserQueryPort userQueryPort;

    public UserValidator(UserQueryPort userQueryPort) {
        this.userQueryPort = userQueryPort;
    }

    /**
     * 신규 사용자 생성 시 Tenant 내 전화번호 중복 검증
     *
     * @param tenantId Tenant ID
     * @param phoneNumber 전화번호 (null이면 검증 skip)
     * @throws PhoneNumberAlreadyExistsException 동일 Tenant 내 중복 전화번호 존재 시
     */
    public void validatePhoneNumberForCreate(TenantId tenantId, String phoneNumber) {
        PhoneNumber phone = PhoneNumber.of(phoneNumber);
        boolean isDuplicate =
                userQueryPort.existsByTenantIdAndPhoneNumberExcludingUser(
                        tenantId, phone, null // 신규 사용자이므로 제외할 userId 없음
                        );

        if (isDuplicate) {
            throw new PhoneNumberAlreadyExistsException(tenantId.value(), phoneNumber);
        }
    }

    /**
     * 기존 사용자 수정 시 Tenant 내 전화번호 중복 검증 (본인 제외)
     *
     * @param tenantId Tenant ID
     * @param phoneNumber 전화번호 (null이면 검증 skip)
     * @param excludeUserId 제외할 사용자 ID (본인)
     * @throws PhoneNumberAlreadyExistsException 동일 Tenant 내 중복 전화번호 존재 시
     */
    public void validatePhoneNumberForUpdate(
            TenantId tenantId, String phoneNumber, UserId excludeUserId) {
        PhoneNumber phone = PhoneNumber.of(phoneNumber);
        boolean isDuplicate =
                userQueryPort.existsByTenantIdAndPhoneNumberExcludingUser(
                        tenantId, phone, excludeUserId);

        if (isDuplicate) {
            throw new PhoneNumberAlreadyExistsException(tenantId.value(), phoneNumber);
        }
    }
}
