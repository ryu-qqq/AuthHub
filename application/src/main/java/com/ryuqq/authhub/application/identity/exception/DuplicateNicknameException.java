package com.ryuqq.authhub.application.identity.exception;

/**
 * Nickname 중복 예외.
 *
 * <p>사용자 등록 시 이미 존재하는 nickname을 사용하려고 할 때 발생하는 예외입니다.</p>
 *
 * <p><strong>발생 조건:</strong></p>
 * <ul>
 *   <li>동일한 nickname이 이미 데이터베이스에 존재</li>
 *   <li>중복 확인(CheckDuplicateNicknamePort) 후 생성 사이에 동시 요청 발생</li>
 * </ul>
 *
 * <p><strong>처리 방법:</strong></p>
 * <ul>
 *   <li>Adapter Layer에서 적절한 HTTP 상태 코드로 변환 (예: 409 Conflict)</li>
 *   <li>클라이언트에게 다른 nickname 선택 요청</li>
 * </ul>
 *
 * @author AuthHub Team
 * @since 1.0.0
 */
public class DuplicateNicknameException extends RuntimeException {

    /**
     * 메시지를 포함한 DuplicateNicknameException을 생성합니다.
     *
     * @param message 예외 메시지 (중복된 nickname 정보 포함 권장)
     * @author AuthHub Team
     * @since 1.0.0
     */
    public DuplicateNicknameException(final String message) {
        super(message);
    }
}
