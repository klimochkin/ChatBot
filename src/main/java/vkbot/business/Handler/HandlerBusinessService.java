package vkbot.business.Handler;


import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import org.json.simple.parser.ParseException;
import vkbot.entity.Comment;
import vkbot.entity.Message;

import java.io.IOException;

public interface HandlerBusinessService {

    void handleMessages(Message msg) throws ClientException, ApiException, IOException, ParseException;

    void handleComments(Comment msg) throws IOException, ApiException, ParseException, ClientException;


}
