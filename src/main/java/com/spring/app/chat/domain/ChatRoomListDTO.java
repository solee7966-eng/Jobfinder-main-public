package com.spring.app.chat.domain;

import lombok.Data;

/**
 * 채팅방 목록 조회 DTO
 * 채팅 리스트 화면용
 */
@Data
public class ChatRoomListDTO {

    /** 채팅방 PK */
    private Long roomId;

    /** 지원 PK */
    private Long applicationId;

    /** 상대방 회원 ID */
    private String opponentId;

    /** 상대방 이름 */
    private String opponentName;
    
    /** 채용공고 제목*/
    private String jobTitle;

    /** 상대방 프로필 (확장 대비) */
    private String opponentProfile;

    /** 마지막 메시지 */
    private String lastMessage;

    /** 마지막 메시지 시간 */
    private String lastMessageAt;

    /** 안읽은 메시지 수 */
    private int unreadCount;
}