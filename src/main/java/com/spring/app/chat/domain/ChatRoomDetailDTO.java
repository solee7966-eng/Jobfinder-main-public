package com.spring.app.chat.domain;

import lombok.Data;

@Data
public class ChatRoomDetailDTO {

    private Long roomId;
    private Long applicationId;
    private Long jobId;
    private String jobTitle;

    private String opponentId;
    private String opponentName;
    private String opponentProfile;

    private String roomType;
}