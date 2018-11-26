package vkbot.business.impl;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;

import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vkbot.access.InitBot;
import vkbot.business.ChatBusinessService;
import vkbot.business.LongPollBusinessService;
import vkbot.entity.Message;
import vkbot.entity.User;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


@Service("ChatBusinessServiceImpl")
public class ChatBusinessServiceImpl implements ChatBusinessService {

    private static final Logger LOG = LoggerFactory.getLogger(LongPollBusinessServiceImpl.class);

    @Autowired
    private LongPollBusinessService longPollBusinessService;


    // public static void sendMessage(String peerId, String message, String attachment) {
    @Override
    public void sendMessage(Message message) throws ClientException, ApiException {
        Random random = new Random();
        InitBot.vk.messages().send(InitBot.actor)
                .peerId(message.getPeerId())
                .message(message.getText())
                .attachment(message.getAttachment())
                .randomId(random.nextInt())
                .execute();

        LOG.debug("Ответ: " + message.getText() + " Вложение: " + message.getAttachment());
    }

    public  List<User> getChatUser(String peer_id) throws IOException, ParseException, ClientException, ApiException {

        Integer chatId = Integer.parseInt(peer_id);
        StringBuilder requestURL = new StringBuilder();
         List<User> users = new ArrayList();

        String jsonStr =InitBot.vk.messages().getChat(InitBot.actor).chatId(chatId - 2000000000).unsafeParam("fields","online").executeAsString();
        LOG.debug(jsonStr);
     //   String strd = InitBot.vk.messages().getChatUsers(InitBot.actor).chatId(chatId - 2000000000).unsafeParam("fields","online").executeAsString();
     //   LOG.debug(strd.toString());

        JSONObject commentsJSON = (JSONObject) new JSONParser().parse(jsonStr);
        JSONObject response = (JSONObject) commentsJSON.get("response");
        if (response != null) {
            JSONArray usersJson = (JSONArray) response.get("users");
            for (int i = 0; i < usersJson.size(); i++) {
                JSONObject userJson = (JSONObject) usersJson.get(i);
                User user = new User(userJson);
                users.add(user);
            }
        }
        else
            throw new RuntimeException ("Неудалось получить список юзеров");

        return users;
    }

    public void startCycleForChat()  {
        try {

            longPollBusinessService.cycle();

        } catch (JSONException | ParseException | IOException | ApiException | ClientException e) {
            e.printStackTrace();
        }


    }

    @PostConstruct
    public void test() {
        LOG.debug("Создан бин ChatBusinessServiceImpl");
    }

}
