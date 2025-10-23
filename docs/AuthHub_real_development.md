🎯 AuthHub의 실제 책임 범위
✅ AuthHub가 실제로 개발해야 할 것 (Epic 1, 2, 5만 해당)
yamlEpic 1: AuthHub Core
- 로그인/로그아웃
- JWT 토큰 발급/갱신
- 비밀번호 검증
- JWKS 엔드포인트

Epic 2: AuthHub Identity
- 사용자 계정 생성 (users 테이블)
- 인증 수단 관리 (user_credentials)
- 기본 조직 소속 관리 (organizations, user_organizations)
- 플랫폼 구분 (platform_identities)

Epic 5: Security & Operations
- Rate Limiting
- 감사 로그
- 모니터링
  ❌ AuthHub가 하지 않는 것 (Epic 3, 4는 참고용)
  yamlEpic 3, 4는 B2C/B2B 서비스 개발 시 참고 가이드입니다!

B2C Service가 개발할 것:
- customers 테이블 (고객 프로필, 멤버십 등급)
- sellers 테이블 (셀러 정보, 정산)
- seller_members 테이블 (셀러 직원 권한)
- b2c_admins 테이블 (관리자 세부 권한)

B2B Service가 개발할 것:
- companies 테이블 (기업 정보, 구독)
- company_members 테이블 (기업 직원 권한)
- b2b_admins 테이블 (플랫폼 관리자)
- subscription_plans 테이블
  📊 권한 관리 책임 분리
  1️⃣ AuthHub의 권한 관리 (최소한)
  sql-- AuthHub는 이것만 관리
  CREATE TABLE user_organizations (
  user_id BIGINT,
  org_id BIGINT,
  base_role VARCHAR(50), -- OWNER, ADMIN, MEMBER (기본 역할만)
  PRIMARY KEY (user_id, org_id)
  );
  JWT에 포함되는 정보:
  json{
  "user_id": "usr_xxx",
  "platform": "B2C",
  "user_type": "SELLER",
  "org_id": "org_nike",
  "base_role": "ADMIN"  // 기본 역할만
  }
  2️⃣ B2C/B2B의 권한 관리 (상세)
  sql-- B2C Service에서 관리
  CREATE TABLE seller_member_permissions (
  seller_member_id BIGINT,
  -- 실제 비즈니스 권한
  can_manage_products BOOLEAN,
  can_view_settlements BOOLEAN,
  can_process_refunds BOOLEAN,
  daily_refund_limit DECIMAL(10,2)
  );

-- B2B Service에서 관리  
CREATE TABLE company_member_permissions (
company_member_id BIGINT,
-- 실제 비즈니스 권한
can_create_orders BOOLEAN,
purchase_limit DECIMAL(15,2),
requires_approval_above DECIMAL(15,2)
);
🔄 실제 권한 체크 플로우
mermaidsequenceDiagram
participant User
participant Gateway
participant AuthHub
participant B2C_Service

    User->>Gateway: POST /seller/products<br/>(JWT Token)
    Gateway->>Gateway: JWT 서명 검증<br/>(AuthHub JWKS 사용)
    Note over Gateway: user_type == "SELLER" 확인
    
    Gateway->>B2C_Service: 요청 전달<br/>X-User-ID: usr_xxx<br/>X-Org-ID: org_nike
    
    B2C_Service->>B2C_Service: 1. seller_members 조회<br/>2. can_manage_products 체크<br/>3. 일일 등록 한도 체크
    
    Note over B2C_Service: AuthHub 호출 없음!<br/>자체 DB에서 권한 확인
    
    B2C_Service-->>User: 상품 등록 완료
📝 수정된 AuthHub Jira 티켓
실제 AuthHub가 개발해야 할 것만:
✅ Epic 1: AuthHub Core (그대로 진행)

[AUTH-001~006] 모두 AuthHub 개발 범위 ✅

✅ Epic 2: AuthHub Identity (일부 수정)

[AUTH-007~012] AuthHub 개발 범위 ✅
단, 권한은 base_role 수준만 관리

⚠️ Epic 3, 4 제거 또는 이동
yaml옵션 1: 제거
- Epic 3, 4의 모든 태스크 제거
- 각 서비스 프로젝트에서 별도 작성

옵션 2: 참고 문서로 변환
제목: [DOC-001] B2C/B2B 서비스 통합 가이드
타입: Documentation
설명: |
B2C/B2B 서비스 개발 시 참고할 통합 가이드
- JWT 검증 라이브러리 사용법
- 권한 체크 패턴
- 데이터베이스 스키마 예시
  ✅ Epic 5: Security & Operations (그대로 진행)

[AUTH-023~028] 모두 AuthHub 개발 범위 ✅

💡 핵심 정리
AuthHub = 순수 Identity Provider

누가 누구인지 (Authentication)
어느 조직 소속인지 (Organization)
기본 역할이 뭔지 (Base Role)

B2C/B2B = Business Logic Owner

실제 권한 관리
비즈니스 규칙
도메인 특화 로직

이렇게 하면 AuthHub는 정말 필요한 인증/인가 기능에만 집중할 수 있고, 각 도메인 서비스는 자신의 비즈니스 로직을 독립적으로 관리할 수 있습니다!