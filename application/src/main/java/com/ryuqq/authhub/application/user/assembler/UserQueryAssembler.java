package com.ryuqq.authhub.application.user.assembler;

import com.ryuqq.authhub.application.user.dto.response.CreateUserResponse;
import com.ryuqq.authhub.application.user.dto.response.UserResponse;
import com.ryuqq.authhub.domain.user.aggregate.User;
import com.ryuqq.authhub.domain.user.vo.UserProfile;
import java.util.Objects;
import org.springframework.stereotype.Component;

/**
 * UserQueryAssembler - User Domain → Response DTO 변환 담당
 *
 * <p>도메인 객체를 응답 DTO로 변환합니다.
 *
 * <p><strong>규칙:</strong>
 *
 * <ul>
 *   <li>Domain → Response 변환 로직만 포함
 *   <li>순수 변환 로직만 포함 (비즈니스 로직 금지)
 *   <li>민감 정보 필터링 (credential 등 제외)
 *   <li>null-safe 변환
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class UserQueryAssembler {

    /**
     * User Domain을 UserResponse로 변환
     *
     * @param user User 도메인 객체
     * @return UserResponse DTO
     * @throws NullPointerException user가 null인 경우
     */
    public UserResponse toResponse(User user) {
        Objects.requireNonNull(user, "User는 null일 수 없습니다");

        UserProfile profile = user.getProfile();

        return new UserResponse(
                user.userIdValue(),
                user.tenantIdValue(),
                user.organizationIdValue(),
                user.userTypeValue(),
                user.statusValue(),
                profile.name(),
                profile.phoneNumberValue(),
                user.createdAt(),
                user.updatedAt());
    }

    /**
     * User Domain을 CreateUserResponse로 변환
     *
     * @param user User 도메인 객체
     * @return CreateUserResponse DTO
     * @throws NullPointerException user가 null인 경우
     */
    public CreateUserResponse toCreateResponse(User user) {
        Objects.requireNonNull(user, "User는 null일 수 없습니다");

        return new CreateUserResponse(user.userIdValue(), user.createdAt());
    }
}
