package vkbot;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import org.json.JSONException;
import org.json.simple.parser.ParseException;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import vkbot.business.ManagerService;


@SpringBootApplication
@Configuration
public class VkBot {

    public static void main(String[] args) throws Exception {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(VkBot.class);
        ManagerService managerBusinessService = ctx.getBean(ManagerService.class);
        managerBusinessService.startBot();
    }
}
