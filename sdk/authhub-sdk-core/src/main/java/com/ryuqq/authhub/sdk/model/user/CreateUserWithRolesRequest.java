package com.ryuqq.authhub.sdk.model.user;

import java.util.List;
import java.util.Objects;

/**
 * 사용자 생성 + 역할 할당 요청.
 *
 * <p>사용자를 생성하고 선택적으로 SERVICE scope Role을 할당합니다.
 *
 * @param organizationId 소속 조직 ID (필수)
 * @param identifier 로그인 식별자 (필수, 이메일 또는 사용자명)
 * @param phoneNumber 전화번호 (선택)
 * @param password 비밀번호 (필수)
 * @param serviceCode 서비스 코드 (선택, 예: SVC_STORE)
 * @param roleNames 역할 이름 목록 (선택, 예: ["ADMIN"])
 */
public record CreateUserWithRolesRequest(
        String organizationId,
        String identifier,
        String phoneNumber,
        String password,
        String serviceCode,
        List<String> roleNames) {

    public CreateUserWithRolesRequest {
        Objects.requireNonNull(organizationId, "organizationId must not be null");
        Objects.requireNonNull(identifier, "identifier must not be null");
        Objects.requireNonNull(password, "password must not be null");
        if (organizationId.isBlank()) {
            throw new IllegalArgumentException("organizationId must not be blank");
        }
        if (identifier.isBlank()) {
            throw new IllegalArgumentException("identifier must not be blank");
        }
        if (password.isBlank()) {
            throw new IllegalArgumentException("password must not be blank");
        }
    }
}
