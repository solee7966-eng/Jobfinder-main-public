package com.spring.app.chat.domain;

import lombok.Data;

/**
 * 채팅방 정보 DTO
 * tbl_chat_room 테이블 매핑
 */
@Data
public class ChatRoomDTO {

    /** 채팅방 PK */
    private Long roomId;

    /** 채팅방 타입 (APPLICATION / OFFER 등) */
    private String roomType;

    /** 지원 PK (지원 기반 채팅일 경우 사용) */
    private Long applicationId;

    /** 제안서 PK (제안 기반 채팅일 경우 사용) */
    private Long offerSubmitId;

    /** 채용공고 PK */
    private Long jobId;

    /** 기업 회원 ID */
    private String companyMemberId;

    /** 구직자 회원 ID */
    private String jobseekerMemberId;

    /** 채팅방 생성자 */
    private String createdBy;

    /** 채팅방 상태 (1: 활성, 0: 종료 등) */
    private int roomStatus;

    /** 마지막 메시지 미리보기 (목록 화면용) */
    private String lastMessagePreview;

    /** 마지막 메시지 시간 */
    private String lastMessageAt;

    /** 생성일시 */
    private String createdAt;

    /** 채팅방 종료일시 */
    private String closedAt;

    /* =========================
       추가: 화면/조인용 필드
       ========================= */

    /** 상대방 이름 (목록 표시용) */
    private String opponentName;

    /** 상대방 프로필 이미지 (확장 대비) */
    private String opponentProfile;

    /** 안읽은 메시지 수 */
    private int unreadCount;
}