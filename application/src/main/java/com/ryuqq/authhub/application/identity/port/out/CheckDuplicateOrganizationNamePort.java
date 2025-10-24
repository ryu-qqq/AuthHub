package com.ryuqq.authhub.application.identity.port.out;

/**
 * CheckDuplicateOrganizationName Port - 조직명 중복 확인 Port Interface.
 *
 * <p>Application Layer에서 조직명 중복 여부를 확인하기 위한 Port입니다.
 * Persistence Adapter는 이 Port를 구현하여 MySQL 데이터베이스에서 조직명 존재 여부를 확인합니다.</p>
 *
 * <p><strong>헥사고날 아키텍처:</strong></p>
 * <ul>
 *   <li>Application Layer가 의존성 역전을 위해 정의한 Port Interface</li>
 *   <li>Persistence Adapter는 이 인터페이스를 구현 (Adapter-out-persistence layer)</li>
 *   <li>단순 조회 - 트랜잭션 필요 없음 (ReadOnly)</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Lombok 금지 - Plain Java Interface 사용</li>
 *   <li>✅ Javadoc 완비 - @author, @since 포함</li>
 *   <li>✅ 책임 명확화 - 중복 확인만 담당</li>
 *   <li>✅ 단순 boolean 반환 - 불필요한 객체 생성 없음</li>
 * </ul>
 *
 * <p><strong>Race Condition 주의사항:</strong></p>
 * <ul>
 *   <li>중복 확인 후 생성 사이에 동시 요청 가능</li>
 *   <li>Database Unique Constraint로 최종 방어</li>
 *   <li>QueryDSL 또는 JPA로 효율적 조회 구현 권장</li>
 * </ul>
 *
 * <p><strong>구현 예시:</strong></p>
 * <pre>
 * {@code
 * @Component
 * public class OrganizationPersistenceAdapter implements CheckDuplicateOrganizationNamePort {
 *     private final OrganizationJpaRepository jpaRepository;
 *
 *     @Override
 *     public boolean existsByName(String organizationName) {
 *         return jpaRepository.existsByName(organizationName);
 *     }
 * }
 * }
 * </pre>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
public interface CheckDuplicateOrganizationNamePort {

    /**
     * 조직명 중복 여부를 확인합니다.
     *
     * <p>대소문자 구분 없이 동일한 조직명이 존재하는지 확인합니다.
     * 삭제된 조직(DELETED 상태)은 제외하고 확인합니다.</p>
     *
     * @param organizationName 확인할 조직명 (null 불가, 공백 불가)
     * @return 조직명이 이미 존재하면 true, 그렇지 않으면 false
     * @throws IllegalArgumentException organizationName이 null이거나 공백인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    boolean existsByName(String organizationName);
}
