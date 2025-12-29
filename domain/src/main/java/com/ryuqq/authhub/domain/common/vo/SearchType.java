package com.ryuqq.authhub.domain.common.vo;

/**
 * SearchType - 검색 방식 Value Object
 *
 * <p>텍스트 검색 시 사용할 검색 방식을 지정합니다.
 *
 * <p><strong>검색 방식별 특징:</strong>
 *
 * <ul>
 *   <li>{@link #PREFIX_LIKE}: 접두사 검색, 인덱스 활용 가능, 빠른 성능
 *   <li>{@link #CONTAINS_LIKE}: 포함 검색, Full Table Scan, 소량 데이터에 적합
 *   <li>{@link #MATCH_AGAINST}: 전문 검색, FULLTEXT INDEX 필요, 대용량 데이터에 적합
 * </ul>
 *
 * <p><strong>권장 사용 시나리오:</strong>
 *
 * <ul>
 *   <li>소량 데이터 (< 10,000건): {@code CONTAINS_LIKE}
 *   <li>중량 데이터 (10,000 ~ 100,000건): {@code PREFIX_LIKE}
 *   <li>대량 데이터 (> 100,000건): {@code MATCH_AGAINST}
 * </ul>
 *
 * <p><strong>사용 예시:</strong>
 *
 * <pre>{@code
 * // Criteria에서 사용
 * UserCriteria criteria = UserCriteria.builder()
 *     .keyword("john")
 *     .searchType(SearchType.MATCH_AGAINST)  // 대용량 User 테이블
 *     .build();
 *
 * // QueryDSL에서 분기
 * BooleanExpression condition = switch (searchType) {
 *     case PREFIX_LIKE -> user.name.startsWith(keyword);
 *     case CONTAINS_LIKE -> user.name.containsIgnoreCase(keyword);
 *     case MATCH_AGAINST -> matchAgainstExpression(user.name, keyword);
 * };
 * }</pre>
 *
 * @author development-team
 * @since 1.0.0
 */
public enum SearchType {

    /**
     * 접두사 LIKE 검색 (LIKE 'keyword%')
     *
     * <p>키워드로 시작하는 항목을 검색합니다.
     *
     * <p><strong>특징:</strong>
     *
     * <ul>
     *   <li>B-Tree 인덱스 활용 가능
     *   <li>빠른 검색 성능
     *   <li>권한 키 (resource:action) 검색에 적합
     * </ul>
     *
     * <p><strong>SQL 예시:</strong> {@code WHERE name LIKE 'keyword%'}
     */
    PREFIX_LIKE,

    /**
     * 포함 LIKE 검색 (LIKE '%keyword%')
     *
     * <p>키워드를 포함하는 항목을 검색합니다.
     *
     * <p><strong>특징:</strong>
     *
     * <ul>
     *   <li>Full Table Scan 발생
     *   <li>소량 데이터에 적합
     *   <li>가장 유연한 검색
     * </ul>
     *
     * <p><strong>SQL 예시:</strong> {@code WHERE name LIKE '%keyword%'}
     */
    CONTAINS_LIKE,

    /**
     * MySQL 전문 검색 (MATCH ... AGAINST)
     *
     * <p>FULLTEXT INDEX를 활용한 전문 검색입니다.
     *
     * <p><strong>특징:</strong>
     *
     * <ul>
     *   <li>FULLTEXT INDEX 필수
     *   <li>대용량 데이터에 적합
     *   <li>자연어 처리, 불용어 제외
     *   <li>관련성 점수 제공
     * </ul>
     *
     * <p><strong>SQL 예시:</strong> {@code WHERE MATCH(name) AGAINST('keyword' IN NATURAL LANGUAGE
     * MODE)}
     *
     * <p><strong>주의:</strong> 대상 컬럼에 FULLTEXT INDEX가 설정되어 있어야 합니다.
     */
    MATCH_AGAINST;

    /**
     * 기본 검색 방식 (포함 검색)
     *
     * @return CONTAINS_LIKE
     */
    public static SearchType defaultType() {
        return CONTAINS_LIKE;
    }

    /**
     * 대용량 데이터용 기본 검색 방식
     *
     * @return MATCH_AGAINST
     */
    public static SearchType forLargeData() {
        return MATCH_AGAINST;
    }

    /**
     * 인덱스 최적화 검색 방식
     *
     * @return PREFIX_LIKE
     */
    public static SearchType forIndexOptimized() {
        return PREFIX_LIKE;
    }

    /**
     * LIKE 기반 검색인지 확인
     *
     * @return PREFIX_LIKE 또는 CONTAINS_LIKE이면 true
     */
    public boolean isLikeBased() {
        return this == PREFIX_LIKE || this == CONTAINS_LIKE;
    }

    /**
     * 전문 검색인지 확인
     *
     * @return MATCH_AGAINST이면 true
     */
    public boolean isFullTextSearch() {
        return this == MATCH_AGAINST;
    }

    /**
     * 인덱스 활용 가능한 검색인지 확인
     *
     * @return PREFIX_LIKE 또는 MATCH_AGAINST이면 true
     */
    public boolean isIndexable() {
        return this == PREFIX_LIKE || this == MATCH_AGAINST;
    }

    /**
     * 화면 표시용 이름 반환
     *
     * @return 한글 표시 이름
     */
    public String displayName() {
        return switch (this) {
            case PREFIX_LIKE -> "시작 문자 검색";
            case CONTAINS_LIKE -> "포함 검색";
            case MATCH_AGAINST -> "전문 검색";
        };
    }

    /**
     * 문자열로부터 SearchType 파싱 (대소문자 무관)
     *
     * @param value 문자열 ("prefix_like", "CONTAINS_LIKE", "match_against" 등)
     * @return SearchType (null이거나 유효하지 않으면 기본값 반환)
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
