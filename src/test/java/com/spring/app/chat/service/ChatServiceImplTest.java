package com.spring.app.chat.service;

import com.spring.app.chat.domain.ChatMessageDTO;
import com.spring.app.chat.domain.ChatMessageResponseDTO;
import com.spring.app.chat.domain.ChatReadDTO;
import com.spring.app.chat.domain.ChatRoomDTO;
import com.spring.app.chat.model.ChatDAO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

/**
 * ChatServiceImpl 단위 테스트
 * DB를 직접 연결하지 않고 ChatDAO를 Mock 처리해서 Service 로직만 검증한다.
 */
@ExtendWith(MockitoExtension.class) // JUnit5에서 Mockito 기능을 사용하기 위한 어노테이션
class ChatServiceImplTest {

	//가짜 객체(실제는 ChatDAO)
    @Mock // 실제 ChatDAO 대신 가짜 객체를 만들어 DB 호출 결과를 직접 지정한다.
    private ChatDAO chatDAO;

    //테스트 대상(ChatServiceImpl)
    @InjectMocks // @Mock으로 만든 ChatDAO를 ChatServiceImpl 생성자에 주입한다.
    private ChatServiceImpl chatService;

    
    
    @Test // JUnit이 실행할 테스트 메서드임을 표시한다.
    @DisplayName("지원 정보가 유효하고 채팅방이 없으면 새 채팅방을 생성한다")
    void getOrCreateChatRoom_createNewRoom_success() {
        // given
        Long applicationId = 1L;
        String memberId = "jobseeker01";

        given(chatDAO.selectApplicationParticipantInfo(applicationId))
                .willReturn(Map.of(
                        "jobseekerMemberId", "jobseeker01",
                        "companyMemberId", "company01",
                        "jobId", 100L
                ));

        given(chatDAO.selectRoomIdByApplicationId(applicationId))
                .willReturn(null);

        willAnswer(invocation -> {
            ChatRoomDTO room = invocation.getArgument(0);
            room.setRoomId(10L); // 실제 DB라면 시퀀스/selectKey로 들어갈 roomId를 테스트에서 직접 넣어준다.
            return 1;
        }).given(chatDAO).insertChatRoom(any(ChatRoomDTO.class));

        // when
        Long resultRoomId = chatService.getOrCreateChatRoom(applicationId, memberId);

        // then
        assertThat(resultRoomId).isEqualTo(10L);

        ArgumentCaptor<ChatRoomDTO> captor = ArgumentCaptor.forClass(ChatRoomDTO.class);
        then(chatDAO).should().insertChatRoom(captor.capture());

        ChatRoomDTO savedRoom = captor.getValue();
        assertThat(savedRoom.getRoomType()).isEqualTo("APPLICATION");
        assertThat(savedRoom.getApplicationId()).isEqualTo(applicationId);
        assertThat(savedRoom.getJobId()).isEqualTo(100L);
        assertThat(savedRoom.getCompanyMemberId()).isEqualTo("company01");
        assertThat(savedRoom.getJobseekerMemberId()).isEqualTo("jobseeker01");
        assertThat(savedRoom.getCreatedBy()).isEqualTo(memberId);
    }

    
    
    @Test
    @DisplayName("이미 채팅방이 있으면 새로 생성하지 않고 기존 roomId를 반환한다")
    void getOrCreateChatRoom_returnExistingRoom_success() {
        // given
        Long applicationId = 1L;
        String memberId = "company01";

        given(chatDAO.selectApplicationParticipantInfo(applicationId))
                .willReturn(Map.of(
                        "jobseekerMemberId", "jobseeker01",
                        "companyMemberId", "company01",
                        "jobId", 100L
                ));

        given(chatDAO.selectRoomIdByApplicationId(applicationId))
                .willReturn(20L);

        // when
        Long resultRoomId = chatService.getOrCreateChatRoom(applicationId, memberId);

        // then
        assertThat(resultRoomId).isEqualTo(20L);
        then(chatDAO).should(never()).insertChatRoom(any(ChatRoomDTO.class));
    }

    
    
    @Test
    @DisplayName("지원자도 기업도 아닌 사용자가 채팅방 생성을 시도하면 예외가 발생한다")
    void getOrCreateChatRoom_notParticipant_fail() {
        // given
        Long applicationId = 1L;
        String memberId = "other01";

        given(chatDAO.selectApplicationParticipantInfo(applicationId))
                .willReturn(Map.of(
                        "jobseekerMemberId", "jobseeker01",
                        "companyMemberId", "company01",
                        "jobId", 100L
                ));

        // when & then
        assertThatThrownBy(() -> chatService.getOrCreateChatRoom(applicationId, memberId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("채팅방 생성 권한이 없습니다.");

        then(chatDAO).should(never()).insertChatRoom(any(ChatRoomDTO.class));
    }

    
    
    @Test
    @DisplayName("메시지 전송 시 메시지를 저장하고 채팅방 마지막 메시지와 읽음 정보를 갱신한다")
    void sendMessage_success() {
        // given
        Long roomId = 10L;
        String senderMemberId = "jobseeker01";
        String content = "안녕하세요.\n문의드립니다.";

        given(chatDAO.isChatRoomParticipant(roomId, senderMemberId))
                .willReturn(1);

        willAnswer(invocation -> {
            ChatMessageDTO dto = invocation.getArgument(0);
            dto.setMessageId(100L); // 실제 DB라면 시퀀스/selectKey로 들어갈 messageId를 테스트에서 직접 넣어준다.
            return 1;
        }).given(chatDAO).insertChatMessage(any(ChatMessageDTO.class));

        ChatMessageDTO savedMessage = new ChatMessageDTO();
        savedMessage.setMessageId(100L);
        savedMessage.setRoomId(roomId);
        savedMessage.setSenderMemberId(senderMemberId);
        savedMessage.setMessageType("TEXT");
        savedMessage.setContent(content.trim());
        savedMessage.setCreatedAt("2026-05-03 10:00:00");

        given(chatDAO.selectChatMessageByMessageId(100L))
                .willReturn(savedMessage);

        given(chatDAO.selectChatRead(roomId, senderMemberId))
                .willReturn(null);

        given(chatDAO.insertChatRead(any(ChatReadDTO.class)))
                .willReturn(1);

        // when
        ChatMessageResponseDTO response =
                chatService.sendMessage(roomId, senderMemberId, content, null);

        // then
        assertThat(response.getMessageId()).isEqualTo(100L);
        assertThat(response.getRoomId()).isEqualTo(roomId);
        assertThat(response.getSenderMemberId()).isEqualTo(senderMemberId);
        assertThat(response.getContent()).isEqualTo(content.trim());

        ArgumentCaptor<ChatMessageDTO> messageCaptor = ArgumentCaptor.forClass(ChatMessageDTO.class);
        then(chatDAO).should().insertChatMessage(messageCaptor.capture());

        ChatMessageDTO insertedMessage = messageCaptor.getValue();
        assertThat(insertedMessage.getRoomId()).isEqualTo(roomId);
        assertThat(insertedMessage.getSenderMemberId()).isEqualTo(senderMemberId);
        assertThat(insertedMessage.getMessageType()).isEqualTo("TEXT");
        assertThat(insertedMessage.getContent()).isEqualTo(content.trim());

        then(chatDAO).should().updateChatRoomLastMessage(eq(roomId), anyString());

        ArgumentCaptor<ChatReadDTO> readCaptor = ArgumentCaptor.forClass(ChatReadDTO.class);
        then(chatDAO).should().insertChatRead(readCaptor.capture());

        ChatReadDTO insertedRead = readCaptor.getValue();
        assertThat(insertedRead.getRoomId()).isEqualTo(roomId);
        assertThat(insertedRead.getMemberId()).isEqualTo(senderMemberId);
        assertThat(insertedRead.getLastReadMessageId()).isEqualTo(100L);
    }

    
    
    @Test
    @DisplayName("빈 메시지를 전송하면 예외가 발생한다")
    void sendMessage_emptyContent_fail() {
        // given
        Long roomId = 10L;
        String senderMemberId = "jobseeker01";

        given(chatDAO.isChatRoomParticipant(roomId, senderMemberId))
                .willReturn(1);

        // when & then
        assertThatThrownBy(() -> chatService.sendMessage(roomId, senderMemberId, "   ", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("메시지 내용이 비어 있습니다.");

        then(chatDAO).should(never()).insertChatMessage(any(ChatMessageDTO.class));
    }

    
    
    @Test
    @DisplayName("채팅방 참여자가 아니면 메시지를 전송할 수 없다")
    void sendMessage_notParticipant_fail() {
        // given
        Long roomId = 10L;
        String senderMemberId = "other01";

        given(chatDAO.isChatRoomParticipant(roomId, senderMemberId))
                .willReturn(0);

        // when & then
        assertThatThrownBy(() -> chatService.sendMessage(roomId, senderMemberId, "안녕하세요", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("채팅방 접근 권한이 없습니다.");

        then(chatDAO).should(never()).insertChatMessage(any(ChatMessageDTO.class));
    }

    
    
    @Test
    @DisplayName("메시지 조회 시 mine과 readByOpponent 값을 계산하고 messageId 오름차순으로 정렬한다")
    void getChatMessages_success() {
        // given
        Long roomId = 10L;
        String memberId = "jobseeker01";

        given(chatDAO.isChatRoomParticipant(roomId, memberId))
                .willReturn(1);

        ChatMessageResponseDTO message2 = new ChatMessageResponseDTO();
        message2.setMessageId(2L);
        message2.setRoomId(roomId);
        message2.setSenderMemberId("company01");
        message2.setContent("답변입니다.");

        ChatMessageResponseDTO message1 = new ChatMessageResponseDTO();
        message1.setMessageId(1L);
        message1.setRoomId(roomId);
        message1.setSenderMemberId("jobseeker01");
        message1.setContent("문의드립니다.");
        message1.setOpponentLastReadMessageId(1L);

        List<ChatMessageResponseDTO> messages = new ArrayList<>();
        messages.add(message2);
        messages.add(message1);

        given(chatDAO.selectChatMessages(roomId, memberId, null, 30))
                .willReturn(messages);

        // when
        List<ChatMessageResponseDTO> result =
                chatService.getChatMessages(roomId, memberId, null, 0);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getMessageId()).isEqualTo(1L);
        assertThat(result.get(1).getMessageId()).isEqualTo(2L);

        assertThat(result.get(0).isMine()).isTrue();
        assertThat(result.get(0).isReadByOpponent()).isTrue();

        assertThat(result.get(1).isMine()).isFalse();
        assertThat(result.get(1).isReadByOpponent()).isFalse();
    }

    
    
    @Test
    @DisplayName("messageId가 null이면 채팅방의 마지막 메시지까지 읽음 처리한다")
    void markAsRead_nullMessageId_success() {
        // given
        Long roomId = 10L;
        String memberId = "company01";

        given(chatDAO.isChatRoomParticipant(roomId, memberId))
                .willReturn(1);

        given(chatDAO.selectLastMessageId(roomId))
                .willReturn(100L);

        given(chatDAO.selectChatRead(roomId, memberId))
                .willReturn(null);

        given(chatDAO.insertChatRead(any(ChatReadDTO.class)))
                .willReturn(1);

        // when
        int result = chatService.markAsRead(roomId, memberId, null);

        // then
        assertThat(result).isEqualTo(1);

        ArgumentCaptor<ChatReadDTO> captor = ArgumentCaptor.forClass(ChatReadDTO.class);
        then(chatDAO).should().insertChatRead(captor.capture());

        ChatReadDTO read = captor.getValue();
        assertThat(read.getRoomId()).isEqualTo(roomId);
        assertThat(read.getMemberId()).isEqualTo(memberId);
        assertThat(read.getLastReadMessageId()).isEqualTo(100L);
    }

    
    
    @Test
    @DisplayName("이미 더 최신 메시지를 읽은 상태라면 읽음 정보를 갱신하지 않는다")
    void markAsRead_alreadyRead_skipUpdate() {
        // given
        Long roomId = 10L;
        String memberId = "company01";

        given(chatDAO.isChatRoomParticipant(roomId, memberId))
                .willReturn(1);

        ChatReadDTO current = new ChatReadDTO();
        current.setRoomId(roomId);
        current.setMemberId(memberId);
        current.setLastReadMessageId(100L);

        given(chatDAO.selectChatRead(roomId, memberId))
                .willReturn(current);

        // when
        int result = chatService.markAsRead(roomId, memberId, 90L);

        // then
        assertThat(result).isEqualTo(0);
        then(chatDAO).should(never()).updateLastReadMessageId(any(ChatReadDTO.class));
    }
}