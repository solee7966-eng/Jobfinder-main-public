package com.spring.app.chat.service;

import java.util.List;

import com.spring.app.chat.domain.ChatMessageResponseDTO;
import com.spring.app.chat.domain.ChatRoomDTO;
import com.spring.app.chat.domain.ChatRoomDetailDTO;
import com.spring.app.chat.domain.ChatRoomListDTO;

public interface ChatService {

    /**
     * 지원 기반 채팅방 생성 또는 기존 채팅방 조회
     */
    Long getOrCreateChatRoom(Long applicationId, String memberId);

    /**
     * 로그인 사용자의 채팅방 목록 조회
     */
    List<ChatRoomListDTO> getChatRoomList(String memberId);
    
    /**
     * 채팅방 목록 단건 조회
     * WebSocket 목록 갱신용
     */
    ChatRoomListDTO getChatRoomListItem(Long roomId, String memberId);

    /**
     * 채팅 메시지 목록 조회
     * lastMessageId가 없으면 최신 메시지 목록을 조회하고,
     * lastMessageId가 있으면 해당 메시지보다 이전 메시지를 조회한다.
     */
    List<ChatMessageResponseDTO> getChatMessages(Long roomId, String memberId, Long lastMessageId, int size);

    /**
     * 메시지 전송
     */
    ChatMessageResponseDTO sendMessage(Long roomId, String senderMemberId, String content, String messageType);

    /**
     * 읽음 처리
     */
    int markAsRead(Long roomId, String memberId, Long messageId);
    
    /**
     * 채팅방 상세 헤더 정보 조회
     */
    ChatRoomDetailDTO getChatRoomDetail(Long roomId, String memberId);
    
    /**
     * 채팅방 목록 실시간 갱신
     */
    ChatRoomDTO getChatRoom(Long roomId);
    
    
    /**
     * 로그인 사용자의 전체 안 읽은 채팅 메시지 수 조회
     */
    int getTotalUnreadChatCount(String memberId);
    
    
}