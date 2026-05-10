package com.spring.app.chat.domain;

import lombok.Data;

/**
 * 채팅 메시지 DTO
 * tbl_chat_message 테이블 매핑
 */
@Data
public class ChatMessageDTO {

    /** 메시지 PK */
    private Long messageId;

    /** 채팅방 PK */
    private Long roomId;

    /** 발신자 회원 ID */
    private String senderMemberId;

    /** 메시지 타입 (TEXT / IMAGE / FILE 등 확장 가능) */
    private String messageType;

    /** 메시지 내용 */
    private String content;

    /** 삭제 여부 (0: 정상, 1: 삭제) */
    private int deletedYn;

    /** 생성일시 */
    private String createdAt;
}