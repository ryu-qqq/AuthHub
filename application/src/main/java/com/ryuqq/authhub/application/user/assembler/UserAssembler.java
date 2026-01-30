package com.ryuqq.authhub.application.user.assembler;

import com.ryuqq.authhub.application.user.dto.response.UserPageResult;
import com.ryuqq.authhub.application.user.dto.response.UserResult;
import com.ryuqq.authhub.domain.user.aggregate.User;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * UserAssembler - Domain → Result 변환
 *
 * <p>Domain Aggregate를 Result DTO로 변환합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Component} 어노테이션
 *   <li>Domain → Result 변환만 (toDomain 금지!)
 *   <li>Port/Repository 의존 금지
 *   <li>비즈니스 로직 금지
 *   <li>Getter 체이닝 금지 (Law of Demeter)
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class UserAssembler {

    /**
     * Domain → Result 변환 (단건)
     *
     * @param user User Domain
     * @return UserResult DTO
     */
    public UserResult toResult(User user) {
        return new UserResult(
                user.userIdValue(),
                user.organizationIdValue(),
                user.identifierValue(),
                user.phoneNumberValue(),
                user.statusValue(),
                user.createdAt(),
                user.updatedAt());
    }

    /**
     * Domain → Result 변환 (목록)
     *
     * @param users User Domain 목록
     * @return UserResult DTO 목록
     */
    public List<UserResult> toResultList(List<User> users) {
        if (users == null || users.isEmpty()) {
            return List.of();
        }
        return users.stream().map(this::toResult).toList();
    }

    /**
     * Domain 목록 + 페이징 정보 → UserPageResult 변환
     *
     * @param users User Domain 목록
     * @param page 현재 페이지 번호
     * @param size 페이지 크기
     * @param totalElements 전체 요소 수
     * @return UserPageResult
     */
    public UserPageResult toPageResult(List<User> users, int page, int size, long totalElements) {
        List<UserResult> content = toResultList(users);
        return UserPageResult.of(content, page, size, totalElements);
    }
}
