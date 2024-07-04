package org.abx.virturalpet.service.sqs;

public interface SqsProducerInterface<QueueMessage> {
    void sendMessage(QueueMessage message);
}
