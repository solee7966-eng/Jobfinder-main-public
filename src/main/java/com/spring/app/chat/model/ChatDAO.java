package com.spring.app.chat.model;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.spring.app.chat.domain.ChatMessageDTO;
import com.spring.app.chat.domain.ChatMessageResponseDTO;
import com.spring.app.chat.domain.ChatReadDTO;
import com.spring.app.chat.domain.ChatRoomDTO;
import com.spring.app.chat.domain.ChatRoomDetailDTO;
import com.spring.app.chat.domain.ChatRoomListDTO;

@Mapper
public interface ChatDAO {

    /**
     * 지원 정보 기반 채팅 참여자 조회
     */
    Map<String, Object> selectApplicationParticipantInfo(@Param("applicationId") Long applicationId);

    /**
     * 지원 기반 채팅방 존재 여부
     */
    int existsChatRoomByApplicationId(@Param("applicationId") Long applicationId);

    /**
     * 지원 기반 기존 채팅방 roomId 조회
     */
    Long selectRoomIdByApplicationId(@Param("applicationId") Long applicationId);

    /**
     * 채팅방 참여자 여부 확인
     */
    int isChatRoomParticipant(@Param("roomId") Long roomId,
                              @Param("memberId") String memberId);

    /**
     * 채팅방 생성
     */
    int insertChatRoom(ChatRoomDTO dto);

    /**
     * 채팅방 목록 실시간 조회
     */
    ChatRoomDTO selectChatRoomByRoomId(@Param("roomId") Long roomId);

    /**
     * 로그인 사용자의 채팅방 목록 조회
     */
    List<ChatRoomListDTO> selectChatRoomList(@Param("memberId") String memberId);
    
    /**
     * 로그인 사용자의 전체 안 읽은 채팅 메시지 수 조회
     */
    int selectTotalUnreadChatCount(@Param("memberId") String memberId);

    /**
     * 메시지 저장
     */
    int insertChatMessage(ChatMessageDTO dto);

    /**
     * 메시지 단건 조회
     */
    ChatMessageDTO selectChatMessageByMessageId(@Param("messageId") Long messageId);

    /**
     * 메시지 목록 조회
     * 최초 조회 시 최신 메시지를 조회하고,
     * lastMessageId가 있으면 해당 메시지보다 이전 메시지를 페이징 조회한다.
     */
    List<ChatMessageResponseDTO> selectChatMessages(@Param("roomId") Long roomId,
    												@Param("memberId") String memberId,
                                                    @Param("lastMessageId") Long lastMessageId,
                                                    @Param("size") int size);

    /**
     * 채팅방의 최신 메시지 ID 조회
     */
    Long selectLastMessageId(@Param("roomId") Long roomId);

    /**
     * 읽음 정보 조회
     */
    ChatReadDTO selectChatRead(@Param("roomId") Long roomId,
                               @Param("memberId") String memberId);

    /**
     * 읽음 정보 최초 등록
     */
    int insertChatRead(ChatReadDTO dto);

    /**
     * 마지막 읽음 메시지 갱신
     */
    int updateLastReadMessageId(ChatReadDTO dto);

    /**
     * 채팅방 마지막 메시지 미리보기 / 시간 갱신
     */
    int updateChatRoomLastMessage(@Param("roomId") Long roomId,
                                  @Param("lastMessagePreview") String lastMessagePreview);
    
    
    /**
     * 채팅방 상세 헤더 정보 조회
     */
    ChatRoomDetailDTO selectChatRoomDetail(@Param("roomId") Long roomId,
                                           @Param("memberId") String memberId);
    
    
    /**
     * 채팅방 목록 단건 조회
     * WebSocket 목록 실시간 갱신용
     */
    ChatRoomListDTO selectChatRoomListItem(@Param("roomId") Long roomId,
                                           @Param("memberId") String memberId);
    
    
    
    
}