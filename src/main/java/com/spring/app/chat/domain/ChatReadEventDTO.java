package com.spring.app.chat.domain;

import lombok.Data;

/**
 * WebSocket 읽음 이벤트 DTO
 */
@Data
public class ChatReadEventDTO {

    /** 채팅방 ID */
    private Long roomId;

    /** 읽음 처리한 사용자 ID */
    private String readerMemberId;

    /** 마지막으로 읽은 메시지 ID */
    private Long lastReadMessageId;
}