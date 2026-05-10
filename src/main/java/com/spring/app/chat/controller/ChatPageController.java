package com.spring.app.chat.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.spring.app.chat.domain.ChatRoomDetailDTO;
import com.spring.app.chat.service.ChatService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatPageController {
	
	private final ChatService chatService;

    /**
     * 채팅방 목록 페이지
     */
    @GetMapping("/rooms")
    public String chatRoomListPage(Principal principal, Model model) {
    	String loginMemberId = principal.getName();
    	
    	model.addAttribute("loginMemberId", loginMemberId);
    	
        return "chat/chatRoomList";
    }

    /**
     * 채팅방 상세 페이지
     */
    @GetMapping("/rooms/{roomId}")
    public String chatRoomDetailPage(@PathVariable("roomId") Long roomId,
                                     Principal principal,
                                     Model model) {

        String loginMemberId = principal.getName();

        ChatRoomDetailDTO roomDetail = chatService.getChatRoomDetail(roomId, loginMemberId);

        model.addAttribute("roomId", roomId);
        model.addAttribute("loginMemberId", loginMemberId);
        model.addAttribute("roomDetail", roomDetail);

        return "chat/chatRoomDetail";
    }
}