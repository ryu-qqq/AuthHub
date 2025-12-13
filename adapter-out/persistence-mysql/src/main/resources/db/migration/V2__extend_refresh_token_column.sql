-- =====================================================
-- AuthHub Database Schema - Migration V2
-- Description: RS256 JWT 토큰 지원을 위한 token 컬럼 확장
-- =====================================================

-- RS256 서명된 JWT 토큰은 HMAC보다 길어서 VARCHAR(500)으로는 부족함
-- VARCHAR(2000)으로 확장하여 RS256 토큰 저장 가능하도록 변경

ALTER TABLE refresh_tokens
    MODIFY COLUMN token VARCHAR(2000) NOT NULL COMMENT 'Refresh Token (RS256 지원)';

-- 인덱스도 함께 조정 (prefix index 유지)
ALTER TABLE refresh_tokens
    DROP INDEX idx_refresh_tokens_token,
    ADD INDEX idx_refresh_tokens_token (token(255));
