-- =====================================================
-- AuthHub Database Schema - Add phone_number to users
-- Version: V5
-- Description: users 테이블에 phone_number 컬럼 추가
-- =====================================================

ALTER TABLE users
    ADD COLUMN phone_number VARCHAR(20) NULL COMMENT '전화번호' AFTER identifier;
