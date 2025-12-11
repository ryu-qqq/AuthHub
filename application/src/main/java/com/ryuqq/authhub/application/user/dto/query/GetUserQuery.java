package com.ryuqq.authhub.application.user.dto.query;

import java.util.UUID;

/**
 * GetUserQuery - 사용자 단건 조회 Query DTO
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>순수 Java Record (jakarta.validation 금지)
 *   <li>Lombok 금지
 *   <li>비즈니스 로직 금지 (Domain 책임)
 * </ul>
 *
 * @param userId 조회할 사용자 ID
 * @author development-team
 * @since 1.0.0
 */
public record GetUserQuery(UUID userId) {}
