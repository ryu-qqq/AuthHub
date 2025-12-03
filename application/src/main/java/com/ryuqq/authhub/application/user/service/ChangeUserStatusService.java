package com.ryuqq.authhub.application.user.service;

import com.ryuqq.authhub.application.user.component.UserUpdater;
import com.ryuqq.authhub.application.user.dto.command.ChangeUserStatusCommand;
import com.ryuqq.authhub.application.user.manager.UserManager;
import com.ryuqq.authhub.application.user.port.in.ChangeUserStatusUseCase;
import com.ryuqq.authhub.application.user.port.out.query.UserQueryPort;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.exception.UserNotFoundException;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import com.ryuqq.authhub.domain.user.vo.UserStatus;
import org.springframework.stereotype.Service;

/**
 * ChangeUserStatusService - 사용자 상태 변경 UseCase 구현체
 *
 * <p>사용자 상태 변경을 orchestration합니다.
 *
 * <p><strong>구조:</strong>
 *
 * <ul>
 *   <li>Service → Manager → Port (트랜잭션은 Manager에서 관리)
 *   <li>Updater로 Domain 업데이트 위임 (Clock 처리 포함)
 *   <li>상태 전환 검증은 Domain에서 수행
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
public class ChangeUserStatusService implements ChangeUserStatusUseCase {

    private final UserQueryPort userQueryPort;
    private final UserManager userManager;
    private final UserUpdater userUpdater;

    public ChangeUserStatusService(
            UserQueryPort userQueryPort, UserManager userManager, UserUpdater userUpdater) {
        this.userQueryPort = userQueryPort;
        this.userManager = userManager;
        this.userUpdater = userUpdater;
    }

    @Override
    public void execute(ChangeUserStatusCommand command) {
        // 1. User 조회
        UserId userId = UserId.of(command.userId());
        User existingUser =
                userQueryPort
                        .findById(userId)
                        .orElseThrow(() -> new UserNotFoundException(userId.value()));

        // 2. 상태 변경 (Updater에 위임 - Clock 처리 포함, 상태 전환 검증은 Domain에서)
        UserStatus targetStatus = UserStatus.valueOf(command.targetStatus());
        User updatedUser = userUpdater.changeStatus(existingUser, targetStatus);

        // 3. 영속화 (Manager 경유 - 트랜잭션 관리)
        userManager.persist(updatedUser);
    }
}
