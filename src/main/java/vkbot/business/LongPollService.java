package vkbot.business;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.vk.api.sdk.callback.longpoll.queries.GetLongPollEventsQuery;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.messages.LongpollParams;
import com.vk.api.sdk.queries.messages.MessagesGetLongPollServerQuery;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vkbot.access.InitBot;
import vkbot.business.Handler.HandlerService;
import vkbot.entity.LongPollResponse;
import vkbot.entity.Message;
import vkbot.enums.SourceTypeEnum;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;


@Service("LongPollService")
public class LongPollService {

    static final Logger LOG = LoggerFactory.getLogger(LongPollService.class);

    LongpollParams LongPollParams;

    @Autowired
    private HandlerService handlerService;

    private void init() throws ClientException, ApiException {
        MessagesGetLongPollServerQuery longPollServer = InitBot.vk.messages().getLongPollServer(InitBot.actor).lpVersion(3);
        longPollServer.needPts(true);
        LongPollParams = longPollServer.execute();
    }

    public void cycle() throws ClientException, ApiException, ParseException, JSONException, IOException, InterruptedException {
        init();
        GetLongPollEventsQuery events;
        int ts = LongPollParams.getTs();

        while (true) {
            events = InitBot.vk.longPoll().getEvents("https://" + LongPollParams.getServer(), LongPollParams.getKey(), ts);
            events.waitTime(25);
            events.unsafeParam("mode", 2);
            LongPollResponse response = castomExecute(events);
            if (response != null) {
                if (response.getUpdates().size() != 0)
                    processEvent(response.getUpdates());
                ts = response.getTs();

                LOG.debug("Последнее событие: " + response.getTs().toString());
            } else {
                LOG.debug("Протухание ключа LongPoll! Попытка повторного получения...");
                init();
                ts = LongPollParams.getTs();
            }
        }
    }

    private void processEvent(List<JsonArray> updates) throws ClientException, ApiException, IOException, ParseException, InterruptedException {
        for (JsonArray arrayItem : updates) {

            int code = arrayItem.get(0).getAsInt();
            //       int uid;
            switch (code) {
                case 4:
                    Message message = new Message();
                    message.setSourceType(SourceTypeEnum.CHAT);
                    message.setMessageId(Long.parseLong(arrayItem.get(1).toString()));
                    message.setFlags(Integer.parseInt(arrayItem.get(2).toString()));
                    message.setPeerId(Integer.parseInt(arrayItem.get(3).toString()));
                    message.setTs(Long.parseLong(arrayItem.get(4).toString()));
                    message.setSubject(arrayItem.get(5).toString());
                    message.setText(arrayItem.get(6).toString().toLowerCase().replace("\"", ""));
                    message.setUserId(0L);
/*                    LOG.debug("++++++++++++++++++++");
                    LOG.debug(message.getText());
                    LOG.debug("++++++++++++++++++++");
                    LOG.debug(updates.toString());
                    LOG.debug(arrayItem.toString());
                    LOG.debug("++++++++++++++++++++");*/
                    try {
                        JSONObject object = new JSONObject(arrayItem.get(7).toString());
                        message.setUserId(Long.parseLong(object.getString("from")));
                    } catch (Exception ignored) {
                    }
                    if (message.getPeerId() == 2000000016 || message.getPeerId() == 2000000003)
                        break;

                    handlerService.handleMessages(message);
                    break;
            }
        }
    }

    public LongPollResponse castomExecute(GetLongPollEventsQuery events) throws ApiException, ClientException, ParseException, JSONException {
        String textResponse = events.executeAsString();
        JsonReader jsonReader = new JsonReader(new StringReader(textResponse));
        JsonObject json = (JsonObject) (new JsonParser()).parse(jsonReader);
        if (json.has("failed")) {
            JsonPrimitive e = json.getAsJsonPrimitive("failed");
            int code = e.getAsInt();
            switch (code) {
                case 1:
                    int ts = json.getAsJsonPrimitive("ts").getAsInt();
                    LOG.error("\'ts\' value is incorrect, minimal value is 1, maximal value is " + ts);
                    return null;
                case 2:
                    return null;
                default:
                    throw new ClientException("Unknown LongPollServer exception, something went wrong.");
            }
        } else {
            Gson gson = new Gson();
            try {
                return gson.fromJson(json, LongPollResponse.class);
            } catch (JsonSyntaxException var7) {
                LOG.error("Invalid JSON: " + textResponse, var7);
                throw new ClientException("Can\'t parse json response");
            }
        }
    }


    @PostConstruct
    public void postConstruct() {
        LOG.debug("Создан бин LongPollService");
    }

}
