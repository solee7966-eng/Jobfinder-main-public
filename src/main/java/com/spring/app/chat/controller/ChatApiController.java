package com.spring.app.chat.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.spring.app.chat.domain.ChatMessageResponseDTO;
import com.spring.app.chat.domain.ChatMessageSendRequest;
import com.spring.app.chat.domain.ChatReadRequest;
import com.spring.app.chat.domain.ChatRoomCreateRequest;
import com.spring.app.chat.domain.ChatRoomListDTO;
import com.spring.app.chat.service.ChatService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(
    name = "Chat API",
    description = """
            지원(application) 기반 1:1 채팅 API.
            Swagger에서는 채팅방 생성 → 채팅방 목록 조회 → 메시지 전송 → 메시지 조회 → 읽음 처리 순서로 테스트하는 것을 권장합니다.
            """
)
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatApiController {

    private static final String KEY_RESULT = "result";
    private static final String KEY_ROOM_ID = "roomId";

    private final ChatService chatService;

    @Operation(
        summary = "채팅방 생성 또는 기존 채팅방 조회",
        description = """
                지원 관계가 있는 경우에만 채팅방을 생성하거나 기존 채팅방을 반환합니다.
                동일한 applicationId에 대해 이미 생성된 채팅방이 있으면 기존 roomId를 재사용합니다.

                Swagger 테스트 예시:
                1. applicationId를 넣어 호출
                2. 응답으로 받은 roomId를 이후 메시지 전송/조회 API에 사용
                """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "채팅방 생성 또는 기존 채팅방 조회 성공",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(
                    name = "success",
                    value = """
                            {
                              "result": "ok",
                              "roomId": 3
                            }
                            """
                )
            )
        ),
        @ApiResponse(responseCode = "400", description = "잘못된 요청값 또는 유효하지 않은 지원 정보"),
        @ApiResponse(responseCode = "401", description = "로그인되지 않은 사용자"),
        @ApiResponse(responseCode = "403", description = "채팅방 생성 권한 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping("/rooms")
    public ResponseEntity<Map<String, Object>> createOrGetRoom(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "채팅방 생성 요청 정보",
                required = true,
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ChatRoomCreateRequest.class),
                    examples = @ExampleObject(
                        name = "createRoomRequest",
                        value = """
                                {
                                  "applicationId": 1
                                }
                                """
                    )
                )
            )
            @RequestBody ChatRoomCreateRequest request,
            Principal principal) {

        String memberId = principal.getName();
        Long roomId = chatService.getOrCreateChatRoom(request.getApplicationId(), memberId);

        Map<String, Object> result = new HashMap<>();
        result.put(KEY_RESULT, "ok");
        result.put(KEY_ROOM_ID, roomId);

        return ResponseEntity.ok(result);
    }

    
    
    @Operation(
        summary = "내 채팅방 목록 조회",
        description = """
                로그인한 사용자의 채팅방 목록을 조회합니다.
                마지막 메시지, 마지막 메시지 시각, 안 읽은 메시지 수를 함께 반환합니다.

                Swagger 테스트 예시:
                - 채팅방 생성 후 바로 호출하면 목록 반영 여부를 확인할 수 있습니다.
                - 메시지 전송 후 다시 호출하면 lastMessage, unreadCount 변화를 확인할 수 있습니다.
                """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "채팅방 목록 조회 성공",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                array = @ArraySchema(schema = @Schema(implementation = ChatRoomListDTO.class)),
                examples = @ExampleObject(
                    name = "roomListSuccess",
                    value = """
                            [
                              {
                                "roomId": 3,
                                "applicationId": 1,
                                "opponentId": "company01",
                                "opponentName": "테스트기업",
                                "opponentProfile": null,
                                "lastMessage": "안녕하세요. 채팅 테스트입니다.",
                                "lastMessageAt": "2026.04.23 21:10",
                                "unreadCount": 0
                              }
                            ]
                            """
                )
            )
        ),
        @ApiResponse(responseCode = "401", description = "로그인되지 않은 사용자"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoomListDTO>> getChatRooms(Principal principal) {
        String memberId = principal.getName();
        return ResponseEntity.ok(chatService.getChatRoomList(memberId));
    }

    
    
    
    
    /**
     * 로그인 사용자의 전체 안 읽은 채팅 메시지 수를 조회한다.
     */
    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Object>> getTotalUnreadChatCount(Principal principal) {

        String memberId = principal.getName();

        int unreadCount = chatService.getTotalUnreadChatCount(memberId);

        Map<String, Object> result = new HashMap<>();
        result.put("unreadCount", unreadCount);

        return ResponseEntity.ok(result);
    }
    
    
    
    
    
    @Operation(
	    summary = "채팅 메시지 목록 조회",
	    description = """
	            특정 채팅방의 메시지 목록을 조회합니다.
	            최초 조회 시에는 최신 메시지 묶음을 조회하고,
	            lastMessageId를 전달하면 해당 메시지 ID보다 이전 메시지를 페이징 조회합니다.

	            페이징 기준:
	            - lastMessageId 없음: 최신 메시지 size건 조회
	            - lastMessageId 있음: messageId < lastMessageId 조건으로 이전 메시지 size건 조회

	            Swagger 테스트 예시:
	            1. roomId만 넣고 호출하여 최신 메시지 목록 조회
	            2. 응답 목록의 가장 작은 messageId를 확인
	            3. 해당 messageId를 lastMessageId로 넣고 다시 호출
	            4. 이전 메시지가 추가 조회되는지 확인
	            """
	)
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "메시지 목록 조회 성공",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                array = @ArraySchema(schema = @Schema(implementation = ChatMessageResponseDTO.class)),
        		examples = @ExampleObject(
    			    name = "messageListSuccess",
    			    value = """
    			            [
    			              {
    			                "messageId": 10,
    			                "roomId": 3,
    			                "senderMemberId": "member01",
    			                "content": "안녕하세요. 채팅 테스트입니다.",
    			                "createdAt": "2026.04.23 21:10:11",
    			                "mine": true,
    			                "opponentLastReadMessageId": 10,
    			                "readByOpponent": true
    			              }
    			            ]
    			            """
    			)
            )
        ),
        @ApiResponse(responseCode = "400", description = "잘못된 roomId 또는 요청 파라미터"),
        @ApiResponse(responseCode = "401", description = "로그인되지 않은 사용자"),
        @ApiResponse(responseCode = "403", description = "채팅방 접근 권한 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<List<ChatMessageResponseDTO>> getMessages(
            @Parameter(
                name = "roomId",
                description = "조회할 채팅방 ID",
                required = true,
                in = ParameterIn.PATH,
                example = "3"
            )
            @PathVariable("roomId") Long roomId,

            @Parameter(
                name = "lastMessageId",
                description = "이 값보다 작은 messageId만 조회합니다. 이전 메시지 페이징용입니다.",
                in = ParameterIn.QUERY,
                example = "100"
            )
            @RequestParam(value = "lastMessageId", required = false) Long lastMessageId,

            @Parameter(
        	    name = "size",
        	    description = "조회 건수. 기본값은 30이며, 서버에서 최대 50건까지 허용합니다.",
        	    in = ParameterIn.QUERY,
        	    example = "30"
        	)
        	@RequestParam(value = "size", defaultValue = "30") int size,

            Principal principal) {

        String memberId = principal.getName();
        return ResponseEntity.ok(chatService.getChatMessages(roomId, memberId, lastMessageId, size));
    }

    
    
    @Operation(
        summary = "메시지 전송",
        description = """
                REST 방식으로 메시지를 저장합니다.
                WebSocket 연결 전, 백엔드 저장 로직과 DB 반영 여부를 먼저 검증할 때 유용합니다.

                Swagger 테스트 예시:
                1. 채팅방 생성 후 roomId 확보
                2. roomId에 메시지 전송
                3. 메시지 목록 조회 API로 저장 결과 확인
                """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "메시지 전송 성공",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ChatMessageResponseDTO.class),
                examples = @ExampleObject(
                    name = "sendMessageSuccess",
                    value = """
                            {
                              "messageId": 10,
                              "roomId": 3,
                              "senderMemberId": "member01",
                              "content": "안녕하세요. 채팅 테스트입니다.",
                              "createdAt": "2026.04.23 21:10:11",
                              "mine": true
                            }
                            """
                )
            )
        ),
        @ApiResponse(responseCode = "400", description = "빈 메시지 또는 메시지 길이 초과"),
        @ApiResponse(responseCode = "401", description = "로그인되지 않은 사용자"),
        @ApiResponse(responseCode = "403", description = "채팅방 접근 권한 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping("/rooms/{roomId}/messages")
    public ResponseEntity<ChatMessageResponseDTO> sendMessage(
            @Parameter(
                name = "roomId",
                description = "메시지를 전송할 채팅방 ID",
                required = true,
                in = ParameterIn.PATH,
                example = "3"
            )
            @PathVariable("roomId") Long roomId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "메시지 전송 요청 정보",
                required = true,
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ChatMessageSendRequest.class),
                    examples = @ExampleObject(
                        name = "sendMessageRequest",
                        value = """
                                {
                                  "content": "안녕하세요. 채팅 테스트입니다.",
                                  "messageType": "TEXT"
                                }
                                """
                    )
                )
            )
            @RequestBody ChatMessageSendRequest request,
            Principal principal) {

        String memberId = principal.getName();

        ChatMessageResponseDTO response =
                chatService.sendMessage(roomId, memberId, request.getContent(), request.getMessageType());

        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "읽음 처리",
        description = """
                현재 사용자의 마지막 읽은 메시지 ID를 갱신합니다.
                messageId를 주면 해당 메시지까지 읽음 처리하고,
                null이면 해당 채팅방의 최신 메시지까지 읽음 처리합니다.

                Swagger 테스트 예시:
                - 특정 메시지까지 읽음 처리: { "messageId": 10 }
                - 최신 메시지까지 읽음 처리: { "messageId": null }
                """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "읽음 처리 성공",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                examples = @ExampleObject(
                    name = "readSuccess",
                    value = """
                            {
                              "result": "ok"
                            }
                            """
                )
            )
        ),
        @ApiResponse(responseCode = "400", description = "잘못된 요청값"),
        @ApiResponse(responseCode = "401", description = "로그인되지 않은 사용자"),
        @ApiResponse(responseCode = "403", description = "채팅방 접근 권한 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping("/rooms/{roomId}/read")
    public ResponseEntity<Map<String, Object>> markAsRead(
            @Parameter(
                name = "roomId",
                description = "읽음 처리할 채팅방 ID",
                required = true,
                in = ParameterIn.PATH,
                example = "3"
            )
            @PathVariable("roomId") Long roomId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "읽음 처리 요청 정보. messageId가 null이면 최신 메시지까지 읽음 처리합니다.",
                required = false,
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ChatReadRequest.class),
                    examples = {
                        @ExampleObject(
                            name = "readSpecificMessage",
                            value = """
                                    {
                                      "messageId": 10
                                    }
                                    """
                        ),
                        @ExampleObject(
                            name = "readLatestMessage",
                            value = """
                                    {
                                      "messageId": null
                                    }
                                    """
                        )
                    }
                )
            )
            @RequestBody(required = false) ChatReadRequest request,
            Principal principal) {

        String memberId = principal.getName();
        Long messageId = (request == null ? null : request.getMessageId());

        int n = chatService.markAsRead(roomId, memberId, messageId);

        Map<String, Object> result = new HashMap<>();
        result.put(KEY_RESULT, n >= 0 ? "ok" : "fail");

        return ResponseEntity.ok(result);
    }
}