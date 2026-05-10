package com.spring.app.chat.domain;

import lombok.Data;

/**
 * 메시지 전송 요청 DTO
 */
@Data
public class ChatMessageSendRequest {

    /** 채팅방 PK */
    private Long roomId;

    /** 메시지 내용 */
    private String content;

    /** 메시지 타입 (기본 TEXT) */
    private String messageType;
    
    /** 프론트에서 생성한 임시 메시지 ID */
    private String clientMessageId;
}