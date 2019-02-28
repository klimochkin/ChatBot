package vkbot.business;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;

import com.vk.api.sdk.objects.messages.responses.GetHistoryResponse;
import com.vk.api.sdk.queries.messages.MessagesSendQuery;
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
import vkbot.entity.Message;
import vkbot.entity.User;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;


@Service("ChatService")
public class ChatService {

    private static final Logger LOG = LoggerFactory.getLogger(LongPollService.class);

    @Autowired
    private LongPollService longPollService;

    public void sendMessage(Message msg) throws ClientException, ApiException {
        Random random = new Random();
        MessagesSendQuery msgSend = InitBot.vk.messages().send(InitBot.actor)
                .peerId(msg.getPeerId())
                .message(msg.getText())
                .attachment(msg.getAttachment())
                .randomId(random.nextInt());

        if (msg.isForward())
            msgSend.forwardMessages(msg.getMessageId().toString()).execute();
        else
            msgSend.execute();

        LOG.debug("Ответ: " + msg.getText() + " Вложение: " + msg.getAttachment());
    }

    public List<User> getChatUser(String peer_id) throws IOException, ParseException, ClientException, ApiException {

        Integer chatId = Integer.parseInt(peer_id);
        List<User> users = new ArrayList();

        String jsonStr = InitBot.vk.messages().getChat(InitBot.actor).chatId(chatId - 2000000000).unsafeParam("fields", "online").executeAsString();
        LOG.debug(jsonStr);

        JSONObject commentsJSON = (JSONObject) new JSONParser().parse(jsonStr);
        JSONObject response = (JSONObject) commentsJSON.get("response");
        if (response != null) {
            JSONArray usersJson = (JSONArray) response.get("users");
            for (int i = 0; i < usersJson.size(); i++) {
                JSONObject userJson = (JSONObject) usersJson.get(i);
                User user = new User(userJson);
                users.add(user);
            }
        } else
            throw new RuntimeException("Неудалось получить список юзеров");

        return users;
    }

    public Map<String, Integer> statisticChatMessage(Integer peer_id) {

        Map<String, Integer> statisticMessages = new HashMap<>();
        Map<Integer, Integer> statisticCharacter = new HashMap<>();
        Integer N = 1;
        Integer chatId = peer_id;
        Integer count;
        Integer countMessage;
        Integer countCharacter;

        List<com.vk.api.sdk.objects.messages.Message> listMessage;
        try {

            int i = 0;
            int offset = 0;
            while (i < N) {
                GetHistoryResponse historyResponse = InitBot.vk.messages().getHistory(InitBot.actor)
                        .peerId(chatId)
                        .count(200)
                        .offset(offset)
                        .rev(false)
                        .execute();

                if (i == 0) {
                    count = historyResponse.getCount();
                    if (count > 400)
                        N = count / 200;
                }
                listMessage = historyResponse.getItems();
                for (com.vk.api.sdk.objects.messages.Message item : listMessage) {
                    //   countCharacter = statisticCharacter.get(item.getUserId());
                    //   Integer usUd = item.getUserId();
                    countMessage = statisticMessages.get(item.getUserId().toString());
                    //   Object obj = statisticMessages.get(usUd);
                    //   countMessage = statisticMessages.get("228900194");
/*                    if (countCharacter == null) {
                        statisticCharacter.put(item.getUserId(), item.getActionText().length());
                    } else {
                        statisticCharacter.put(item.getUserId(), countCharacter + item.getActionText().length());
                    }*/
                    if (countMessage == null) {
                        countMessage = 1;
                        statisticMessages.put(item.getUserId().toString(), countMessage);
                    } else {
                        countMessage = countMessage + 1;
                        statisticMessages.put(item.getUserId().toString(), countMessage);
                    }
                }
                TimeUnit.MILLISECONDS.sleep(300);
                offset += 200;
                i++;
            }
        } catch (ApiException | ClientException | InterruptedException e) {
            e.printStackTrace();
        }

        return statisticMessages;
    }

    public List<Message> getChatMessages(Integer peer_id) {
        //размер диалога с шагом в 200 смс
        Integer N = 1;

        Integer count;
        List<Message> chatMessages = new ArrayList<>();
        List<com.vk.api.sdk.objects.messages.Message> listMessage;
        try {
            int i = 0;
            int offset = 0;
            while (i < N) {
                GetHistoryResponse historyResponse = InitBot.vk.messages().getHistory(InitBot.actor)
                        .peerId(peer_id)
                        .count(200)
                        .offset(offset)
                        .rev(false)
                        .execute();

                if (i == 0) {
                    count = historyResponse.getCount();
                    if (count > 400)
                        N = count / 200;
                }
                listMessage = historyResponse.getItems();
                for (com.vk.api.sdk.objects.messages.Message msg : listMessage) {
                    Message message = new Message(msg);
                    message.setPeerId(peer_id);
                    chatMessages.add(message);
                }
                TimeUnit.MILLISECONDS.sleep(300);
                offset += 200;
                i++;
            }
        } catch (ApiException | ClientException | InterruptedException e) {
            e.printStackTrace();
        }
        return chatMessages;
    }

        public void startCycleForChat() {
        try {
            longPollService.cycle();
        } catch (JSONException | ParseException | IOException | ApiException | ClientException | InterruptedException e) {
            e.printStackTrace();
        }
        }

    @PostConstruct
    public void test() {
        LOG.debug("Создан бин ChatService");
    }

}
