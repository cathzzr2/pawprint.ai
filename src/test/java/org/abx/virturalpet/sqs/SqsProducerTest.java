package org.abx.virturalpet.sqs;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.abx.virturalpet.dto.ImageGenSqsDto;
import org.abx.virturalpet.dto.ImmutableImageGenSqsDto;
import org.abx.virturalpet.exception.SqsProducerException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlResponse;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

@ExtendWith(MockitoExtension.class)
public class SqsProducerTest {

    @Mock
    private SqsClient sqsClient;

    private ImageGenSqsProducer imageGenSqsProducer;

    private final String queueName = "testQueue";
    private final String queueUrl = "testQueueUrl";

    @BeforeEach
    void beforeEach() {
        // Mock the GetQueueUrlResponse to return a valid queue URL
        GetQueueUrlResponse getQueueUrlResponse =
                GetQueueUrlResponse.builder().queueUrl(queueUrl).build();
        when(sqsClient.getQueueUrl(ArgumentMatchers.any(GetQueueUrlRequest.class)))
                .thenReturn(getQueueUrlResponse);

        // Initialize ImageGenSqsProducer with mocked dependencies
        imageGenSqsProducer = new ImageGenSqsProducer(sqsClient, queueName);
    }

    @Test
    void sendMessage_shouldSendSuccessfully() {
        ImageGenSqsDto imageGenSqsDto = ImmutableImageGenSqsDto.builder()
                .jobId("testJobId")
                .photoId("testPhotoId")
                .build();

        SendMessageResponse sendMessageResponse = SendMessageResponse.builder().build();
        when(sqsClient.sendMessage(ArgumentMatchers.any(SendMessageRequest.class)))
                .thenReturn(sendMessageResponse);

        imageGenSqsProducer.sendMessage(imageGenSqsDto);

        ArgumentCaptor<SendMessageRequest> sendMessageRequestCaptor = ArgumentCaptor.forClass(SendMessageRequest.class);
        verify(sqsClient).sendMessage(sendMessageRequestCaptor.capture());
        SendMessageRequest capturedRequest = sendMessageRequestCaptor.getValue();

        Assertions.assertEquals(queueUrl, capturedRequest.queueUrl());
        Assertions.assertEquals(imageGenSqsDto.toString(), capturedRequest.messageBody());
        Assertions.assertEquals(5, capturedRequest.delaySeconds());
    }

    @Test
    void sendMessage_shouldLogErrorAndThrowExceptionWhenSendingFails() {
        ImageGenSqsDto imageGenSqsDto = ImmutableImageGenSqsDto.builder()
                .jobId("testJobId")
                .photoId("testPhotoId")
                .build();

        RuntimeException exception = new RuntimeException("SQS error");
        when(sqsClient.sendMessage(ArgumentMatchers.any(SendMessageRequest.class)))
                .thenThrow(exception);

        SqsProducerException thrownException = Assertions.assertThrows(SqsProducerException.class, () -> {
            imageGenSqsProducer.sendMessage(imageGenSqsDto);
        });

        Assertions.assertEquals("Error sending message to SQS", thrownException.getMessage());
        Assertions.assertEquals(exception, thrownException.getCause());
    }
}
