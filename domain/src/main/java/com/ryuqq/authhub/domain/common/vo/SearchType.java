package com.ryuqq.authhub.domain.common.vo;

/**
 * SearchType - 검색 방식 Value Object
 *
 * <p>문자열 검색 시 매칭 방식을 지정합니다.
 *
 * <p><strong>사용 예시:</strong>
 *
 * <pre>{@code
 * TenantCriteria criteria = TenantCriteria.of(
 *     "테넌트",
 *     SearchType.CONTAINS_LIKE,  // 부분 일치
 *     statuses,
 *     dateRange,
 *     sortKey,
 *     sortDirection,
 *     pageRequest
 * );
 * }</pre>
 *
 * @author development-team
 * @since 1.0.0
 */
public enum SearchType {

    /**
     * 정확 일치 (Exact Match)
     *
     * <p>검색어와 정확히 일치하는 결과만 반환
     *
     * <ul>
     *   <li>SQL: WHERE name = '검색어'
     * </ul>
     */
    EXACT,

    /**
     * 부분 일치 (Contains Like)
     *
     * <p>검색어를 포함하는 모든 결과 반환
     *
     * <ul>
     *   <li>SQL: WHERE name LIKE '%검색어%'
     * </ul>
     */
    CONTAINS_LIKE,

    /**
     * 접두사 일치 (Starts With)
     *
     * <p>검색어로 시작하는 결과 반환
     *
     * <ul>
     *   <li>SQL: WHERE name LIKE '검색어%'
     * </ul>
     */
    STARTS_WITH,

    /**
     * 접미사 일치 (Ends With)
     *
     * <p>검색어로 끝나는 결과 반환
     *
     * <ul>
     *   <li>SQL: WHERE name LIKE '%검색어'
     * </ul>
     */
    ENDS_WITH,

    /**
     * 접두사 일치 (Prefix Like)
     *
     * <p>검색어로 시작하는 결과 반환 (STARTS_WITH의 별칭)
     *
     * <ul>
     *   <li>SQL: WHERE name LIKE '검색어%'
     * </ul>
     */
    PREFIX_LIKE,

    /**
     * 전문 검색 (Match Against)
     *
     * <p>MySQL FULLTEXT 인덱스 기반 검색 (현재는 CONTAINS_LIKE와 동일)
     *
     * <ul>
     *   <li>SQL: MATCH(name) AGAINST('검색어')
     * </ul>
     */
    MATCH_AGAINST;

    /**
     * 기본 검색 방식 (부분 일치)
     *
     * @return CONTAINS_LIKE
     */
    public static SearchType defaultType() {
        return CONTAINS_LIKE;
    }

    /**
     * 정확 일치인지 확인
     *
     * @return EXACT이면 true
     */
    public boolean isExact() {
        return this == EXACT;
    }

    /**
     * 부분 일치인지 확인
     *
     * @return CONTAINS_LIKE이면 true
     */
    public boolean isContainsLike() {
        return this == CONTAINS_LIKE;
    }

    /**
     * 접두사 일치인지 확인
     *
     * @return STARTS_WITH이면 true
     */
    public boolean isStartsWith() {
        return this == STARTS_WITH;
    }

    /**
     * 접미사 일치인지 확인
     *
     * @return ENDS_WITH이면 true
     */
    public boolean isEndsWith() {
        return this == ENDS_WITH;
    }

    /**
     * 화면 표시용 이름 반환
     *
     * @return 한글 표시 이름
     */
    public String displayName() {
        return switch (this) {
            case EXACT -> "정확 일치";
            case CONTAINS_LIKE -> "부분 일치";
            case STARTS_WITH -> "접두사 일치";
            case ENDS_WITH -> "접미사 일치";
            case PREFIX_LIKE -> "접두사 일치";
            case MATCH_AGAINST -> "전문 검색";
        };
    }

    /**
     * 문자열로부터 SearchType 파싱 (대소문자 무관)
     *
     * @param value 문자열 ("exact", "CONTAINS_LIKE" 등)
     * @return SearchType (null이거나 유효하지 않으면 CONTAINS_LIKE 반환)
     */
    public static SearchType fromString(String value) {
        if (value == null || value.isBlank()) {
            return defaultType();
        }
        try {
            return SearchType.valueOf(value.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            return defaultType();
        }
    }
}
