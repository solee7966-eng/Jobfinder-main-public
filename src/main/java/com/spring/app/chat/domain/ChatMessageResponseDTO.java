package com.spring.app.chat.domain;

import lombok.Data;

/**
 * 메시지 조회/응답 DTO
 * 채팅 메시지 리스트 / WebSocket 응답 공용
 */
@Data
public class ChatMessageResponseDTO {

    /** 메시지 PK */
    private Long messageId;

    /** 채팅방 PK */
    private Long roomId;

    /** 발신자 ID */
    private String senderMemberId;

    /** 메시지 내용 */
    private String content;

    /** 생성 시간 */
    private String createdAt;

    /** 내가 보낸 메시지 여부 (프론트 처리용) */
    private boolean mine;
    
    /** 프론트 임시 메시지와 서버 저장 메시지를 매칭하기 위한 ID */
    private String clientMessageId;
    
    /** 상대방이 마지막으로 읽은 메시지 ID */
    private Long opponentLastReadMessageId;

    /** 내가 보낸 메시지를 상대방이 읽었는지 여부 */
    private boolean readByOpponent;
}