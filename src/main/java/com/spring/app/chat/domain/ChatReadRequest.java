package com.spring.app.chat.domain;

import lombok.Data;

/**
 * 읽음 처리 요청 DTO
 */
@Data
public class ChatReadRequest {

    /** 마지막으로 읽은 메시지 PK
     *  null이면 해당 채팅방의 최신 메시지까지 읽음 처리
     */
    private Long messageId;
}