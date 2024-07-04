package org.abx.virturalpet.service.sqs;

import software.amazon.awssdk.services.sqs.model.Message;

public class GenImageSqsMessageProcessor implements MessageProcessor {

    @Override
    public void processMessage(Message message) {}
}
