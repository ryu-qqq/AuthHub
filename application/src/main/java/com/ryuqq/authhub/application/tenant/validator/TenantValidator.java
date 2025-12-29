package com.ryuqq.authhub.application.tenant.validator;

import com.ryuqq.authhub.application.tenant.manager.query.TenantReadManager;
import com.ryuqq.authhub.domain.tenant.exception.DuplicateTenantNameException;
import com.ryuqq.authhub.domain.tenant.vo.TenantName;
import org.springframework.stereotype.Component;

/**
 * TenantValidator - 테넌트 비즈니스 규칙 검증
 *
 * <p>테넌트 관련 비즈니스 규칙 검증을 담당합니다. 조회가 필요한 검증 로직을 Service에서 분리합니다.
 *
 * <p><strong>검증 규칙:</strong>
 *
 * <ul>
 *   <li>이름 중복 검증 (신규 생성)
 *   <li>이름 중복 검증 (수정 시 자기 자신 제외)
 * </ul>
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Component} 어노테이션 ({@code @Service} 아님)
 *   <li>{@code validate*()} 메서드 네이밍
 *   <li>ReadManager만 의존 (Port 직접 호출 금지)
 *   <li>{@code @Transactional} 금지 (ReadManager 책임)
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class TenantValidator {

    private final TenantReadManager readManager;

    public TenantValidator(TenantReadManager readManager) {
        this.readManager = readManager;
    }

    /**
     * 이름 중복 검증 (신규 생성용)
     *
     * <p>동일한 이름의 테넌트가 이미 존재하면 예외를 발생시킵니다.
     *
     * @param name 검증할 테넌트 이름
     * @throws DuplicateTenantNameException 동일 이름 테넌트가 존재하는 경우
     */
    public void validateNameNotDuplicated(TenantName name) {
        if (readManager.existsByName(name)) {
            throw new DuplicateTenantNameException(name);
        }
    }

    /**
     * 이름 중복 검증 (수정 시 자기 자신 제외)
     *
     * <p>수정 시 자신의 현재 이름이 아닌 다른 테넌트의 이름과 중복되면 예외를 발생시킵니다.
     *
     * @param newName 변경할 새 이름
     * @param currentName 현재 이름
     * @throws DuplicateTenantNameException 동일 이름 테넌트가 존재하는 경우
     */
    public void validateNameNotDuplicatedExcluding(TenantName newName, TenantName currentName) {
        if (!newName.equals(currentName) && readManager.existsByName(newName)) {
            throw new DuplicateTenantNameException(newName);
        }
    }
}
