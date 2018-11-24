package vkbot.business;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import org.json.JSONException;
import org.json.simple.parser.ParseException;
import vkbot.entity.DataBot;

import java.io.IOException;

public interface ManagerBusinessService {

    public void startBot() throws ClientException, ApiException, ParseException, JSONException, IOException;

}
