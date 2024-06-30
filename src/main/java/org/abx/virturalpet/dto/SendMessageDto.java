package org.abx.virturalpet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.UUID;

@Value.Immutable
@JsonSerialize(as = ImmutableSendMessageDto.class)
@JsonDeserialize(as = ImmutableSendMessageDto.class)
public interface SendMessageDto {
    @JsonProperty("user_id")
    @Value.Default
    default UUID getUserId() {
        return UUID.randomUUID();
    }

    @JsonProperty("thread_id")
    @Value.Default
    default UUID getThreadId() {
        return UUID.randomUUID();
    }

    @JsonProperty("message_content")
    @Value.Default
    default String getMessageContent() {
        return "";
    }

    @JsonProperty("timestamp")
    @Value.Default
    default String getTimestamp() {
        return "";
    }

//    @JsonProperty("message_id")
//    @Value.Default
//    default int getMessageId() {
//        return 0;
//    }

    @JsonProperty("status_code")
    @Value.Default
    default int getStatusCode() {
        return 0;
    }

    @JsonProperty("status")
    @Value.Default
    default String getStatus() {
        return "";
    }

    static ImmutableSendMessageDto.Builder builder() {
        return ImmutableSendMessageDto.builder();
    }
}
