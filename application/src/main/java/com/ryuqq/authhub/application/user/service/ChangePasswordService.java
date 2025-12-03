package com.ryuqq.authhub.application.user.service;

import com.ryuqq.authhub.application.common.component.PasswordHasher;
import com.ryuqq.authhub.application.user.component.PasswordChangeValidator;
import com.ryuqq.authhub.application.user.component.UserUpdater;
import com.ryuqq.authhub.application.user.dto.command.ChangePasswordCommand;
import com.ryuqq.authhub.application.user.manager.UserManager;
import com.ryuqq.authhub.application.user.port.in.ChangePasswordUseCase;
import com.ryuqq.authhub.application.user.port.out.query.UserQueryPort;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.exception.UserNotFoundException;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import com.ryuqq.authhub.domain.user.vo.Password;
import org.springframework.stereotype.Service;

/**
 * ChangePasswordService - 비밀번호 변경 UseCase 구현체
 *
 * <p>사용자 비밀번호 변경을 orchestration합니다.
 *
 * <p><strong>비밀번호 변경 시나리오:</strong>
 *
 * <ul>
 *   <li>일반 변경 (verified=false): Validator에서 현재 비밀번호 검증
 *   <li>재설정 (verified=true): 본인 인증 완료, 검증 건너뜀
 * </ul>
 *
 * <p><strong>구조:</strong>
 *
 * <ul>
 *   <li>Service → Manager → Port (트랜잭션은 Manager에서 관리)
 *   <li>Validator로 비밀번호 검증 위임
 *   <li>Updater로 Domain 업데이트 위임 (Clock 처리 포함)
 *   <li>ACTIVE 상태 검증은 Domain에서 수행
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>Transaction 내 외부 API 호출 금지
 *   <li>Command null 체크 금지 (외부 레이어에서 검증됨)
 *   <li>비즈니스 로직 Service 노출 금지 (Domain/Component에 위임)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
public class ChangePasswordService implements ChangePasswordUseCase {

    private final UserQueryPort userQueryPort;
    private final UserManager userManager;
    private final UserUpdater userUpdater;
    private final PasswordChangeValidator passwordChangeValidator;
    private final PasswordHasher passwordHasher;

    public ChangePasswordService(
            UserQueryPort userQueryPort,
            UserManager userManager,
            UserUpdater userUpdater,
            PasswordChangeValidator passwordChangeValidator,
            PasswordHasher passwordHasher) {
        this.userQueryPort = userQueryPort;
        this.userManager = userManager;
        this.userUpdater = userUpdater;
        this.passwordChangeValidator = passwordChangeValidator;
        this.passwordHasher = passwordHasher;
    }

    @Override
    public void execute(ChangePasswordCommand command) {
        // 1. User 조회
        UserId userId = UserId.of(command.userId());
        User existingUser =
                userQueryPort
                        .findById(userId)
                        .orElseThrow(() -> new UserNotFoundException(command.userId()));

        // 2. 비밀번호 검증 (Validator에 위임 - verified 여부에 따라 조건부 검증)
        passwordChangeValidator.validate(command, existingUser);

        // 3. 새 비밀번호 해싱
        String newHashedPassword = passwordHasher.hash(command.newPassword());
        Password newPassword = Password.ofHashed(newHashedPassword);

        // 4. 비밀번호 변경 (Updater에 위임 - Clock 처리 포함, ACTIVE 검증은 Domain에서)
        User updatedUser = userUpdater.changePassword(existingUser, newPassword);

        // 5. 영속화 (Manager 경유 - 트랜잭션 관리)
        userManager.persist(updatedUser);
    }
}
