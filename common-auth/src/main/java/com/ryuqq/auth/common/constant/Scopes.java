package com.ryuqq.auth.common.constant;

/**
 * Scopes - 역할 범위 상수 정의
 *
 * <p>역할의 접근 범위를 정의하는 상수입니다. 데이터 접근 제어 시 사용됩니다.
 *
 * <p><strong>범위 계층:</strong>
 *
 * <ul>
 *   <li>GLOBAL: 전역 범위 (모든 테넌트/조직 접근)
 *   <li>TENANT: 테넌트 범위 (해당 테넌트 내 모든 조직 접근)
 *   <li>ORGANIZATION: 조직 범위 (해당 조직만 접근)
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public final class Scopes {

    /** 전역 범위 - 모든 테넌트/조직 데이터 접근 가능 */
    public static final String GLOBAL = "GLOBAL";

    /** 테넌트 범위 - 해당 테넌트 내 모든 조직 데이터 접근 가능 */
    public static final String TENANT = "TENANT";

    /** 조직 범위 - 해당 조직 데이터만 접근 가능 */
    public static final String ORGANIZATION = "ORGANIZATION";

    private Scopes() {
        throw new AssertionError("Utility class - cannot instantiate");
    }

    /**
     * 범위 이름이 유효한지 확인
     *
     * @param scope 범위 이름
     * @return 유효한 범위이면 true
     */
    public static boolean isValidScope(String scope) {
        return GLOBAL.equals(scope) || TENANT.equals(scope) || ORGANIZATION.equals(scope);
    }

    /**
     * 범위 계층 비교 (높은 범위일수록 큰 값)
     *
     * @param scope 범위 이름
     * @return 범위 수준 (GLOBAL=3, TENANT=2, ORGANIZATION=1, 알 수 없음=0)
     */
    public static int getLevel(String scope) {
        if (GLOBAL.equals(scope)) {
            return 3;
        }
        if (TENANT.equals(scope)) {
            return 2;
        }
        if (ORGANIZATION.equals(scope)) {
            return 1;
        }
        return 0;
    }

    /**
     * 첫 번째 범위가 두 번째 범위를 포함하는지 확인
     *
     * @param higherScope 상위 범위
     * @param lowerScope 하위 범위
     * @return 포함 관계이면 true
     */
    public static boolean includes(String higherScope, String lowerScope) {
        return getLevel(higherScope) >= getLevel(lowerScope);
    }
}
