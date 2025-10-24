# JIRA Scope Clarification - AuthHub 프로젝트 책임 범위

## 📋 문서 목적

이 문서는 AuthHub 프로젝트가 실제로 개발해야 할 범위를 명확히 정의하고, JIRA Epic/Task 중 어떤 것이 AuthHub의 책임이고 어떤 것이 다른 서비스의 책임인지를 구분합니다.

**참고 문서**: [AuthHub_real_development.md](./AuthHub_real_development.md)

---

## 🎯 핵심 원칙: AuthHub = Pure Identity Provider

### AuthHub의 책임 (MUST)
1. **인증 (Authentication)**: 누가 누구인지 확인
2. **기본 조직 관리**: 어느 조직 소속인지
3. **Base Role**: 기본 역할이 무엇인지 (OWNER, ADMIN, MEMBER 등)

### AuthHub의 책임이 아님 (MUST NOT)
1. **상세 비즈니스 권한**: B2C/B2B 서비스가 자체 DB에서 관리
2. **도메인 특화 로직**: 고객/셀러/기업 관련 비즈니스 로직
3. **외부 연동**: 소셜 로그인, SMS 인증 등 (각 서비스가 필요 시 구현)

---

## 📊 JIRA Epic 분류

### ✅ Epic 1: AuthHub Core (AUT-1) - AuthHub 개발

**책임**: 로그인/로그아웃, JWT 토큰 발급/갱신, JWKS 엔드포인트

**Tasks**: AUT-001 ~ AUT-006 (총 6개)
- ✅ 모두 AuthHub에서 개발

**주요 기능**:
- POST /auth/login → JWT Access/Refresh 토큰 발급
- POST /auth/refresh → Access 토큰 갱신
- POST /auth/logout → Refresh 토큰 무효화
- GET /auth/jwks → JWT 서명 검증용 공개키 제공

---

### ✅ Epic 2: AuthHub Identity (AUT-2) - AuthHub 개발 (Base Role만)

**책임**: 사용자 계정, 기본 조직 소속, base_role 관리

**Tasks**: AUT-007 ~ AUT-012 (총 6개)
- ✅ 모두 AuthHub에서 개발
- ⚠️ 단, 권한은 **base_role 수준만** 관리

**Database Schema (AuthHub)**:
```sql
-- 사용자 기본 정보
CREATE TABLE users (
  user_id BIGINT PRIMARY KEY,
  email VARCHAR(255),
  phone_number VARCHAR(50),
  password_hash VARCHAR(255),
  created_at TIMESTAMP,
  updated_at TIMESTAMP
);

-- 인증 수단
CREATE TABLE user_credentials (
  credential_id BIGINT PRIMARY KEY,
  user_id BIGINT,
  credential_type VARCHAR(50),  -- PASSWORD, API_KEY
  credential_value VARCHAR(500),
  expires_at TIMESTAMP
);

-- 조직
CREATE TABLE organizations (
  org_id BIGINT PRIMARY KEY,
  org_name VARCHAR(255),
  org_type VARCHAR(50),  -- PERSONAL, BUSINESS
  created_at TIMESTAMP
);

-- 기본 조직 소속 및 Base Role
CREATE TABLE user_organizations (
  user_id BIGINT,
  org_id BIGINT,
  base_role VARCHAR(50),  -- OWNER, ADMIN, MANAGER, MEMBER, VIEWER
  joined_at TIMESTAMP,
  PRIMARY KEY (user_id, org_id)
);

-- 플랫폼 구분
CREATE TABLE platform_identities (
  identity_id BIGINT PRIMARY KEY,
  user_id BIGINT,
  platform VARCHAR(50),    -- B2C, B2B, INTERNAL
  user_type VARCHAR(50),   -- CUSTOMER, SELLER, VENDOR, ADMIN
  org_id BIGINT,
  created_at TIMESTAMP
);
```

**JWT Claims (AuthHub가 발급)**:
```json
{
  "user_id": "usr_xxx",
  "platform": "B2C",
  "user_type": "SELLER",
  "org_id": "org_nike",
  "base_role": "ADMIN"  // ← 기본 역할만 포함!
}
```

**중요**: 상세 비즈니스 권한 (can_manage_products, purchase_limit 등)은 **JWT에 포함되지 않으며**, 각 서비스가 자체 DB에서 관리합니다.

---

### ⚠️ Epic 3: B2C Commerce Integration (AUT-3) - 참고 문서

**상태**: Won't Do in AuthHub
**용도**: B2C 서비스 개발 시 참고용 문서

**Tasks**: AUT-020 ~ AUT-025 (총 6개)
- ❌ 모두 Won't Do 마킹 완료
- 📚 B2C 서비스 프로젝트에서 개발

**B2C 서비스 책임**:

1. **Domain Layer**:
   - `Customer` Aggregate (고객 프로필, 멤버십, 포인트)
   - `Seller` Aggregate (셀러 정보, 정산 계좌, 수수료율)
   - `SellerMember` Aggregate (셀러 직원 권한)

2. **Database Schema (B2C Service)**:
```sql
-- 고객 정보
CREATE TABLE customers (
  customer_id BIGINT PRIMARY KEY,
  user_id BIGINT,  -- ← AuthHub의 user_id 참조 (Long FK)
  membership_level VARCHAR(50),
  total_points INT,
  created_at TIMESTAMP
);

-- 셀러 정보
CREATE TABLE sellers (
  seller_id BIGINT PRIMARY KEY,
  org_id BIGINT,  -- ← AuthHub의 org_id 참조 (Long FK)
  business_name VARCHAR(255),
  settlement_account VARCHAR(100),
  commission_rate DECIMAL(5,2),
  created_at TIMESTAMP
);

-- 셀러 직원 상세 권한 (AuthHub의 base_role과는 별개!)
CREATE TABLE seller_member_permissions (
  seller_member_id BIGINT PRIMARY KEY,
  seller_id BIGINT,
  user_id BIGINT,  -- ← AuthHub의 user_id 참조
  -- 실제 비즈니스 권한
  can_manage_products BOOLEAN,
  can_view_settlements BOOLEAN,
  can_process_refunds BOOLEAN,
  daily_refund_limit DECIMAL(10,2),
  created_at TIMESTAMP
);
```

3. **권한 체크 (B2C Service)**:
```java
// B2C 서비스의 PermissionChecker
public class SellerPermissionChecker {

    public boolean canManageProducts(Long userId, Long sellerId) {
        // 1. AuthHub JWT에서 base_role 확인 (Gateway에서 이미 검증됨)
        // 2. seller_member_permissions 테이블에서 상세 권한 확인
        SellerMemberPermission permission =
            sellerMemberPermissionRepository
                .findByUserIdAndSellerId(userId, sellerId);

        return permission != null && permission.isCanManageProducts();
    }
}
```

**AuthHub와의 관계**:
- AuthHub: JWT 토큰 발급, JWKS 제공
- Gateway: JWT 서명 검증 (AuthHub JWKS 사용), base_role 확인
- B2C Service: 자체 DB에서 상세 권한 확인 및 비즈니스 로직 처리

---

### ⚠️ Epic 4: B2B Distribution Integration (AUT-4) - 참고 문서

**상태**: Won't Do in AuthHub
**용도**: B2B 서비스 개발 시 참고용 문서

**Tasks**: AUT-026 ~ AUT-031 (총 6개)
- ❌ 모두 Won't Do 마킹 완료
- 📚 B2B 서비스 프로젝트에서 개발

**B2B 서비스 책임**:

1. **Domain Layer**:
   - `Company` Aggregate (기업 정보, 사업자 등록번호)
   - `Subscription` Aggregate (구독 플랜, 만료일)
   - `CompanyMember` Aggregate (기업 직원 권한)

2. **Database Schema (B2B Service)**:
```sql
-- 기업 정보
CREATE TABLE companies (
  company_id BIGINT PRIMARY KEY,
  org_id BIGINT,  -- ← AuthHub의 org_id 참조 (Long FK)
  business_number VARCHAR(50),
  corporate_name VARCHAR(255),
  tax_invoice_email VARCHAR(255),
  created_at TIMESTAMP
);

-- 구독 정보
CREATE TABLE subscriptions (
  subscription_id BIGINT PRIMARY KEY,
  company_id BIGINT,
  plan_type VARCHAR(50),  -- BASIC, PRO, ENTERPRISE
  started_at TIMESTAMP,
  expires_at TIMESTAMP,
  monthly_fee DECIMAL(10,2)
);

-- 기업 직원 상세 권한 (AuthHub의 base_role과는 별개!)
CREATE TABLE company_member_permissions (
  company_member_id BIGINT PRIMARY KEY,
  company_id BIGINT,
  user_id BIGINT,  -- ← AuthHub의 user_id 참조
  -- 실제 비즈니스 권한
  can_create_orders BOOLEAN,
  purchase_limit DECIMAL(15,2),
  requires_approval_above DECIMAL(15,2),
  can_view_analytics BOOLEAN,
  created_at TIMESTAMP
);
```

3. **권한 체크 (B2B Service)**:
```java
// B2B 서비스의 PermissionChecker
public class CompanyPermissionChecker {

    public boolean canCreateOrder(Long userId, Long companyId, BigDecimal amount) {
        // 1. AuthHub JWT에서 base_role 확인 (Gateway에서 이미 검증됨)
        // 2. company_member_permissions 테이블에서 상세 권한 확인
        CompanyMemberPermission permission =
            companyMemberPermissionRepository
                .findByUserIdAndCompanyId(userId, companyId);

        if (permission == null || !permission.isCanCreateOrders()) {
            return false;
        }

        // 3. 금액 한도 체크 (비즈니스 규칙)
        if (amount.compareTo(permission.getPurchaseLimit()) > 0) {
            return false;
        }

        return true;
    }
}
```

**AuthHub와의 관계**:
- AuthHub: JWT 토큰 발급, JWKS 제공
- Gateway: JWT 서명 검증, base_role 확인
- B2B Service: 자체 DB에서 상세 권한 확인, 구독 상태 검증, 비즈니스 로직 처리

---

### ✅ Epic 5: Security & Operations (AUT-5) - AuthHub 개발

**책임**: Rate Limiting, 감사 로그, 모니터링

**Tasks**: AUT-023 ~ AUT-028 (총 6개)
- ✅ 모두 AuthHub에서 개발

**주요 기능**:
- Rate Limiting (IP/User별 요청 제한)
- Audit Log (인증 시도, 토큰 발급/갱신/폐기 기록)
- Security Monitoring (이상 패턴 감지)
- Session Management (다중 로그인 관리)

---

## 🔄 실제 권한 체크 플로우

### Scenario: B2C 셀러가 상품 등록 요청

```
1. User → Gateway
   POST /seller/products
   Authorization: Bearer <JWT>

2. Gateway → JWT 검증
   - AuthHub JWKS로 JWT 서명 검증
   - Claims 추출: { user_type: "SELLER", base_role: "ADMIN" }
   - user_type == "SELLER" 확인 ✅

3. Gateway → B2C Service
   X-User-ID: usr_xxx
   X-Org-ID: org_nike
   X-Base-Role: ADMIN

4. B2C Service → 상세 권한 체크
   SELECT can_manage_products, daily_product_limit
   FROM seller_member_permissions
   WHERE user_id = 'usr_xxx' AND seller_id = 'seller_nike';

   → can_manage_products = TRUE ✅
   → daily_product_limit 체크 ✅

5. B2C Service → 비즈니스 로직 실행
   - 상품 등록 처리
   - 일일 등록 수 증가
   - 응답 반환

⚠️ AuthHub는 호출되지 않음! Gateway가 JWKS만 조회하여 JWT 검증
```

---

## 📋 JIRA Task 요약

| Epic | Tasks | AuthHub 개발 | 상태 |
|------|-------|-------------|------|
| Epic 1 (AUT-1) | AUT-001 ~ AUT-006 (6개) | ✅ Yes | Active |
| Epic 2 (AUT-2) | AUT-007 ~ AUT-012 (6개) | ✅ Yes (Base Role만) | Active |
| Epic 3 (AUT-3) | AUT-020 ~ AUT-025 (6개) | ❌ No (B2C 서비스) | Won't Do |
| Epic 4 (AUT-4) | AUT-026 ~ AUT-031 (6개) | ❌ No (B2B 서비스) | Won't Do |
| Epic 5 (AUT-5) | AUT-023 ~ AUT-028 (6개) | ✅ Yes | Active |

**Total**: 30 tasks
- **AuthHub 개발**: 18 tasks (Epic 1, 2, 5)
- **Won't Do**: 12 tasks (Epic 3, 4)

---

## 💡 핵심 정리

### AuthHub = Pure Identity Provider

**AuthHub가 하는 일**:
1. 누가 누구인지 확인 (Authentication)
2. 어느 조직 소속인지 (Organization)
3. 기본 역할이 무엇인지 (Base Role)

**AuthHub가 하지 않는 일**:
1. 상세 비즈니스 권한 관리 → 각 서비스가 자체 DB에서
2. 도메인 특화 로직 → 각 서비스가 구현
3. 외부 연동 (소셜/SMS) → 필요한 서비스가 구현

### B2C/B2B = Business Logic Owner

**B2C/B2B 서비스가 하는 일**:
1. 실제 권한 관리 (can_manage_products, purchase_limit 등)
2. 비즈니스 규칙 (일일 한도, 승인 플로우 등)
3. 도메인 특화 로직 (고객/셀러/기업 관리)

### 권한 체크는 2단계

1. **Gateway (Base Role)**: AuthHub JWT로 기본 역할 확인
2. **Service (Detailed Permissions)**: 자체 DB에서 상세 권한 확인

---

## 📝 업데이트 이력

- **2025-01-XX**: 초기 작성 (Epic 3, 4 Won't Do 마킹 완료, Epic 2 명확화)
- JIRA Epic AUT-3 description 업데이트
- JIRA Epic AUT-4 description 업데이트
- JIRA Tasks AUT-20~31 Won't Do 마킹
- JIRA Epic AUT-2 description 명확화 (Base Role만 관리)

---

**✅ 이 문서를 기준으로 AuthHub 프로젝트는 Epic 1, 2, 5만 개발합니다!**
