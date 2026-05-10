package com.spring.app.chat.domain;

import lombok.Data;

/**
 * 채팅방 생성 요청 DTO
 */
@Data
public class ChatRoomCreateRequest {

    /** 지원 PK (application 기반 채팅 생성용) */
    private Long applicationId;
}