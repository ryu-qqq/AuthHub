package com.ryuqq.authhub.application.tenant.manager.query;

import com.ryuqq.authhub.application.tenant.port.out.query.TenantQueryPort;
import com.ryuqq.authhub.domain.tenant.aggregate.Tenant;
import com.ryuqq.authhub.domain.tenant.exception.TenantNotFoundException;
import com.ryuqq.authhub.domain.tenant.identifier.TenantId;
import com.ryuqq.authhub.domain.tenant.vo.TenantName;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * TenantReadManager - 단일 Port 조회 관리
 *
 * <p>단일 QueryPort에 대한 조회를 관리합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Component} 어노테이션 ({@code @Service} 아님)
 *   <li>{@code @Transactional(readOnly = true)} 메서드 단위
 *   <li>{@code find*()} 메서드 네이밍
 *   <li>단일 QueryPort만 의존
 *   <li>비즈니스 로직 금지
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class TenantReadManager {

    private final TenantQueryPort queryPort;

    public TenantReadManager(TenantQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    /**
     * ID로 Tenant 조회 (필수)
     *
     * @param id Tenant ID
     * @return Tenant Domain
     * @throws TenantNotFoundException 존재하지 않는 경우
     */
    @Transactional(readOnly = true)
    public Tenant findById(TenantId id) {
        return queryPort.findById(id).orElseThrow(() -> new TenantNotFoundException(id));
    }

    /**
     * ID로 Tenant 존재 여부 확인
     *
     * @param id Tenant ID
     * @return 존재 여부
     */
    @Transactional(readOnly = true)
    public boolean existsById(TenantId id) {
        return queryPort.existsById(id);
    }

    /**
     * 이름으로 Tenant 존재 여부 확인
     *
     * @param name Tenant 이름
     * @return 존재 여부
     */
    @Transactional(readOnly = true)
    public boolean existsByName(TenantName name) {
        return queryPort.existsByName(name);
    }

    /**
     * 조건에 맞는 Tenant 목록 조회 (페이징)
     *
     * @param name Tenant 이름 필터 (null 허용, 부분 검색)
     * @param status Tenant 상태 필터 (null 허용)
     * @param offset 시작 위치
     * @param limit 조회 개수
     * @return Tenant Domain 목록
     */
    @Transactional(readOnly = true)
    public List<Tenant> findAllByCriteria(String name, String status, int offset, int limit) {
        return queryPort.findAllByCriteria(name, status, offset, limit);
    }

    /**
     * 조건에 맞는 Tenant 개수 조회
     *
     * @param name Tenant 이름 필터 (null 허용, 부분 검색)
     * @param status Tenant 상태 필터 (null 허용)
     * @return 조건에 맞는 Tenant 총 개수
     */
    @Transactional(readOnly = true)
    public long countAll(String name, String status) {
        return queryPort.countAll(name, status);
    }
}
