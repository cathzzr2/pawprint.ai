package org.abx.virturalpet.sqs;

import software.amazon.awssdk.services.sqs.model.Message;

public interface MessageProcessor {
    void processMessage(Message message);
}
