package vkbot;

import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import org.json.JSONException;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import vkbot.business.ManagerBusinessService;
import vkbot.business.impl.ManagerBusinessServiceImpl;


@SpringBootApplication
@Configuration
public class VkBot {

   // @Autowired
   // ManagerBusinessService managerBusinessService;

    public static void main(String[] args) throws Exception {
       // SpringApplication.run(VkBot.class, args);
       // VkBot bot = new VkBot();
     //   bot.run();
        ApplicationContext ctx = new AnnotationConfigApplicationContext(VkBot.class);
        ManagerBusinessService managerBusinessService = ctx.getBean(ManagerBusinessService.class);
        managerBusinessService.startBot();
        //SpringApplication.main(args);
       // managerBusinessService.startBot();
   //     ManagerBusinessService managerBusinessService = new ManagerBusinessServiceImpl();
        //      ManagerBusinessService managerBusinessService = new ManagerBusinessServiceImpl();
        //       InitApplication initApplication = managerBusinessService.Init();
//        UserActor actor = new UserActor(512877743, initApplication.getVkToken()[0]);
   //     RestTemplate restTemplate = new RestTemplate();

        //Page page = restTemplate.getForObject("http://graph.facebook.com/pivotalsoftware", Page.class);
    }

    void myrun() throws JSONException, ApiException, ParseException, ClientException {

     //   managerBusinessService.startBot();
    }
}
