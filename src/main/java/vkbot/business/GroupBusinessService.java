package vkbot.business;


import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import org.json.simple.parser.ParseException;
import vkbot.entity.Comment;
import vkbot.entity.User;

import java.util.List;


public interface GroupBusinessService {

    void sendComment(Comment comment) throws ClientException, ApiException;

    void startCycleForGroups();

    List<User> getUserTopic(Integer groupId, Integer topicId) throws ClientException, ParseException;

}
