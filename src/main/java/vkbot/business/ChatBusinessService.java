package vkbot.business;

import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import org.springframework.stereotype.Service;
import vkbot.entity.Message;


@Service("ChatBusinessService")
public interface ChatBusinessService {

    void sendMessage(Message message) throws ClientException, ApiException;

}
