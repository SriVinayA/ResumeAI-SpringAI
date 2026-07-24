import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.ai.deepseek.api.DeepSeekApi;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.messages.UserMessage;
import java.util.List;

public class scratch2 {
    public static void main(String[] args) throws Exception {
        String apiKey = System.getenv("DEEPSEEK_API_KEY");
        if (apiKey == null) { apiKey = "dummy"; }
        DeepSeekApi deepSeekApi = DeepSeekApi.builder().apiKey(apiKey).build();
        DeepSeekChatModel chatModel = DeepSeekChatModel.builder()
                .deepSeekApi(deepSeekApi)
                .options(DeepSeekChatOptions.builder().model("deepseek-chat").build())
                .build();
        System.out.println("Calling deepseek...");
        try {
            System.out.println(chatModel.call(new Prompt(new UserMessage("Hello!"))));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
