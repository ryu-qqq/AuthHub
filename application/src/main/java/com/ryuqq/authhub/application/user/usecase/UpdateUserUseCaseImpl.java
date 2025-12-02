package com.ryuqq.authhub.application.user.usecase;

import com.ryuqq.authhub.application.user.assembler.UserAssembler;
import com.ryuqq.authhub.application.user.dto.command.UpdateUserCommand;
import com.ryuqq.authhub.application.user.dto.response.UserResponse;
import com.ryuqq.authhub.application.user.port.out.command.UserPersistencePort;
import com.ryuqq.authhub.application.user.port.out.query.UserQueryPort;
import com.ryuqq.authhub.domain.common.Clock;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.identifier.UserId;
import com.ryuqq.authhub.domain.user.vo.UserProfile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * UpdateUserUseCaseImpl - 사용자 정보 수정 UseCase 구현체
 *
 * <p>사용자 프로필 정보 수정 비즈니스 로직을 구현합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 * <ul>
 *   <li>Transaction 내 외부 API 호출 금지</li>
 *   <li>Domain 검증 후 영속화</li>
 *   <li>Response DTO로 변환 후 반환 (Assembler 사용)</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Service
@Transactional
public class UpdateUserUseCaseImpl implements UpdateUserUseCase {

    private final UserQueryPort userQueryPort;
    private final UserPersistencePort userPersistencePort;
    private final UserAssembler userAssembler;
    private final Clock clock;

    public UpdateUserUseCaseImpl(
            UserQueryPort userQueryPort,
            UserPersistencePort userPersistencePort,
            UserAssembler userAssembler,
            Clock clock) {
        this.userQueryPort = userQueryPort;
        this.userPersistencePort = userPersistencePort;
        this.userAssembler = userAssembler;
        this.clock = clock;
    }

    @Override
    public UserResponse execute(UpdateUserCommand command) {
        Objects.requireNonNull(command, "UpdateUserCommand는 null일 수 없습니다");

        if (command.userId() == null) {
            throw new IllegalArgumentException("userId는 null일 수 없습니다");
        }

        // 1. User 조회
        UserId userId = UserId.of(command.userId());
        User existingUser = userQueryPort.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User를 찾을 수 없습니다: " + userId));

        // 2. 삭제된 User 체크
        if (existingUser.isDeleted()) {
            throw new IllegalStateException("삭제된 User는 수정할 수 없습니다: " + userId);
        }

        // 3. 프로필 업데이트
        UserProfile newProfile = UserProfile.of(
                command.name(),
                command.nickname(),
                command.profileImageUrl()
        );
        User updatedUser = existingUser.updateProfile(newProfile, clock);

        // 4. 영속화
        userPersistencePort.persist(updatedUser);

        // 5. Response 반환
        return userAssembler.toResponse(updatedUser);
    }
}
