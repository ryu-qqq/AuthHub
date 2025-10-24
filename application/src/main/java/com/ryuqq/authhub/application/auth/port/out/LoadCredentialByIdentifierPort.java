package com.ryuqq.authhub.application.auth.port.out;

import com.ryuqq.authhub.domain.auth.credential.CredentialType;
import com.ryuqq.authhub.domain.auth.credential.Identifier;
import com.ryuqq.authhub.domain.auth.credential.UserCredential;

import java.util.Optional;

/**
 * LoadCredentialByIdentifier Port Interface.
 *
 * <p>식별자(Identifier)와 인증 타입(CredentialType)으로 인증 정보를 조회하는 Out Port입니다.
 * Hexagonal Architecture의 Port-Adapter 패턴을 따르며, Application Layer가 Persistence Layer에 의존하지 않도록 합니다.</p>
 *
 * <p><strong>구현 위치:</strong></p>
 * <ul>
 *   <li>Interface: {@code application/auth/port/out/} (Application Layer)</li>
 *   <li>Adapter: {@code adapter-out-persistence/auth/adapter/} (Persistence Layer)</li>
 * </ul>
 *
 * <p><strong>사용 시나리오:</strong></p>
 * <ul>
 *   <li>로그인 시 identifier(이메일, 전화번호, 사용자명)로 인증 정보 조회</li>
 *   <li>비밀번호 검증 전 UserCredential 로드</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
public interface LoadCredentialByIdentifierPort {

    /**
     * 식별자와 인증 타입으로 인증 정보를 조회합니다.
     *
     * <p>로그인 시 사용자가 입력한 identifier(이메일, 전화번호, 사용자명)와
     * credentialType(EMAIL, PHONE, USERNAME)을 조합하여 UserCredential을 조회합니다.</p>
     *
     * @param credentialType 인증 타입 (null 불가)
     * @param identifier 식별자 (null 불가)
     * @return Optional로 감싼 UserCredential (존재하지 않으면 Empty)
     * @throws IllegalArgumentException credentialType 또는 identifier가 null인 경우
     * @author AuthHub Team
     * @since 1.0.0
     */
    Optional<UserCredential> loadByIdentifier(CredentialType credentialType, Identifier identifier);
}
