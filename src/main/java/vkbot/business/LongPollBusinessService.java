package vkbot.business;


import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import org.json.JSONException;
import org.json.simple.parser.ParseException;

import java.io.IOException;


public interface LongPollBusinessService {

    //   LongpollParams getLongPollServer(UserActor actor) throws ClientException, ApiException;
    void cycle() throws ClientException, ApiException, ParseException, JSONException, IOException;
}
