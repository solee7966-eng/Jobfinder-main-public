package com.spring.app.chat.controller;

import com.spring.app.chat.domain.ChatMessageResponseDTO;
import com.spring.app.chat.domain.ChatRoomListDTO;
import com.spring.app.chat.service.ChatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ChatApiController REST API 테스트
 * Spring Context를 띄우지 않고 Controller만 단독으로 테스트한다.
 */
@ExtendWith(MockitoExtension.class) // Mockito 기반 단위 테스트 환경을 활성화한다.
class ChatApiControllerTest {

    private MockMvc mockMvc; // 가짜 HTTP 요청을 보내기 위한 테스트 도구

    @Mock // 실제 ChatService 대신 가짜 Service 객체를 만든다.
    private ChatService chatService;

    private final Principal principal = () -> "jobseeker01"; // 테스트용 로그인 사용자

    @BeforeEach // 각 테스트 실행 전에 MockMvc를 새로 구성한다.
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new ChatApiController(chatService))
                .build();
    }

    @Test
    @DisplayName("POST /api/chat/rooms - 채팅방 생성 또는 기존 채팅방 조회")
    void createOrGetRoom_success() throws Exception {
        given(chatService.getOrCreateChatRoom(1L, "jobseeker01"))
                .willReturn(10L);

        String requestJson = """
                {
                  "applicationId": 1
                }
                """;

        mockMvc.perform(post("/api/chat/rooms")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("ok"))
                .andExpect(jsonPath("$.roomId").value(10));

        then(chatService).should()
                .getOrCreateChatRoom(1L, "jobseeker01");
    }

    @Test
    @DisplayName("GET /api/chat/rooms - 내 채팅방 목록 조회")
    void getChatRooms_success() throws Exception {
        ChatRoomListDTO room = new ChatRoomListDTO();
        room.setRoomId(10L);
        room.setApplicationId(1L);
        room.setOpponentId("company01");
        room.setOpponentName("테스트기업");
        room.setJobTitle("풀스택 개발자 채용");
        room.setLastMessage("안녕하세요.");
        room.setUnreadCount(2);

        given(chatService.getChatRoomList("jobseeker01"))
                .willReturn(List.of(room));

        mockMvc.perform(get("/api/chat/rooms")
                        .principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].roomId").value(10))
                .andExpect(jsonPath("$[0].opponentId").value("company01"))
                .andExpect(jsonPath("$[0].opponentName").value("테스트기업"))
                .andExpect(jsonPath("$[0].jobTitle").value("풀스택 개발자 채용"))
                .andExpect(jsonPath("$[0].unreadCount").value(2));
    }

    @Test
    @DisplayName("GET /api/chat/unread-count - 전체 안 읽은 채팅 메시지 수 조회")
    void getTotalUnreadChatCount_success() throws Exception {
        given(chatService.getTotalUnreadChatCount("jobseeker01"))
                .willReturn(5);

        mockMvc.perform(get("/api/chat/unread-count")
                        .principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.unreadCount").value(5));
    }

    @Test
    @DisplayName("GET /api/chat/rooms/{roomId}/messages - 메시지 목록 조회")
    void getMessages_success() throws Exception {
        ChatMessageResponseDTO message = new ChatMessageResponseDTO();
        message.setMessageId(100L);
        message.setRoomId(10L);
        message.setSenderMemberId("jobseeker01");
        message.setContent("안녕하세요.");
        message.setMine(true);
        message.setReadByOpponent(true);

        given(chatService.getChatMessages(10L, "jobseeker01", null, 30))
                .willReturn(List.of(message));

        mockMvc.perform(get("/api/chat/rooms/{roomId}/messages", 10L)
                        .principal(principal)
                        .param("size", "30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].messageId").value(100))
                .andExpect(jsonPath("$[0].content").value("안녕하세요."))
                .andExpect(jsonPath("$[0].mine").value(true))
                .andExpect(jsonPath("$[0].readByOpponent").value(true));
    }

    @Test
    @DisplayName("POST /api/chat/rooms/{roomId}/messages - 메시지 전송")
    void sendMessage_success() throws Exception {
        ChatMessageResponseDTO response = new ChatMessageResponseDTO();
        response.setMessageId(100L);
        response.setRoomId(10L);
        response.setSenderMemberId("jobseeker01");
        response.setContent("안녕하세요.");

        given(chatService.sendMessage(10L, "jobseeker01", "안녕하세요.", "TEXT"))
                .willReturn(response);

        String requestJson = """
                {
                  "content": "안녕하세요.",
                  "messageType": "TEXT"
                }
                """;

        mockMvc.perform(post("/api/chat/rooms/{roomId}/messages", 10L)
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messageId").value(100))
                .andExpect(jsonPath("$.content").value("안녕하세요."));
    }

    @Test
    @DisplayName("POST /api/chat/rooms/{roomId}/read - 읽음 처리")
    void markAsRead_success() throws Exception {
        given(chatService.markAsRead(10L, "jobseeker01", 100L))
                .willReturn(1);

        String requestJson = """
                {
                  "messageId": 100
                }
                """;

        mockMvc.perform(post("/api/chat/rooms/{roomId}/read", 10L)
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("ok"));
    }
}