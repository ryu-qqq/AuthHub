package com.ryuqq.authhub.adapter.out.persistence.endpointpermission.repository;

import static com.ryuqq.authhub.adapter.out.persistence.endpointpermission.entity.QEndpointPermissionJpaEntity.endpointPermissionJpaEntity;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.authhub.adapter.out.persistence.endpointpermission.entity.EndpointPermissionJpaEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

/**
 * EndpointPermissionQueryDslRepository - 엔드포인트 권한 QueryDSL Repository (Query 전용)
 *
 * <p>QueryDSL 기반 조회 작업을 담당합니다.
 *
 * <p><strong>책임:</strong>
 *
 * <ul>
 *   <li>findByEndpointPermissionId() - UUID로 단건 조회
 *   <li>findByServiceNameAndPathAndMethod() - 서비스+경로+메서드로 조회
 *   <li>existsByServiceNameAndPathAndMethod() - 존재 여부 확인
 *   <li>findAllByServiceNameAndMethod() - 서비스+메서드로 목록 조회 (경로 매칭용)
 *   <li>search() - 조건 검색
 *   <li>count() - 조건 개수 조회
 * </ul>
 *
 * <p><strong>규칙:</strong>
 *
 * <ul>
 *   <li>Entity 반환 (Domain 변환은 Adapter에서)
 *   <li>삭제되지 않은 엔드포인트만 조회 (deleted = false)
 *   <li>비즈니스 로직 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Repository
public class EndpointPermissionQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public EndpointPermissionQueryDslRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    /**
     * UUID로 엔드포인트 권한 단건 조회
     *
     * @param endpointPermissionId 엔드포인트 권한 UUID
     * @return Optional<EndpointPermissionJpaEntity>
     */
    public Optional<EndpointPermissionJpaEntity> findByEndpointPermissionId(
            UUID endpointPermissionId) {
        EndpointPermissionJpaEntity result =
                queryFactory
                        .selectFrom(endpointPermissionJpaEntity)
                        .where(
                                endpointPermissionJpaEntity.endpointPermissionId.eq(
                                        endpointPermissionId),
                                endpointPermissionJpaEntity.deleted.eq(false))
                        .fetchOne();
        return Optional.ofNullable(result);
    }

    /**
     * 서비스명 + 경로 + HTTP 메서드로 단건 조회
     *
     * @param serviceName 서비스 이름
     * @param path 경로
     * @param method HTTP 메서드
     * @return Optional<EndpointPermissionJpaEntity>
     */
    public Optional<EndpointPermissionJpaEntity> findByServiceNameAndPathAndMethod(
            String serviceName, String path, String method) {
        EndpointPermissionJpaEntity result =
                queryFactory
                        .selectFrom(endpointPermissionJpaEntity)
                        .where(
                                endpointPermissionJpaEntity.serviceName.eq(serviceName),
                                endpointPermissionJpaEntity.path.eq(path),
                                endpointPermissionJpaEntity.method.eq(method),
                                endpointPermissionJpaEntity.deleted.eq(false))
                        .fetchOne();
        return Optional.ofNullable(result);
    }

    /**
     * 서비스명 + 경로 + HTTP 메서드 존재 여부 확인
     *
     * @param serviceName 서비스 이름
     * @param path 경로
     * @param method HTTP 메서드
     * @return 존재 여부
     */
    public boolean existsByServiceNameAndPathAndMethod(
            String serviceName, String path, String method) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(endpointPermissionJpaEntity)
                        .where(
                                endpointPermissionJpaEntity.serviceName.eq(serviceName),
                                endpointPermissionJpaEntity.path.eq(path),
                                endpointPermissionJpaEntity.method.eq(method),
                                endpointPermissionJpaEntity.deleted.eq(false))
                        .fetchFirst();
        return result != null;
    }

    /**
     * 서비스명 + HTTP 메서드로 목록 조회 (경로 매칭용)
     *
     * <p>인증 시 요청 경로와 매칭할 후보 목록을 조회합니다.
     *
     * @param serviceName 서비스 이름
     * @param method HTTP 메서드
     * @return EndpointPermissionJpaEntity 목록
     */
    public List<EndpointPermissionJpaEntity> findAllByServiceNameAndMethod(
            String serviceName, String method) {
        return queryFactory
                .selectFrom(endpointPermissionJpaEntity)
                .where(
                        endpointPermissionJpaEntity.serviceName.eq(serviceName),
                        endpointPermissionJpaEntity.method.eq(method),
                        endpointPermissionJpaEntity.deleted.eq(false))
                .fetch();
    }

    /**
     * 서비스명으로 전체 엔드포인트 권한 목록 조회 (권한 명세 조회용)
     *
     * <p>Gateway에서 권한 검증을 위해 전체 엔드포인트 권한 목록을 조회합니다.
     *
     * @param serviceName 서비스 이름
     * @return EndpointPermissionJpaEntity 목록
     */
    public List<EndpointPermissionJpaEntity> findAllByServiceName(String serviceName) {
        return queryFactory
                .selectFrom(endpointPermissionJpaEntity)
                .where(
                        endpointPermissionJpaEntity.serviceName.eq(serviceName),
                        endpointPermissionJpaEntity.deleted.eq(false))
                .orderBy(endpointPermissionJpaEntity.path.asc())
                .fetch();
    }

    /**
     * 서비스명으로 활성화된 엔드포인트 권한 개수 조회
     *
     * @param serviceName 서비스 이름
     * @return 활성화된 권한 개수
     */
    public long countByServiceName(String serviceName) {
        Long count =
                queryFactory
                        .select(endpointPermissionJpaEntity.count())
                        .from(endpointPermissionJpaEntity)
                        .where(
                                endpointPermissionJpaEntity.serviceName.eq(serviceName),
                                endpointPermissionJpaEntity.deleted.eq(false))
                        .fetchOne();
        return count != null ? count : 0L;
    }

    /**
     * 엔드포인트 권한 검색 (페이징)
     *
     * @param serviceName 서비스 이름 필터 (null 허용, 정확한 매칭)
     * @param path 경로 필터 (null 허용, 부분 검색)
     * @param method HTTP 메서드 필터 (null 허용)
     * @param isPublic 공개 여부 필터 (null 허용)
     * @param offset 시작 위치
     * @param limit 조회 개수
     * @return EndpointPermissionJpaEntity 목록
     */
    public List<EndpointPermissionJpaEntity> search(
            String serviceName,
            String path,
            String method,
            Boolean isPublic,
            int offset,
            int limit) {
        BooleanBuilder builder = buildCondition(serviceName, path, method, isPublic);

        return queryFactory
                .selectFrom(endpointPermissionJpaEntity)
                .where(builder)
                .orderBy(endpointPermissionJpaEntity.createdAt.desc())
                .offset(offset)
                .limit(limit)
                .fetch();
    }

    /**
     * 엔드포인트 권한 검색 개수 조회
     *
     * @param serviceName 서비스 이름 필터
     * @param path 경로 필터
     * @param method HTTP 메서드 필터
     * @param isPublic 공개 여부 필터
     * @return 조건에 맞는 총 개수
     */
    public long count(String serviceName, String path, String method, Boolean isPublic) {
        BooleanBuilder builder = buildCondition(serviceName, path, method, isPublic);

        Long count =
                queryFactory
                        .select(endpointPermissionJpaEntity.count())
                        .from(endpointPermissionJpaEntity)
                        .where(builder)
                        .fetchOne();
        return count != null ? count : 0L;
    }

    /**
     * 검색 조건 빌더 생성
     *
     * @param serviceName 서비스 이름 필터
     * @param path 경로 필터
     * @param method HTTP 메서드 필터
     * @param isPublic 공개 여부 필터
     * @return BooleanBuilder
     */
    private BooleanBuilder buildCondition(
            String serviceName, String path, String method, Boolean isPublic) {
        BooleanBuilder builder = new BooleanBuilder();

        // 삭제되지 않은 엔드포인트만 조회
        builder.and(endpointPermissionJpaEntity.deleted.eq(false));

        if (serviceName != null && !serviceName.isBlank()) {
            builder.and(endpointPermissionJpaEntity.serviceName.eq(serviceName));
        }
        if (path != null && !path.isBlank()) {
            builder.and(endpointPermissionJpaEntity.path.containsIgnoreCase(path));
        }
        if (method != null && !method.isBlank()) {
            builder.and(endpointPermissionJpaEntity.method.eq(method.toUpperCase()));
        }
        if (isPublic != null) {
            builder.and(endpointPermissionJpaEntity.isPublic.eq(isPublic));
        }
        return builder;
    }
}
