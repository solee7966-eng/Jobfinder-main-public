package com.spring.app.chat.domain;

import lombok.Data;

/**
 * 채팅 읽음 상태 DTO
 * tbl_chat_read 테이블 매핑
 */
@Data
public class ChatReadDTO {

    /** 채팅방 PK */
    private Long roomId;

    /** 회원 ID */
    private String memberId;

    /** 마지막으로 읽은 메시지 PK */
    private Long lastReadMessageId;

    /** 마지막 읽은 시각 */
    private String lastReadAt;

    /** 갱신 시각 */
    private String updatedAt;
}