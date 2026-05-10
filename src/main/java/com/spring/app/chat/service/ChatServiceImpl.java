package com.spring.app.chat.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spring.app.chat.domain.ChatMessageDTO;
import com.spring.app.chat.domain.ChatMessageResponseDTO;
import com.spring.app.chat.domain.ChatReadDTO;
import com.spring.app.chat.domain.ChatRoomDTO;
import com.spring.app.chat.domain.ChatRoomDetailDTO;
import com.spring.app.chat.domain.ChatRoomListDTO;
import com.spring.app.chat.model.ChatDAO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

	private static final int DEFAULT_MESSAGE_SIZE = 30;
	private static final int MAX_MESSAGE_PAGE_SIZE = 50;
	private static final int MAX_MESSAGE_LENGTH = 1000;
    private static final String ROOM_TYPE_APPLICATION = "APPLICATION";
    private static final String MESSAGE_TYPE_TEXT = "TEXT";

    private final ChatDAO chatDAO;

    /**
     * 지원 기반 채팅방 생성 또는 기존 채팅방 반환
     */
    @Override
    @Transactional
    public Long getOrCreateChatRoom(Long applicationId, String memberId) {

        if (applicationId == null) {
            throw new IllegalArgumentException("applicationId는 필수입니다.");
        }

        Map<String, Object> appInfo = chatDAO.selectApplicationParticipantInfo(applicationId);
        if (appInfo == null) {
            throw new IllegalArgumentException("유효하지 않은 지원 정보입니다.");
        }

        String jobseekerMemberId = (String) appInfo.get("jobseekerMemberId");
        String companyMemberId = (String) appInfo.get("companyMemberId");

        if (!memberId.equals(jobseekerMemberId) && !memberId.equals(companyMemberId)) {
            throw new IllegalArgumentException("채팅방 생성 권한이 없습니다.");
        }

        Long roomId = chatDAO.selectRoomIdByApplicationId(applicationId);
        if (roomId != null) {
            return roomId;
        }

        ChatRoomDTO room = new ChatRoomDTO();
        room.setRoomType(ROOM_TYPE_APPLICATION);
        room.setApplicationId(applicationId);
        room.setJobId(((Number) appInfo.get("jobId")).longValue());
        room.setCompanyMemberId(companyMemberId);
        room.setJobseekerMemberId(jobseekerMemberId);
        room.setCreatedBy(memberId);

        chatDAO.insertChatRoom(room);

        return room.getRoomId();
    }

    /**
     * 채팅방 목록 조회
     */
    @Override
    public List<ChatRoomListDTO> getChatRoomList(String memberId) {
        return chatDAO.selectChatRoomList(memberId);
    }
    
    
    /**
     * 로그인 사용자의 전체 안 읽은 채팅 메시지 수 조회
     */
    @Override
    public int getTotalUnreadChatCount(String memberId) {
        return chatDAO.selectTotalUnreadChatCount(memberId);
    }
    
    
    /**
     * 채팅방 목록 단건 조회
     * WebSocket 목록 갱신용
     */
    @Override
    public ChatRoomListDTO getChatRoomListItem(Long roomId, String memberId) {

        validateChatRoomParticipant(roomId, memberId);

        return chatDAO.selectChatRoomListItem(roomId, memberId);
    }

    /**
     * 메시지 목록 조회
     * 최초 조회 시 최신 메시지를 조회하고,
     * lastMessageId가 있으면 해당 메시지보다 이전 메시지를 페이징 조회한다.
     */
    @Override
    public List<ChatMessageResponseDTO> getChatMessages(Long roomId, String memberId, Long lastMessageId, int size) {

        validateChatRoomParticipant(roomId, memberId);

        // 요청 size가 비정상적이면 기본값을 사용하고, 과도한 조회를 막기 위해 최대값을 제한한다.
        int pageSize = (size <= 0)
                ? DEFAULT_MESSAGE_SIZE
                : Math.min(size, MAX_MESSAGE_PAGE_SIZE);

        List<ChatMessageResponseDTO> list = chatDAO.selectChatMessages(roomId, memberId, lastMessageId, pageSize);

        if (list != null) {
            for (ChatMessageResponseDTO dto : list) {
                boolean mine = memberId.equals(dto.getSenderMemberId());
                dto.setMine(mine);

                // 내 메시지이고, 상대방의 last_read_message_id가 내 메시지 ID 이상이면 읽음
                dto.setReadByOpponent(
                        mine
                        && dto.getOpponentLastReadMessageId() != null
                        && dto.getOpponentLastReadMessageId() >= dto.getMessageId()
                );
            }

            list.sort(Comparator.comparing(ChatMessageResponseDTO::getMessageId));
        }

        return list;
    }

    /**
     * 메시지 전송
     */
    @Override
    @Transactional
    public ChatMessageResponseDTO sendMessage(Long roomId, String senderMemberId, String content, String messageType) {
        validateChatRoomParticipant(roomId, senderMemberId);

        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("메시지 내용이 비어 있습니다.");
        }

        String trimmed = content.trim();
        if (trimmed.length() > MAX_MESSAGE_LENGTH) {
            throw new IllegalArgumentException("메시지는 1000자 이하만 전송할 수 있습니다.");
        }

        String finalMessageType = (messageType == null || messageType.trim().isEmpty())
                ? MESSAGE_TYPE_TEXT
                : messageType.trim().toUpperCase();

        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setRoomId(roomId);
        dto.setSenderMemberId(senderMemberId);
        dto.setMessageType(finalMessageType);
        dto.setContent(trimmed);

        chatDAO.insertChatMessage(dto);

        // 채팅방 마지막 메시지/시간 갱신
        chatDAO.updateChatRoomLastMessage(roomId, buildPreview(trimmed));

        ChatMessageDTO saved = chatDAO.selectChatMessageByMessageId(dto.getMessageId());

        // 보낸 사람은 자기 메시지까지 읽음 처리
        ChatReadDTO readDTO = chatDAO.selectChatRead(roomId, senderMemberId);
        if (readDTO == null) {
            ChatReadDTO newRead = new ChatReadDTO();
            newRead.setRoomId(roomId);
            newRead.setMemberId(senderMemberId);
            newRead.setLastReadMessageId(dto.getMessageId());
            chatDAO.insertChatRead(newRead);
        }
        else if (readDTO.getLastReadMessageId() == null || dto.getMessageId() > readDTO.getLastReadMessageId()) {
            ChatReadDTO updateRead = new ChatReadDTO();
            updateRead.setRoomId(roomId);
            updateRead.setMemberId(senderMemberId);
            updateRead.setLastReadMessageId(dto.getMessageId());
            chatDAO.updateLastReadMessageId(updateRead);
        }

        ChatMessageResponseDTO response = new ChatMessageResponseDTO();
        response.setMessageId(saved.getMessageId());
        response.setRoomId(saved.getRoomId());
        response.setSenderMemberId(saved.getSenderMemberId());
        response.setContent(saved.getContent());
        response.setCreatedAt(saved.getCreatedAt());
        
        /*
          아래 코드로 인해 모든 구독자에게 같은 payload를 뿌릴 가능성이 생김!
          즉, 채팅방에 들어와있는 상태일 경우 상대방이 보낸 메시지도 내가 보낸 메시지처럼 보임
        */
        //response.setMine(false); //기본값으로 두거나 아예 빼기
        
        

        return response;
    }

    /**
     * 읽음 처리
     */
    @Override
    @Transactional
    public int markAsRead(Long roomId, String memberId, Long messageId) {

        validateChatRoomParticipant(roomId, memberId);

        if (messageId == null) {
            Long lastMessageId = chatDAO.selectLastMessageId(roomId);
            if (lastMessageId == null) {
                return 0;
            }
            messageId = lastMessageId;
        }

        ChatReadDTO current = chatDAO.selectChatRead(roomId, memberId);

        if (current == null) {
            ChatReadDTO dto = new ChatReadDTO();
            dto.setRoomId(roomId);
            dto.setMemberId(memberId);
            dto.setLastReadMessageId(messageId);
            return chatDAO.insertChatRead(dto);
        }

        if (current.getLastReadMessageId() != null && current.getLastReadMessageId() >= messageId) {
            return 0;
        }

        ChatReadDTO dto = new ChatReadDTO();
        dto.setRoomId(roomId);
        dto.setMemberId(memberId);
        dto.setLastReadMessageId(messageId);
        return chatDAO.updateLastReadMessageId(dto);
    }

    /**
     * 채팅방 상세 헤더 정보 조회
     */
    @Override
    public ChatRoomDetailDTO getChatRoomDetail(Long roomId, String memberId) {

        if (roomId == null) {
            throw new IllegalArgumentException("roomId는 필수입니다.");
        }

        ChatRoomDetailDTO detail = chatDAO.selectChatRoomDetail(roomId, memberId);

        if (detail == null) {
            throw new IllegalArgumentException("채팅방 접근 권한이 없습니다.");
        }

        return detail;
    }
    
    
    /**
     * 채팅방 참여자 검증
     */
    private void validateChatRoomParticipant(Long roomId, String memberId) {
        Integer result = chatDAO.isChatRoomParticipant(roomId, memberId);
        if (result == null || result == 0) {
            throw new IllegalArgumentException("채팅방 접근 권한이 없습니다.");
        }
    }

    /**
     * 마지막 메시지 미리보기 생성
     */
    private String buildPreview(String content) {
        if (content == null) {
            return null;
        }

        String normalized = content.replaceAll("\\s+", " ").trim();
        if (normalized.length() <= 100) {
            return normalized;
        }

        return normalized.substring(0, 100);
    }
    
    
    /**
     * 채팅방 목록 실시간 갱신
     */
    @Override
    public ChatRoomDTO getChatRoom(Long roomId) {
        return chatDAO.selectChatRoomByRoomId(roomId);
    }
    
}