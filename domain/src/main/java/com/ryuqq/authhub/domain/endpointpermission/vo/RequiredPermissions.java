package com.ryuqq.authhub.domain.endpointpermission.vo;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/**
 * RequiredPermissions Value Object - 필수 권한 목록
 *
 * <p><strong>도메인 규칙</strong>:
 *
 * <ul>
 *   <li>권한 키 형식: {resource}:{action} (예: user:read, order:create)
 *   <li>빈 목록 허용 (권한 체크 필요 없는 엔드포인트)
 *   <li>OR 조건으로 평가 (목록 중 하나만 만족하면 허용)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public record RequiredPermissions(Set<String> values) {

    /** Compact Constructor (검증 및 불변 처리) */
    public RequiredPermissions {
        if (values == null) {
            values = Collections.emptySet();
        } else {
            values = Set.copyOf(values);
        }
    }

    /**
     * 값 기반 생성
     *
     * @param values 권한 키 목록
     * @return RequiredPermissions
     */
    public static RequiredPermissions of(Set<String> values) {
        return new RequiredPermissions(values);
    }

    /**
     * 빈 권한 목록 생성
     *
     * @return 빈 RequiredPermissions
     */
    public static RequiredPermissions empty() {
        return new RequiredPermissions(Collections.emptySet());
    }

    /**
     * 단일 권한으로 생성
     *
     * @param permission 권한 키
     * @return RequiredPermissions
     */
    public static RequiredPermissions single(String permission) {
        if (permission == null || permission.isBlank()) {
            throw new IllegalArgumentException("권한은 null이거나 빈 문자열일 수 없습니다");
        }
        return new RequiredPermissions(Set.of(permission));
    }

    /**
     * 권한이 비어있는지 확인
     *
     * @return 비어있으면 true
     */
    public boolean isEmpty() {
        return values.isEmpty();
    }

    /**
     * 주어진 권한이 포함되어 있는지 확인
     *
     * @param permission 확인할 권한
     * @return 포함되어 있으면 true
     */
    public boolean contains(String permission) {
        return values.contains(permission);
    }

    /**
     * 사용자 권한 중 하나라도 만족하는지 확인 (OR 조건)
     *
     * @param userPermissions 사용자가 가진 권한 목록
     * @return 하나라도 만족하면 true
     */
    public boolean hasAnyOf(Set<String> userPermissions) {
        if (values.isEmpty()) {
            return true;
        }
        if (userPermissions == null || userPermissions.isEmpty()) {
            return false;
        }
        return values.stream().anyMatch(userPermissions::contains);
    }

    /**
     * 권한 개수 반환
     *
     * @return 권한 개수
     */
    public int size() {
        return values.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RequiredPermissions that = (RequiredPermissions) o;
        return Objects.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values);
    }

    @Override
    public String toString() {
        return "RequiredPermissions{values=" + values + "}";
    }
}
