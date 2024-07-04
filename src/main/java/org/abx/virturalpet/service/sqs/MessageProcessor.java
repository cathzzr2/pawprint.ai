package org.abx.virturalpet.service.sqs;

import software.amazon.awssdk.services.sqs.model.Message;

public interface MessageProcessor {
    void processMessage(Message message);
}
