package com.ryuqq.authhub.application.identity.port.out;

import com.ryuqq.authhub.domain.identity.organization.Organization;

/**
 * SaveOrganization Port - Organization 저장 Port Interface.
 *
 * <p>Application Layer에서 Organization Aggregate를 영속성 계층에 저장하기 위한 Port입니다.
 * Persistence Adapter는 이 Port를 구현하여 MySQL 데이터베이스에 저장합니다.</p>
 *
 * <p><strong>헥사고날 아키텍처:</strong></p>
 * <ul>
 *   <li>Application Layer가 의존성 역전을 위해 정의한 Port Interface</li>
 *   <li>Persistence Adapter는 이 인터페이스를 구현 (Adapter-out-persistence layer)</li>
 *   <li>Domain Object (Organization)를 직접 반환 - Persistence Object 변환은 Adapter에서 처리</li>
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙 준수:</strong></p>
 * <ul>
 *   <li>✅ Lombok 금지 - Plain Java Interface 사용</li>
 *   <li>✅ Javadoc 완비 - @author, @since 포함</li>
 *   <li>✅ Domain Object 반환 - DTO 변환 없음</li>
 *   <li>✅ 책임 명확화 - 저장만 담당, 조회는 별도 Port</li>
 * </ul>
 *
 * <p><strong>구현 예시:</strong></p>
 * <pre>
 * {@code
 * @Component
 * public class OrganizationPersistenceAdapter implements SaveOrganizationPort {
 *     private final OrganizationJpaRepository jpaRepository;
 *
 *     @Override
 *     public Organization save(Organization organization) {
 *         OrganizationJpaEntity entity = OrganizationMapper.toEntity(organization);
 *         OrganizationJpaEntity savedEntity = jpaRepository.save(entity);
 *         return OrganizationMapper.toDomain(savedEntity);
 *     }
 * }
 * }
 * </pre>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
public interface SaveOrganizationPort {

    /**
     * Organization Aggregate를 저장합니다.
     *
     * <p>신규 Organization을 생성하거나 기존 Organization을 업데이트합니다.
     * 영속성 기술의 경우 ID 존재 여부로 INSERT/UPDATE를 자동 판단합니다.</p>
     *
     * @param organization 저장할 Organization Aggregate (null 불가)
     * @return 저장된 Organization Aggregate (ID가 할당된 상태)
     * @throws IllegalArgumentException organization이 null인 경우
     * @throws RuntimeException 조직명 중복 등 데이터 무결성 제약 조건 위반 시
     * @author AuthHub Team
     * @since 1.0.0
     */
    Organization save(Organization organization);
}
