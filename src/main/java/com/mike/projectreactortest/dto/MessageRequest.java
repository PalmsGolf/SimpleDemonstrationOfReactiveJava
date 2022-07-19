package com.mike.projectreactortest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

@JsonPropertyOrder({
        "messageId",
        "message"
})
@Getter @Setter
public class MessageRequest {

    @JsonProperty("messageId")
    private String messageId;

    @JsonProperty("message")
    private String message;

}