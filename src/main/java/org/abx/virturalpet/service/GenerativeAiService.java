package org.abx.virturalpet.service;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class GenerativeAiService {

    public static final float TEMPERATURE = 0.1f;
    public static final String MODEL = "gpt-4-o";
    private final OpenAiChatModel chatModel;
    private final OpenAiImageModel openaiImageModel;

    public GenerativeAiService(OpenAiChatModel chatModel, OpenAiImageModel openaiImageModel) {
        this.chatModel = chatModel;
        this.openaiImageModel = openaiImageModel;
    }

    public ChatResponse generateResponse(String userInput) {
        return chatModel.call(preparePrompt(userInput));
    }

    // stream response
    public Flux<ChatResponse> generateStreamResponse(String userInput) {
        return chatModel.stream(preparePrompt(userInput));
    }

    public ImageResponse generateImage(String jobType, String photoUrl) {
        return openaiImageModel.call(prepareImagePrompt(jobType, photoUrl));
    }

    private static ImagePrompt prepareImagePrompt(String jobType, String photoUrl) {
        String userInput;
        // Validate inputs
        if (photoUrl == null || photoUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("Image URL cannot be blank or null.");
        }
        if (jobType == null || jobType.trim().isEmpty()) {
            throw new IllegalArgumentException("Operation type cannot be blank or null.");
        }

        // Create a prompt based on the operation type
        switch (jobType.toLowerCase()) {
            case "enhance":
                userInput = "Enhance the image: " + photoUrl;
                break;
            case "stylize":
                userInput = "Stylize the image in a cartoon style: " + photoUrl;
                break;
            default:
                throw new IllegalArgumentException("Unsupported operation type: " + jobType);
        }
        return new ImagePrompt(
                userInput,
                OpenAiImageOptions.builder()
                        .withQuality("hd")
                        .withN(4)
                        .withHeight(1024)
                        .withWidth(1024)
                        .build());
    }

    private static Prompt preparePrompt(String userInput) {
        // TODO: prompt template, validation, etc.
        return new Prompt(
                userInput,
                OpenAiChatOptions.builder()
                        .withModel(MODEL)
                        .withTemperature(TEMPERATURE)
                        .build());
    }
}
