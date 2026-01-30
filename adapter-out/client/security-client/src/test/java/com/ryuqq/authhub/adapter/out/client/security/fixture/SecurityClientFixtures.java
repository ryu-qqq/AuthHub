package com.ryuqq.authhub.adapter.out.client.security.fixture;

import com.ryuqq.authhub.adapter.out.client.security.config.JwtProperties;
import com.ryuqq.authhub.application.token.dto.composite.TokenClaimsComposite;
import com.ryuqq.authhub.application.userrole.dto.composite.RolesAndPermissionsComposite;
import com.ryuqq.authhub.domain.user.id.UserId;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Set;

/**
 * Security Client 테스트 픽스처
 *
 * @author development-team
 * @since 1.0.0
 */
public final class SecurityClientFixtures {

    private static final String DEFAULT_USER_ID = "01941234-5678-7000-8000-123456789001";
    private static final String DEFAULT_TENANT_ID = "01941234-5678-7000-8000-123456789002";
    private static final String DEFAULT_ORG_ID = "01941234-5678-7000-8000-123456789003";

    private SecurityClientFixtures() {}

    /** 기본 JwtProperties (HS256) */
    public static JwtProperties jwtPropertiesHs256() {
        JwtProperties props = new JwtProperties();
        props.setSecret("test-secret-key-at-least-256-bits-long-for-hs256-algorithm");
        props.setAccessTokenExpiration(3600L);
        props.setRefreshTokenExpiration(604800L);
        props.setIssuer("authhub");
        return props;
    }

    /** RSA 활성화된 JwtProperties (공개키/비밀키 content) */
    public static JwtProperties jwtPropertiesRsaWithContent() throws NoSuchAlgorithmException {
        KeyPair keyPair = generateRsaKeyPair();
        String publicKeyPem = toPublicKeyPem(keyPair);
        String privateKeyPem = toPrivateKeyPem(keyPair);

        JwtProperties props = new JwtProperties();
        props.setSecret("fallback");
        props.setAccessTokenExpiration(3600L);
        props.setRefreshTokenExpiration(604800L);
        props.setIssuer("authhub");

        JwtProperties.RsaKeyProperties rsa = props.getRsa();
        rsa.setEnabled(true);
        rsa.setKeyId("test-key-1");
        rsa.setPublicKeyContent(publicKeyPem);
        rsa.setPrivateKeyContent(privateKeyPem);

        return props;
    }

    /** RSA KeyPair 생성 */
    public static KeyPair generateRsaKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(512);
        return generator.generateKeyPair();
    }

    /** 공개키를 PEM 형식으로 변환 */
    public static String toPublicKeyPem(KeyPair keyPair) {
        String encoded = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        return "-----BEGIN PUBLIC KEY-----\n" + encoded + "\n-----END PUBLIC KEY-----";
    }

    /** 비밀키를 PEM 형식으로 변환 */
    public static String toPrivateKeyPem(KeyPair keyPair) {
        String encoded = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
        return "-----BEGIN PRIVATE KEY-----\n" + encoded + "\n-----END PRIVATE KEY-----";
    }

    /** 기본 TokenClaimsComposite */
    public static TokenClaimsComposite tokenClaimsComposite() {
        return TokenClaimsComposite.builder()
                .userId(UserId.of(DEFAULT_USER_ID))
                .tenantId(DEFAULT_TENANT_ID)
                .tenantName("Test Tenant")
                .organizationId(DEFAULT_ORG_ID)
                .organizationName("Test Org")
                .email("test@example.com")
                .mfaVerified(false)
                .build();
    }

    /** 기본 RolesAndPermissionsComposite */
    public static RolesAndPermissionsComposite rolesAndPermissionsComposite() {
        return new RolesAndPermissionsComposite(
                Set.of("ROLE_USER", "ROLE_ADMIN"), Set.of("user:read", "user:write"));
    }

    /** 빈 RolesAndPermissionsComposite */
    public static RolesAndPermissionsComposite emptyRolesAndPermissions() {
        return RolesAndPermissionsComposite.empty();
    }
}
