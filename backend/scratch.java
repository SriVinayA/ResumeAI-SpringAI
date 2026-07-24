import org.springframework.ai.deepseek.api.DeepSeekApi;
import java.lang.reflect.Field;
public class scratch {
    public static void main(String[] args) throws Exception {
        System.out.println("hello");
        for (Field f : DeepSeekApi.class.getDeclaredFields()) {
            if (f.getType().equals(String.class)) {
                f.setAccessible(true);
                System.out.println(f.getName() + " = " + f.get(null));
            }
        }
    }
}
