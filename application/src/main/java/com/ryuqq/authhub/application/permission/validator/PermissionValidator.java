package com.ryuqq.authhub.application.permission.validator;

import com.ryuqq.authhub.application.permission.manager.query.PermissionReadManager;
import com.ryuqq.authhub.domain.permission.exception.DuplicatePermissionKeyException;
import com.ryuqq.authhub.domain.permission.vo.PermissionKey;
import org.springframework.stereotype.Component;

/**
 * PermissionValidator - 권한 비즈니스 규칙 검증
 *
 * <p>조회가 필요한 검증 로직을 담당합니다.
 *
 * <p><strong>Zero-Tolerance 규칙:</strong>
 *
 * <ul>
 *   <li>{@code @Component} 어노테이션 ({@code @Service} 아님)
 *   <li>{@code validate*()} 메서드 네이밍
 *   <li>ReadManager만 의존 (Port 직접 호출 금지)
 *   <li>검증 실패 시 Domain Exception throw
 *   <li>{@code @Transactional} 금지 (ReadManager 책임)
 *   <li>Lombok 금지
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class PermissionValidator {

    private final PermissionReadManager readManager;

    public PermissionValidator(PermissionReadManager readManager) {
        this.readManager = readManager;
    }

    /**
     * 권한 키 중복 검증
     *
     * @param key 검증할 권한 키
     * @throws DuplicatePermissionKeyException 중복 시
     */
    public void validateKeyNotDuplicated(PermissionKey key) {
        if (readManager.existsByKey(key)) {
            throw new DuplicatePermissionKeyException(key.value());
        }
    }
}
