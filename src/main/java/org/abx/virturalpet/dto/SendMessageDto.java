package org.abx.virturalpet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableSendMessageDto.class)
@JsonDeserialize(as = ImmutableSendMessageDto.class)
public interface SendMessageDto {
    @JsonProperty("user_id")
    @Value.Default
    default int getUserId() {
        return 0;
    }

    @JsonProperty("thread_id")
    @Value.Default
    default int getThreadId() {
        return 0;
    }

    @JsonProperty("message_content")
    @Value.Default
    default String getMessageContent() {
        return "";
    }

    @JsonProperty("message_id")
    @Value.Default
    default int getMessageId() {
        return 0;
    }

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
