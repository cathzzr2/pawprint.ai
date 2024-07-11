package org.abx.virturalpet.sqs;

public interface SqsProducerInterface<QueueMessage> {
    void sendMessage(QueueMessage message);
}
