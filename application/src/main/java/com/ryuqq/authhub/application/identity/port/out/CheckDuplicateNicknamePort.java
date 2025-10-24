package com.ryuqq.authhub.application.identity.port.out;

/**
 * CheckDuplicateNickname Port - Nickname 중복 확인 Out Port.
 *
 * <p>UserProfile의 Nickname 중복 여부를 확인하는 Out Port입니다.
 * Adapter Layer의 Repository에서 구현됩니다.</p>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Out Port는 단일 책임 원칙 (SRP) 준수</li>
 *   <li>✅ Lombok 금지 - Plain Java Interface 사용</li>
 *   <li>✅ Javadoc 완비 - @author, @since 포함</li>
 * </ul>
 *
 * <p><strong>구현 위치:</strong></p>
 * <ul>
 *   <li>adapter-out/persistence-jpa/UserProfileRepositoryAdapter</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
public interface CheckDuplicateNicknamePort {

    /**
     * Nickname 중복 여부를 확인합니다.
     *
     * @param nickname 닉네임
     * @return 중복이면 true, 아니면 false
     * @author AuthHub Team
     * @since 1.0.0
     */
    boolean existsByNickname(String nickname);
}
