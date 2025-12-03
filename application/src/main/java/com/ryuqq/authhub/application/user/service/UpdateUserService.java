package com.ryuqq.authhub.application.user.service;

import com.ryuqq.authhub.application.user.assembler.UserQueryAssembler;
import com.ryuqq.authhub.application.user.component.UserUpdater;
import com.ryuqq.authhub.application.user.component.UserValidator;
import com.ryuqq.authhub.application.user.dto.command.UpdateUserCommand;
import com.ryuqq.authhub.application.user.dto.response.UserResponse;
import com.ryuqq.authhub.application.user.manager.UserManager;
import com.ryuqq.authhub.application.user.port.in.UpdateUserUseCase;
import com.ryuqq.authhub.application.user.port.out.query.UserQueryPort;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.exception.UserNotFoundException;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import com.ryuqq.authhub.domain.user.vo.UserProfile;
import org.springframework.stereotype.Service;

/**
 * UpdateUserService - 사용자 정보 수정 UseCase 구현체
 *
 * <p>사용자 프로필 정보(이름, 전화번호) 수정을 orchestration합니다.
 *
 * <p><strong>구조:</strong>
 *
 * <ul>
 *   <li>Service → Manager → Port (트랜잭션은 Manager에서 관리)
 *   <li>Validator로 검증 로직 위임
 *   <li>Updater로 Domain 업데이트 위임 (Clock 처리 포함)
 *   <li>QueryAssembler로 Response 변환 로직 위임
 *   <li>비즈니스 로직은 Domain 객체에 위임
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Transaction 내 외부 API 호출 금지
 *   <li>Command null 체크 금지 (외부 레이어에서 검증됨)
 *   <li>비즈니스 로직 Service 노출 금지 (Domain에 위임)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class UpdateUserService implements UpdateUserUseCase {

    private final UserQueryPort userQueryPort;
    private final UserManager userManager;
    private final UserValidator userValidator;
    private final UserUpdater userUpdater;
    private final UserQueryAssembler userQueryAssembler;

    public UpdateUserService(
            UserQueryPort userQueryPort,
            UserManager userManager,
            UserValidator userValidator,
            UserUpdater userUpdater,
            UserQueryAssembler userQueryAssembler) {
        this.userQueryPort = userQueryPort;
        this.userManager = userManager;
        this.userValidator = userValidator;
        this.userUpdater = userUpdater;
        this.userQueryAssembler = userQueryAssembler;
    }

    @Override
    public UserResponse execute(UpdateUserCommand command) {
        // 1. 기존 User 조회
        UserId userId = UserId.of(command.userId());
        User existingUser =
                userQueryPort
                        .findById(userId)
                        .orElseThrow(() -> new UserNotFoundException(command.userId()));

        // 2. Tenant별 phoneNumber 중복 검증 (Validator에 위임)
        userValidator.validatePhoneNumberForUpdate(
                existingUser.getTenantId(), command.phoneNumber(), existingUser.getUserId());

        // 3. Domain 업데이트 (Updater에 위임 - Clock 처리 포함, ACTIVE 검증은 Domain에서)
        UserProfile newProfile = UserProfile.of(command.name(), command.phoneNumber());
        User updatedUser = userUpdater.updateProfile(existingUser, newProfile);

        // 4. 영속화 (Manager 경유 - 트랜잭션 관리)
        userManager.persist(updatedUser);

        // 5. Response 변환 및 반환
        return userQueryAssembler.toResponse(updatedUser);
    }
}
