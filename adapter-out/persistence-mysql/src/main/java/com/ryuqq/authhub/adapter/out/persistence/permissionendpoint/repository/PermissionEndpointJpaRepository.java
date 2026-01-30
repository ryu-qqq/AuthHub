package com.ryuqq.authhub.adapter.out.persistence.permissionendpoint.repository;

import com.ryuqq.authhub.adapter.out.persistence.permissionendpoint.entity.PermissionEndpointJpaEntity;
import com.ryuqq.authhub.domain.permissionendpoint.vo.HttpMethod;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * PermissionEndpointJpaRepository - PermissionEndpoint JPA Repository
 *
 * <p>Spring Data JPA 기반 기본 CRUD 작업을 제공합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>JpaRepository만 상속 (QueryDSL 별도)
 *   <li>Native Query 사용 금지
 *   <li>카운트/존재 여부 쿼리만 허용
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface PermissionEndpointJpaRepository
        extends JpaRepository<PermissionEndpointJpaEntity, Long> {

    /**
     * URL 패턴과 HTTP 메서드 조합으로 존재 여부 확인
     *
     * @param urlPattern URL 패턴
     * @param httpMethod HTTP 메서드
     * @return 존재 여부
     */
    boolean existsByUrlPatternAndHttpMethodAndDeletedAtIsNull(
            String urlPattern, HttpMethod httpMethod);

    /**
     * URL 패턴과 HTTP 메서드로 조회
     *
     * @param urlPattern URL 패턴
     * @param httpMethod HTTP 메서드
     * @return PermissionEndpointJpaEntity (Optional)
     */
    Optional<PermissionEndpointJpaEntity> findByUrlPatternAndHttpMethodAndDeletedAtIsNull(
            String urlPattern, HttpMethod httpMethod);

    /**
     * Permission ID로 연결된 모든 엔드포인트 조회
     *
     * @param permissionId 권한 ID
     * @return 엔드포인트 목록
     */
    List<PermissionEndpointJpaEntity> findAllByPermissionIdAndDeletedAtIsNull(Long permissionId);
}
