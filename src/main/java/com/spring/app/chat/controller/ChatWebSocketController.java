package com.spring.app.chat.controller;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.spring.app.chat.domain.ChatMessageResponseDTO;
import com.spring.app.chat.domain.ChatMessageSendRequest;
import com.spring.app.chat.domain.ChatReadEventDTO;
import com.spring.app.chat.domain.ChatRoomDTO;
import com.spring.app.chat.domain.ChatRoomListDTO;
import com.spring.app.chat.service.ChatService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat/send")
    public void sendMessage(ChatMessageSendRequest request, Principal principal) {

        String senderMemberId = principal.getName();

        ChatMessageResponseDTO response = chatService.sendMessage(
                request.getRoomId(),
                senderMemberId,
                request.getContent(),
                request.getMessageType()
        );
        
        // 프론트 임시 메시지와 DB 저장 메시지를 매칭하기 위해 그대로 응답에 담아준다.
        response.setClientMessageId(request.getClientMessageId());

        messagingTemplate.convertAndSend(
                "/topic/chat/room/" + request.getRoomId(),
                response
        );
        
        
        // 채팅방 목록 실시간 갱신
        ChatRoomDTO room = chatService.getChatRoom(request.getRoomId());

        ChatRoomListDTO senderRoomItem =
                chatService.getChatRoomListItem(request.getRoomId(), room.getCompanyMemberId());

        ChatRoomListDTO jobseekerRoomItem =
                chatService.getChatRoomListItem(request.getRoomId(), room.getJobseekerMemberId());

        // 기업 회원 목록 갱신
        messagingTemplate.convertAndSend(
                "/topic/chat/rooms/" + room.getCompanyMemberId(),
                senderRoomItem
        );

        // 구직자 회원 목록 갱신
        messagingTemplate.convertAndSend(
                "/topic/chat/rooms/" + room.getJobseekerMemberId(),
                jobseekerRoomItem
        );
    }
    
    
    
    @MessageMapping("/chat/read")
    public void readMessage(ChatReadEventDTO request, Principal principal) {

        String memberId = principal.getName();

        // DB 읽음 처리
        chatService.markAsRead(
                request.getRoomId(),
                memberId,
                request.getLastReadMessageId()
        );

        ChatReadEventDTO event = new ChatReadEventDTO();
        event.setRoomId(request.getRoomId());
        event.setReaderMemberId(memberId);
        event.setLastReadMessageId(request.getLastReadMessageId());

        // 같은 채팅방 구독자에게 읽음 상태 전파
        messagingTemplate.convertAndSend(
                "/topic/chat/room/" + request.getRoomId() + "/read",
                event
        );
    }
}