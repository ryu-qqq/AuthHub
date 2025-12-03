package com.ryuqq.authhub.application.user.dto.response;

import java.time.Instant;
import java.util.UUID;

/**
 * Create User Response DTO
 *
 * <p>사용자 생성 결과를 클라이언트에게 반환하는 최소 응답 객체입니다.
 *
 * <p><strong>최소 응답 원칙:</strong>
 *
 * <ul>
 *   <li>생성된 리소스의 ID만 반환
 *   <li>불필요한 정보 노출 방지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public record CreateUserResponse(UUID userId, Instant createdAt) {}
