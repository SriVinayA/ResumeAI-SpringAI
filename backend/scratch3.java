import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.messages.UserMessage;

public class scratch3 {
    public static void main(String[] args) throws Exception {
        OpenAiChatModel chatModel = OpenAiChatModel.builder()
            .options(OpenAiChatOptions.builder()
                .apiKey("sk-invalid")
                .baseUrl("https://api.deepseek.com")
                .model("deepseek-chat")
                .build())
            .build();
        try {
            System.out.println(chatModel.call(new Prompt(new UserMessage("Hello!"))));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
