package com.ryuqq.authhub.application.user.assembler;

import com.ryuqq.authhub.application.user.dto.response.UserResponse;
import com.ryuqq.authhub.domain.user.aggregate.User;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * UserAssembler - 사용자 응답 어셈블러
 *
 * <p>Domain → Response DTO 변환을 담당합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Component} 어노테이션
 *   <li>Domain → DTO 단방향 변환만 수행
 *   <li>비즈니스 로직 금지
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class UserAssembler {

    /**
     * User → UserResponse 변환
     *
     * @param user 도메인 객체
     * @return 응답 DTO
     */
    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.userIdValue(),
                user.tenantIdValue(),
                user.organizationIdValue(),
                user.getIdentifier(),
                user.getUserStatus().name(),
                user.getCreatedAt(),
                user.getUpdatedAt());
    }

    /**
     * User 목록 → UserResponse 목록 변환
     *
     * @param users 도메인 객체 목록
     * @return 응답 DTO 목록
     */
    public List<UserResponse> toResponseList(List<User> users) {
        return users.stream().map(this::toResponse).toList();
    }
}
