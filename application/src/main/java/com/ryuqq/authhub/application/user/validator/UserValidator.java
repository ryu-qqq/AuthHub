package com.ryuqq.authhub.application.user.validator;

import com.ryuqq.authhub.application.user.manager.UserReadManager;
import com.ryuqq.authhub.application.user.port.out.client.PasswordEncoderClient;
import com.ryuqq.authhub.domain.auth.exception.InvalidCredentialsException;
import com.ryuqq.authhub.domain.organization.id.OrganizationId;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.exception.DuplicateUserIdentifierException;
import com.ryuqq.authhub.domain.user.exception.DuplicateUserPhoneNumberException;
import com.ryuqq.authhub.domain.user.exception.InvalidPasswordException;
import com.ryuqq.authhub.domain.user.exception.UserNotActiveException;
import com.ryuqq.authhub.domain.user.id.UserId;
import com.ryuqq.authhub.domain.user.vo.Identifier;
import com.ryuqq.authhub.domain.user.vo.PhoneNumber;
import org.springframework.stereotype.Component;

/**
 * UserValidator - 사용자 비즈니스 규칙 검증
 *
 * <p>조회가 필요한 검증 로직을 담당합니다.
 *
 * <p>VAL-001: Validator는 @Component 어노테이션 사용.
 *
 * <p>VAL-002: Validator는 {Domain}Validator 네이밍 사용.
 *
 * <p>VAL-003: Validator는 ReadManager만 의존.
 *
 * <p>VAL-004: Validator는 void 반환, 실패 시 DomainException.
 *
 * <p>VAL-005: Validator 메서드는 validateXxx() 또는 checkXxx() 사용.
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class UserValidator {

    private final UserReadManager readManager;
    private final PasswordEncoderClient passwordEncoderClient;

    public UserValidator(UserReadManager readManager, PasswordEncoderClient passwordEncoderClient) {
        this.readManager = readManager;
        this.passwordEncoderClient = passwordEncoderClient;
    }

    /**
     * User 조회 및 존재 여부 검증
     *
     * @param id User ID (VO)
     * @return User 조회된 도메인 객체
     * @throws com.ryuqq.authhub.domain.user.exception.UserNotFoundException 존재하지 않는 경우
     */
    public User findExistingOrThrow(UserId id) {
        return readManager.findById(id);
    }

    /**
     * 로그인용 사용자 조회 및 활성 상태 검증
     *
     * <p>사용자 조회와 활성 상태 검증을 한 번에 수행합니다.
     *
     * <p><strong>보안 고려사항:</strong>
     *
     * <ul>
     *   <li>사용자 미존재 시 InvalidCredentialsException (정보 노출 방지)
     *   <li>비활성 사용자는 UserNotActiveException (명확한 상태 전달)
     * </ul>
     *
     * @param identifier 사용자 식별자 (이메일 등)
     * @return User 활성 상태인 도메인 객체
     * @throws InvalidCredentialsException 사용자가 존재하지 않는 경우
     * @throws UserNotActiveException 사용자가 활성 상태가 아닌 경우
     */
    public User findActiveUserByIdentifierOrThrow(Identifier identifier) {
        User user =
                readManager
                        .findByIdentifier(identifier)
                        .orElseThrow(InvalidCredentialsException::new);

        if (!user.isActive()) {
            throw new UserNotActiveException(user.getUserId(), user.getStatus());
        }

        return user;
    }

    /**
     * 조직 내 식별자 중복 검증
     *
     * @param organizationId 조직 ID
     * @param identifier 검증할 식별자
     * @throws DuplicateUserIdentifierException 중복 시
     */
    public void validateIdentifierNotDuplicated(
            OrganizationId organizationId, Identifier identifier) {
        if (readManager.existsByOrganizationIdAndIdentifier(organizationId, identifier)) {
            throw new DuplicateUserIdentifierException(identifier);
        }
    }

    /**
     * 조직 내 전화번호 중복 검증
     *
     * @param organizationId 조직 ID
     * @param phoneNumber 검증할 전화번호
     * @throws DuplicateUserPhoneNumberException 중복 시
     */
    public void validatePhoneNumberNotDuplicated(
            OrganizationId organizationId, PhoneNumber phoneNumber) {
        if (phoneNumber == null) {
            return;
        }
        if (readManager.existsByOrganizationIdAndPhoneNumber(organizationId, phoneNumber)) {
            throw new DuplicateUserPhoneNumberException(phoneNumber);
        }
    }

    /**
     * 사용자 조회 및 현재 비밀번호 검증
     *
     * <p>사용자 존재 여부와 비밀번호 일치를 한 번에 검증합니다.
     *
     * @param id User ID (VO)
     * @param currentPassword 입력된 현재 비밀번호
     * @return User 검증된 도메인 객체
     * @throws com.ryuqq.authhub.domain.user.exception.UserNotFoundException 존재하지 않는 경우
     * @throws InvalidPasswordException 비밀번호 불일치 시
     */
    public User validatePasswordAndFindUser(UserId id, String currentPassword) {
        User user = readManager.findById(id);
        if (!passwordEncoderClient.matches(currentPassword, user.getHashedPassword())) {
            throw new InvalidPasswordException(user.identifierValue());
        }
        return user;
    }
}
